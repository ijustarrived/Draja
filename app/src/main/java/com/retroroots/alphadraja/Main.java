package com.retroroots.alphadraja;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.util.LinkedList;

import com.retroroots.alphadraja.CanisterEngine.Android.Application.AppManager;
import com.retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import com.retroroots.alphadraja.CanisterEngine.Android.Utilities.Dialoger;
import com.retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import com.retroroots.alphadraja.CanisterEngine.Android.Widgets.Dialog;


public class Main extends ActionBarActivity implements optionsFragment.OnSoundChanged,
         WeaponFragment.OnWeaponPicked,
        DrawFragment.OnPlayerDoneDrawing, Dialoger, AppManager
{
    private static boolean isFightCurrentFrag = false;

    private FragmentConfig fragmentConfig = new FragmentConfig();

   private LinkedList<String> fragmentTags = new LinkedList<String>();

   private Sound sound = new Sound();

    //Indicates if sound is active
   private static boolean isSoundOn = true;

   private Canvas canvas;

   private Path path;

   private static int
               modeId = 0;

    @Override
    public void KillApp()
    {
        finish();

        System.exit(0);
    }

    public enum Players
    {
        p1,
        p2
    }

    private Players currentPlayer = null;

    public static void SetIsFightCurrentFrag(boolean isFight)
    {
        isFightCurrentFrag = isFight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SplashScreenActivity.GetActivity().finish();

        //region Initiations
        fragmentTags.add("drawFragment");

        fragmentTags.add("fightFragment");

        modeId = 0;

        currentPlayer = Players.p1;

        isSoundOn = true;

        //endregion

        //Hide upper action bar
        getSupportActionBar().hide();

        /*
          Since the screen orientation changes a few times, this looks for the last fragment and loads it.
          If it doesn't find any, then it's back to main menu.
         */
        if(!fragmentConfig.ReloadFragment(getFragmentManager(), android.R.id.content, false))
        {
            MainMenuFragment mainMenuFragment = new MainMenuFragment();

            //Set orientation to landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            if (isSoundOn)
                sound.PlaySong(R.raw.drajamainmenueddited,true, this);

            fragmentConfig.ReplaceFragment(mainMenuFragment, android.R.id.content, getFragmentManager(), mainMenuFragment.GetTag(), true);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(isSoundOn)
        {
            sound.GetCurrentlyPlayingSong().pause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(isSoundOn)
        {
            sound.GetCurrentlyPlayingSong().start();
        }
    }

    public void onBackBtnPressed()
    {
        Fragment currentFragment = getFragmentManager().findFragmentById(android.R.id.content);

        Bundle currentFragmentArguments = currentFragment.getArguments();

        if (currentFragmentArguments != null)
            currentPlayer = (Players)currentFragmentArguments.getSerializable("currentPlayer");

        //if not on last fragment(fight) then check if can go back to last fragment
        if (!currentFragment.getTag().contains("fight") && !currentFragment.getTag().contains("#"))
        {
            // Is there more than one fragment saved(1 is main menu fragment) and is player 1?
            // Player 2 can only go back when drawing
            if (getFragmentManager().getBackStackEntryCount() > 1  && /*currentPlayer < 2*/
                    (currentPlayer == null || currentPlayer.equals(Players.p1)))
            {
                getFragmentManager().popBackStack();
            }

            //Player 2 can't go back if picking weapon
            else if ( currentPlayer.equals(Players.p2)
                    && !currentFragment.getTag().contains("weapon"))
            {
                getFragmentManager().popBackStack();
            }

            else
            {
                //show to exit or go back to main menu

                ShowDialog();
            }
        }

        else
        {
            //show to exit or go back to main menu

            ShowDialog();
        }
    }

    @Override
    public void onBackPressed()
    {
        ShowDialog();
    }

    @Override
    public void ShowDialog()
    {
        //Don't do anything if it's fight fragment
        if(!isFightCurrentFrag)
        {
            if(isSoundOn)
                sound.GetCurrentlyPlayingSong().pause();

            Dialog d = new Dialog();

            d.show(getFragmentManager(), "pauseDialog");
        }
    }

    @Override
    public void SetIsSoundOn(Boolean isOn)
    {
        isSoundOn = isOn;
    }

    @Override
    public void SetPlayer1WeaponId(int weaponId)
    {
    }

    @Override
    public void SetPlayer2WeaponId(int weaponId)
    {
    }

    @Override
    public void SetAIWeaponId(int weaponId)
    {
    }

    @Override
    public void SetCurrentPlayer(Players _currentPlayer)
    {
        currentPlayer = _currentPlayer;
    }

    public static boolean GetIsSoundOn()
    {
        return isSoundOn;
    }

    public Sound GetSoundObj()
    {
        return sound;
    }

}
