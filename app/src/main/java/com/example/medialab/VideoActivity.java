package com.example.medialab;

import static com.example.medialab.MainActivity.mediaPlayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tactivity_video);

        videoView = findViewById(R.id.videoView);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        Button btnPlayVideo = findViewById(R.id.btnPlayVideo);
        Button btnStopVideo = findViewById(R.id.btnStopVideo);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Настройка громкости
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Настройка видео
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video_sample;
        videoView.setVideoURI(Uri.parse(videoPath));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Видео закончилось - возобновляем аудио через 1.5 секунды
                new Handler().postDelayed(() -> {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        btnPlayVideo.setEnabled(true);
                        btnStopVideo.setEnabled(false);
                    }
                }, 1500);
            }
        });

        btnPlayVideo.setOnClickListener(v -> {
            MainActivity.pauseBackgroundAudio(); // Пауза аудио
            videoView.start();
            btnPlayVideo.setEnabled(false);
            btnStopVideo.setEnabled(true);
        });

        btnStopVideo.setOnClickListener(v -> {
            videoView.pause();
            btnPlayVideo.setEnabled(true);
            btnStopVideo.setEnabled(false);
            MainActivity.resumeBackgroundAudio(); // Возобновление через 1.5 сек
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}