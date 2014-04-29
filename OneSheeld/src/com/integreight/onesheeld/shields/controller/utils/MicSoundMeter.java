package com.integreight.onesheeld.shields.controller.utils;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.widget.Toast;

import com.integreight.onesheeld.Log;

public class MicSoundMeter {
	// static final private double EMA_FILTER = 0.6;
	static final private double POWER_REFERENCE = 0.00002;

	private MediaRecorder mRecorder = null;
	private double mEMA = 0.0;
	private static MicSoundMeter thisInstance;
	boolean isCanceled = false;
	boolean isRecording = false;
	boolean initialStart = true;

	private MicSoundMeter() {
		// TODO Auto-generated constructor stub
	}

	public static MicSoundMeter getInstance() {
		if (thisInstance == null)
			thisInstance = new MicSoundMeter();
		return thisInstance;
	}

	public void start() {
		if (isCanceled | initialStart) {
			initialStart = false;
			isCanceled = false;
			isRecording = false;
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");
			try {

				mRecorder.prepare();
				mRecorder.start();
				mEMA = 0.0;
				isRecording = true;
			} catch (IllegalStateException e) {

			} catch (IOException e) {

			}

		} else {
			Log.d("Mic", "Not Started");
		}
	}

	public void stop() {
		if (mRecorder != null) {
			try {
				if (isRecording) {
					mRecorder.stop();
					mRecorder.reset();
					mRecorder.release();
					mRecorder = null;
					isCanceled = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public double getAmplitude() {
		if (mRecorder != null) {
			double maxAmp = 0;
			try {
				maxAmp = mRecorder.getMaxAmplitude();
			} catch (Exception e) {
			}
			return maxAmp;
		} else
			return 0;

	}

	// db= 20* log10(amplitude/baseline_amplitude);
	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		// mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		if (amp == 0)
			return 0;

		mEMA = (20.0 * Math.log10(amp / POWER_REFERENCE));
		return (mEMA - 100.0);
	}

}
