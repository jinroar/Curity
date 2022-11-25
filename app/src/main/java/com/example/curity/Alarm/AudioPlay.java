package com.example.curity.Alarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class AudioPlay {

    private static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    public static boolean isplayingAudio=false;

    public static void playAudio(Context c,int id){
        mediaPlayer = MediaPlayer.create(c,id);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

        if(!mediaPlayer.isPlaying()) {
            isplayingAudio=true;
            mediaPlayer.start(); // play the mp3 file
            mediaPlayer.setLooping(true); // loop the mp3 file

        }
    }

    public static void stopAudio(){
        isplayingAudio=false;
        mediaPlayer.stop(); // stop playing the mp3 file
        mediaPlayer.release();
    }
}