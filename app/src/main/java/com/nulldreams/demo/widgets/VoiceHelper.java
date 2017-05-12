package com.nulldreams.demo.widgets;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by boybe on 2017/5/12.
 */

public class VoiceHelper {
    private VoiceHelper () {

    }

    private static MediaRecorder sRecorder;
    public static MediaRecorder startRecord (String path) {
        if (isRecording()) {
            return null;
        }
        sRecorder = new MediaRecorder();
        sRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        sRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        sRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        sRecorder.setOutputFile(path);
        try {
            sRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sRecorder.start();
        return sRecorder;
    }

    public static void stopRecord () {
        if (sRecorder != null) {
            sRecorder.stop();
            sRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
            sRecorder.release();
            sRecorder = null;
        }
    }

    public static boolean isRecording () {
        return sRecorder != null;
    }
}
