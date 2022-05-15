package com.example.melody;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;


import com.example.melody.databinding.ActivityPlayMusicBinding;

import java.util.ArrayList;


import MusicComponent.SongsDetails;

public class PlayMusicActivity extends AppCompatActivity {

    ActivityPlayMusicBinding binding;
   static MediaPlayer mediaPlayer;
    int position;
    Thread updateSeekbar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayMusicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<SongsDetails> musicList = getIntent().getParcelableArrayListExtra("SongsList");
        position = getIntent().getIntExtra("pos", 0);

        binding.musicName.setText(musicList.get(position).getSongName());
        binding.musicName.setSelected(true);

        playMusic(musicList, position);

      seekBar(position);

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    binding.play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }else{
                    mediaPlayer.start();
                    binding.play.setImageResource(R.drawable.ic_baseline_pause_24);
                }
            }
        });

        binding.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 0){
                    playMusic(musicList,position);
                    return;
                }
                --position;
                binding.musicName.setText(musicList.get(position).getSongName());
                binding.musicName.setSelected(true);

                playMusic(musicList,position);

                seekBar(position);
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position == musicList.size() - 1){
                    position = 0;
                    playMusic(musicList,position);
                    binding.musicName.setText(musicList.get(position).getSongName());
                    binding.musicName.setSelected(true);
                    return;
                }

                ++position;

                binding.musicName.setText(musicList.get(position).getSongName());
                binding.musicName.setSelected(true);

                playMusic(musicList,position);

                seekBar(position);

            }
        });


        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.next.performClick();
            }
        });


    }


    private void playMusic(ArrayList<SongsDetails> musicList, int position) {

      if(mediaPlayer != null) {
          mediaPlayer.stop();
          mediaPlayer.reset();
          mediaPlayer.release();
      }

      mediaPlayer = MediaPlayer.create(PlayMusicActivity.this,musicList.get(position).getSongUri());
      mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.next.performClick();
            }
        });

    }

    public String createTime(int duration) {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time+= min + ":";

        if (sec<10)
        {
            time+= "0";
        }
        time+=sec;

        return time;
    }

    private void seekBar(int position){
        updateSeekbar = new Thread() {

            @Override
            public void run() {

                int currentPosition = position;
                int totalDuration = mediaPlayer.getDuration();


                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        binding.seekBar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        String endtime = createTime(mediaPlayer.getDuration());
        binding.fullDuration.setText(endtime);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){

                String currenttime = createTime(mediaPlayer.getCurrentPosition());
                binding.runnigTime.setText(currenttime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        binding.seekBar.setMax(mediaPlayer.getDuration());

        updateSeekbar.start();
        binding.seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        binding.seekBar.getThumb().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

    }

}
