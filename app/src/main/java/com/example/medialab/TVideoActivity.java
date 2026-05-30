package com.example.medialab;

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

public class TVideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        Button btnPlayVideo = findViewById(R.id.btnPlayVideo);
        Button btnStopVideo = findViewById(R.id.btnStopVideo);

        // Получение AudioManager для управления громкостью
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Настройка SeekBar громкости
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Изменение громкости для STREAM_MUSIC (управляет и видео, и фоновым аудио)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Настройка VideoView с MediaController
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Установка источника видео из res/raw
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video_sample;
        videoView.setVideoURI(Uri.parse(videoPath));

        // Обработчик окончания видео
        videoView.setOnCompletionListener(mp -> {
            // Видео закончилось — возобновляем фоновое аудио через 1.5 секунды
            new Handler().postDelayed(() -> {
                TMainActivity.resumeBackgroundAudio();
                btnPlayVideo.setEnabled(true);
                btnStopVideo.setEnabled(false);
            }, 1500);
        });

        // Кнопка "Воспроизвести"
        btnPlayVideo.setOnClickListener(v -> {
            TMainActivity.pauseBackgroundAudio(); // пауза фонового аудио
            videoView.start();
            btnPlayVideo.setEnabled(false);
            btnStopVideo.setEnabled(true);
        });

        // Кнопка "Остановить"
        btnStopVideo.setOnClickListener(v -> {
            videoView.pause();
            btnPlayVideo.setEnabled(true);
            btnStopVideo.setEnabled(false);
            TMainActivity.resumeBackgroundAudio(); // возобновление через 1.5 сек
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}