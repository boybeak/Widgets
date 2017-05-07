package com.nulldreams.widget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by gaoyunfei on 2017/5/5.
 */

public class Amplitude {

    public static Amplitude fromFile (String path) {
        return fromFile(new File(path));
    }

    public static Amplitude fromFile (File file) {
        try {
            return fromStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Amplitude fromStream (InputStream stream) {

        try {
            byte[] amp17v = new byte[6];
            stream.read(amp17v);

            byte[] cursorBa = new byte[4];
            stream.read(cursorBa);
            int cursor = byteArrayToInt(cursorBa);

            byte[] drawBarBufSizeBa = new byte[4];
            stream.read(drawBarBufSizeBa);
            int drawBarBufSize = byteArrayToInt(drawBarBufSizeBa);

            byte[] maxValueBa = new byte[4];
            stream.read(maxValueBa);
            int maxValue = byteArrayToInt(maxValueBa);

            byte[] minValueBa = new byte[4];
            stream.read(minValueBa);
            int minValue = byteArrayToInt(minValueBa);

            byte[] ampMaxBa = new byte[4];
            stream.read(ampMaxBa);
            int ampMax = byteArrayToInt(ampMaxBa);

            byte[] ampUnitBa = new byte[4];
            stream.read(ampUnitBa);
            int ampUtil = byteArrayToInt(ampUnitBa);

            byte[] barColorBa = new byte[4];
            stream.read(barColorBa);
            int barColor = byteArrayToInt(barColorBa);

            byte[] barWidthBa = new byte[4];
            stream.read(barWidthBa);
            float barWidth = byteArrayToFloat(barWidthBa);

            byte[] gapWidthBa = new byte[4];
            stream.read(gapWidthBa);
            float gapWidth = byteArrayToFloat(gapWidthBa);

            byte[] heightUnitBa = new byte[4];
            stream.read(heightUnitBa);
            float heightUnit = byteArrayToFloat(heightUnitBa);

            byte[] directionBa = new byte[4];
            stream.read(directionBa);
            int direction = byteArrayToInt(directionBa);

            byte[] periodBa = new byte[8];
            stream.read(periodBa);
            long period = byteArrayToLong(periodBa);

            byte[] movePeriodBa = new byte[8];
            stream.read(movePeriodBa);
            long movePeriod = byteArrayToLong(movePeriodBa);

            byte[] data = new byte[cursor];
            stream.read(data);

            return new Amplitude(data, direction, cursor, drawBarBufSize, maxValue, minValue,
                    ampUtil, ampMax, barColor, barWidth, gapWidth, heightUnit, period, movePeriod);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] intToByteArray (int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private static int byteArrayToInt (byte[] array) {
        return ByteBuffer.wrap(array).getInt();
    }

    private static byte[] floatToByteArray (float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    private static float byteArrayToFloat (byte[] array) {
        return ByteBuffer.wrap(array).getFloat();
    }

    private static byte[] longToByteArray (long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    private static long byteArrayToLong (byte[] array) {
        return ByteBuffer.wrap(array).getLong();
    }

    private byte[] data;
    private int direction;
    private int cursor, drawBarBufSize;
    private int maxValue, minValue, ampUnit, ampMax;
    private int barColor;
    private float barWidth, gapWidth, heightUnit;
    private long period, movePeriod;

    public Amplitude(byte[] data, int direction, int cursor, int drawBarBufSize, int maxValue,
                     int minValue, int ampUnit, int ampMax, int barColor, float barWidth,
                     float gapWidth, float heightUint, long period, long movePeriod) {
        this.data = data;
        this.direction = direction;
        this.cursor = cursor;
        this.drawBarBufSize = drawBarBufSize;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.ampUnit = ampUnit;
        this.ampMax = ampMax;
        this.barColor = barColor;
        this.barWidth = barWidth;
        this.gapWidth = gapWidth;
        this.heightUnit = heightUint;
        this.period = period;
        this.movePeriod = movePeriod;
    }

    public boolean flushTo (File to) {
        if (!to.getParentFile().exists()) {
            if (!to.getParentFile().mkdirs()) {
                return false;
            }
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(to);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (outputStream != null) {
            byte[] amp17v = {0x61, 0x6d, 0x70, 0x31, 0x37, 0x76};
            byte[] cursor = intToByteArray(this.cursor);
            byte[] drawBarBufSize = intToByteArray(this.drawBarBufSize);
            byte[] maxValue = intToByteArray(this.maxValue);
            byte[] minValue = intToByteArray(this.minValue);
            byte[] ampMax = intToByteArray(this.ampMax);
            byte[] ampUnit = intToByteArray(this.ampUnit);
            byte[] barColor = intToByteArray(this.barColor);

            byte[] barWidth = floatToByteArray(this.barWidth);
            byte[] gapWidth = floatToByteArray(this.gapWidth);
            byte[] heightUnit = floatToByteArray(this.heightUnit);

            byte[] direction = intToByteArray(this.direction);

            byte[] period = longToByteArray(this.period);
            byte[] movePeriod = longToByteArray(this.movePeriod);

            try {
                outputStream.write(amp17v);
                outputStream.write(cursor);
                outputStream.write(drawBarBufSize);
                outputStream.write(maxValue);
                outputStream.write(minValue);
                outputStream.write(ampMax);
                outputStream.write(ampUnit);
                outputStream.write(barColor);

                outputStream.write(barWidth);
                outputStream.write(gapWidth);
                outputStream.write(heightUnit);

                outputStream.write(direction);

                outputStream.write(period);
                outputStream.write(movePeriod);
                outputStream.write(this.data);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean flushTo (String to) {
        return flushTo(new File(to));
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("direction", direction);
            jsonObject.put("cursor", cursor);
            jsonObject.put("drawBarBufSize", drawBarBufSize);
            jsonObject.put("maxValue", maxValue);
            jsonObject.put("minValue", minValue);
            jsonObject.put("ampUnit", ampUnit);
            jsonObject.put("ampMax", ampMax);
            jsonObject.put("barColor", barColor);
            jsonObject.put("barWidth", barWidth);
            jsonObject.put("gapWidth", gapWidth);
            jsonObject.put("heightUnit", heightUnit);
            jsonObject.put("period", period);
            jsonObject.put("movePeriod", movePeriod);
            jsonObject.put("data_length", data.length);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
