package com.retroroots.alphadraja.CanisterEngine.Android.Utilities;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Manages everything related to audio and fx.
 */

public class Sound
{
    private static MediaPlayer currentlyPlayingSong,
                                currentlyPlayingFX;

    public Sound() {}

    public void PlayFX(int fxId, Context context, boolean shouldLoop)
    {
        MediaPlayer fx = MediaPlayer.create(context, fxId);

        //As long as the fx isn't the currently playing, then play
        if (currentlyPlayingFX != fx)
        {
            //Stop it just in case
            StopFX();

            //Assigns new currently playing fx
            currentlyPlayingFX = fx;

            currentlyPlayingFX.start();

            currentlyPlayingFX.setLooping(shouldLoop);
        }

        else
            currentlyPlayingFX.start();
    }

    public void PlaySong(int songId, boolean shouldLoop, Context context)
    {
        MediaPlayer song = MediaPlayer.create(context, songId);

        //As long as the song isn't the currently playing, then play
        if (currentlyPlayingSong != song)
        {
            //Stop it to avoid overlaps
            StopSong();

            //Assigns new currently playing song
            currentlyPlayingSong = song;

            currentlyPlayingSong.start();

            currentlyPlayingSong.setLooping(shouldLoop);
        }
    }

    public void StopFX()
    {
        //Resets fx
        if (currentlyPlayingFX != null)
        {
            currentlyPlayingFX.stop();

            currentlyPlayingFX.release();

            currentlyPlayingFX = null;
        }
    }

    public void StopSong()
    {
        //Resets song
        if (currentlyPlayingSong != null)
        {
            currentlyPlayingSong.stop();

            currentlyPlayingSong.release();

            currentlyPlayingSong = null;
        }
    }

    public MediaPlayer GetCurrentlyPlayingSong()
    {
        return currentlyPlayingSong;
    }
}
