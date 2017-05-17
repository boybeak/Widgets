package com.nulldreams.demo.widgets;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;

import com.nulldreams.base.manager.AbsManager;

import java.io.IOException;

/**
 * Created by Beak on 2015/8/7.
 */
public class VoiceManager extends AbsManager implements AudioManager.OnAudioFocusChangeListener{

    private static final String TAG = VoiceManager.class.getSimpleName();

    private static final int MAX_RECORD_DURATION = 60 * 1000;

    private static VoiceManager sManager = null;

    public static synchronized VoiceManager getInstance (Context context) {
        if (sManager == null) {
            sManager = new VoiceManager(context.getApplicationContext());
        }
        return sManager;
    }

    private AudioManager mAudioManager = null;

    private MediaRecorder mRecorder = null;
    private boolean isRecording = false;
    private OnRecordListener mRecordListener = null;
    private String mRecordPath = null;
    private long mStartTime = 0;

    private MediaPlayer mPlayer = null;
    private boolean isPlayStarted = false, isPlaying = false;
    private String mPlayPath = null;
    private OnPlayListener mPlayListener = null;

    private MediaRecorder.OnErrorListener mRecordErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            stopRecord();
        }
    };

    private MediaPlayer.OnPreparedListener mPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            isPlayStarted = true;
            isPlaying = true;
            if (mPlayListener != null) {
                mPlayListener.onPlayStart(VoiceManager.this, mp);
            }
            mUpdateHandler.post(mPlayUpdateRunnable);
            /*final int length = mPlayListenerList.size();
            for (int i = 0; i < length; i++) {
                OnPlayListener listener = mPlayListenerList.get(i);
                if (listener != null) {
                    listener.onPlayStart(mp);
                }
            }*/
        }
    };

    private MediaPlayer.OnCompletionListener mPlayerCompetionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlay();
        }
    };

    private Runnable mRecordUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            long currentDuration = SystemClock.elapsedRealtime() - mStartTime;
            if (currentDuration > MAX_RECORD_DURATION && isRecording) {
                stopRecord();
                return;
            }
            if (mRecordListener != null && isRecording) {
                mRecordListener.onRecording(mRecordPath, mRecorder, currentDuration);
            }
            if (isRecording) {
                mUpdateHandler.postDelayed(this, 1000);
            }
        }
    };

    private Runnable mPlayUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayListener != null && isPlayStarted) {
                mPlayListener.onPlaying(VoiceManager.this, mPlayPath, mPlayer, mPlayer.getCurrentPosition());//TODO
            }
            if (isPlayStarted) {
                mUpdateHandler.postDelayed(this, 1000);
            }
        }
    };

    private Handler mUpdateHandler = new Handler();

    private VoiceManager(Context context) {
        super(context);

        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mRecorder = null;
    }

    public MediaRecorder startRecord (String path) {
        mRecorder = new MediaRecorder();
        //mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 14800, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_8BIT, 1024 * 128);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(path);
        mRecordPath = path;
        mRecorder.setOnErrorListener(mRecordErrorListener);
        /*mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {

            }
        });*/

        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //TODO couldn't get audio focus
            Toast.makeText(getContext(), "startRecord result failed", Toast.LENGTH_SHORT).show();
            return null;
        }
        doRecord();
        return mRecorder;
    }

    public void stopRecord () {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        mAudioManager.abandonAudioFocus(this);
        isRecording = false;
        mUpdateHandler.removeCallbacks(mRecordUpdateRunnable);
        if (mRecordListener != null) {
            mRecordListener.onRecordStop(mRecordPath, SystemClock.elapsedRealtime() - mStartTime);
        }
        mRecordPath = null;
        mStartTime = 0;
    }

    private void doRecord () {
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            isRecording = false;
        }
        mRecorder.start();
        isRecording = true;
        mStartTime = SystemClock.elapsedRealtime();

        if (mRecordListener != null) {
            mRecordListener.onRecordStart(mRecordPath, mRecorder);
        }
        mUpdateHandler.post(mRecordUpdateRunnable);
    }

    public void startPlay (String path) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(path);
            mPlayPath = path;
            mPlayer.setOnPreparedListener(mPlayerPreparedListener);
            mPlayer.setOnCompletionListener(mPlayerCompetionListener);
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //TODO couldn't get audio focus
                Toast.makeText(getContext(), "startPlay result failed", Toast.LENGTH_SHORT).show();
                return;
            }
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getPlayingPath () {
        return mPlayPath;
    }

    public void pausePlay () {
        if (mPlayer != null) {
            mPlayer.pause();
            isPlaying = false;
            if (mPlayListener != null) {
                mPlayListener.onPlayPaused();
            }
        }
    }

    public void resumePlay () {
        if (mPlayer != null) {
            mPlayer.start();
            isPlaying = true;
            if (mPlayListener != null) {
                mPlayListener.onPlayResumed();
            }
        }
    }

    public void stopPlay () {
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        isPlayStarted = false;
        isPlaying = false;
        mPlayPath = null;
        mUpdateHandler.removeCallbacks(mPlayUpdateRunnable);
        if (mPlayListener != null) {
            mPlayListener.onPlayStop(this);
        }
        /*final int length = mPlayListenerList.size();
        for (int i = 0; i < length; i++) {
            OnPlayListener listener = mPlayListenerList.get(i);
            if (listener != null) {
                listener.onPlayStop();
            }
        }*/
    }

    public boolean isPlaying () {
        return isPlaying;
    }

    public boolean isPlayStarted() {
        return isPlayStarted;
    }

    public boolean isRecording () {
        return isRecording;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            if (isRecording()) {
                stopRecord();
            }
            if (isPlayStarted()) {
                stopPlay();
            }
        }
        /*switch (mMode) {
            case MODE_RECORD:
                doRecord();
                break;
            case MODE_PLAY_BACK:
                break;
        }*/
    }

    public void setOnRecordListener (OnRecordListener listener) {
        mRecordListener = listener;
    }

    public interface OnRecordListener {
        void onRecordStart(String targetPath, MediaRecorder recorder);
        void onRecording(String targetPath, MediaRecorder recorder, long currentDuration);
        void onRecordPaused ();
        void onRecordResumed ();
        void onRecordStop(String targetPath, long durationInMills);
    }

    public void setOnPlayListener (OnPlayListener listener) {
        mPlayListener = listener;
    }

    /*public void registerOnPlayListener (OnPlayListener listener) {
        if (mPlayListenerList.contains(listener)) {
            return;
        }
        mPlayListenerList.add(listener);
    }

    public void unregisterOnPlayListener (OnPlayListener listener) {
        if (!mPlayListenerList.contains(listener)) {
            return;
        }
        mPlayListenerList.remove(listener);
    }*/

    public interface OnPlayListener {
        void onPlayStart(VoiceManager voiceManager, MediaPlayer player);
        void onPlaying(VoiceManager voiceManager, String targetPath, MediaPlayer mp, long position);
        void onPlayPaused ();
        void onPlayResumed ();
        void onPlayStop(VoiceManager voiceManager);
    }
}
