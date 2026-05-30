package com.example.medialab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;

public class TMainActivity extends AppCompatActivity {
    private ImageView imageView;
    // 4 изображения для галереи
    private int[] images = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5, R.drawable.image_6};
    private int currentIndex = 0;
    private Timer slideshowTimer;
    private boolean isSlideshowRunning = false;
    public static MediaPlayer mediaPlayer;

    // Для дополнительного задания: обновление позиции аудио
    private SeekBar audioPositionBar;
    private Timer audioTimer;
    private Handler mainHandler = new Handler();
    //private Button btnSlideshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tactivity_main);

        imageView = findViewById(R.id.imageView);
        audioPositionBar = findViewById(R.id.audioPositionBar);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnSlideshow = findViewById(R.id.btnSlideshow);
        Button btnVideo = findViewById(R.id.btnVideo);

        btnPrev.setOnClickListener(v -> showPreviousImage());
        btnNext.setOnClickListener(v -> showNextImage());
        btnSlideshow.setOnClickListener(v -> toggleSlideshow());
        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(TMainActivity.this, TVideoActivity.class);
            startActivity(intent);
        });

        // Запуск фонового аудио
        startBackgroundAudio();

        // Запуск обновления позиции аудио
        startAudioPositionUpdate();
    }

    private void showImage(int index) {
        if (index >= 0 && index < images.length) {
            imageView.setImageResource(images[index]);
            currentIndex = index;
        }
    }

    private void showNextImage() {
        currentIndex = (currentIndex + 1) % images.length;
        showImage(currentIndex);
    }

    private void showPreviousImage() {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        showImage(currentIndex);
    }

    private void toggleSlideshow() {
        if (isSlideshowRunning) {
            if (slideshowTimer != null) {
                slideshowTimer.cancel();
            }
            isSlideshowRunning = false;
            //btnSlideshow.setText("Автосмена");
        } else {
            slideshowTimer = new Timer();
            slideshowTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> showNextImage());
                }
            }, 0, 3000); // интервал 3 секунды по заданию
            isSlideshowRunning = true;
            //tnSlideshow.setText("Стоп");
        }
    }

    private void startBackgroundAudio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.audio_sample);
        mediaPlayer.setLooping(true); // зацикливание по заданию
        mediaPlayer.start();
    }

    // Дополнительное задание: обновление SeekBar позиции аудио
    private void startAudioPositionUpdate() {
        audioTimer = new Timer();
        audioTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(() -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int duration = mediaPlayer.getDuration();
                        int position = mediaPlayer.getCurrentPosition();
                        if (duration > 0) {
                            audioPositionBar.setProgress((position * 1000) / duration);
                        }
                    }
                });
            }
        }, 0, 1000); // обновление каждую секунду
    }

    // Метод для паузы (вызывается из TVideoActivity)
    public static void pauseBackgroundAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Метод для возобновления с задержкой 1.5 секунды
    public static void resumeBackgroundAudio() {
        new Handler().postDelayed(() -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (slideshowTimer != null) {
            slideshowTimer.cancel();
        }
        if (audioTimer != null) {
            audioTimer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}