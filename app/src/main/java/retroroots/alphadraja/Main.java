package retroroots.alphadraja;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.util.LinkedList;

import retroroots.alphadraja.CanisterEngine.Android.Application.AppManager;
import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Game.GameManager;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Dialoger;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import retroroots.alphadraja.CanisterEngine.Android.Widgets.Dialog;

/**TODO
 *
 * Add UI images/gifs
 *
 *
 * Program onResume for when the phone goes idle.
 *
* */

public class Main extends ActionBarActivity implements optionsFragment.OnSoundChanged,
        /*ModeFragment.OnModePicked,*/ WeaponFragment.OnWeaponPicked,
        DrawFragment.OnPlayerDoneDrawing, Dialoger, AppManager
{
    private static Dialog d = null;

    private FragmentConfig fragmentConfig = new FragmentConfig();

   private LinkedList<String> fragmentTags = new LinkedList<String>();

   private Sound sound = new Sound();

    //Indicates if sound is active
   private static boolean isSoundOn = true;

   private Canvas canvas;

   private Path path;

   private static int player1WeaponId = 0,
               player2WeaponId = 0,
               aiWeaponId = 0,
               modeId = 0;

    public static Dialog GetPauseDialog()
    {
        return d;
    }

    @Override
    public void KillApp()
    {
        finish();

        System.exit(0);
    }

               /*currentPlayer = 1;*/

    public enum Players
    {
        p1,
        p2
    }

    private Players currentPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SplashScreenActivity.GetActivity().finish();

        //region Initiations
        fragmentTags.add("drawFragment");

        fragmentTags.add("fightFragment");

        player1WeaponId = 0;

        player2WeaponId = 0;

        aiWeaponId = 0;

        modeId = 0;

        //currentPlayer = 1;

        currentPlayer = Players.p1;

        isSoundOn = true;

        //sound.Add(R.raw.drajamainmenueddited, "mainMenuSong", this);

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
            //currentPlayer = currentFragmentArguments.getInt("currentPlayer");
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
            else if (/*currentPlayer > 1*/ currentPlayer.equals(Players.p2)
                    && !currentFragment.getTag().contains("weapon"))
            {
                getFragmentManager().popBackStack();
            }

            else
            {
                //show to exit or go back to main menu

                ShowDialog();

                /*dialog.show(getFragmentManager(), "pauseDialog");

                String dialogStateTxt = dialog.GetDialogStateTxt();

                if (dialogStateTxt.equals(Dialog.DialogStates.Exit.toString()))
                {
                    finish();

                    System.exit(0);
                }

                else if (dialogStateTxt.equals(Dialog.DialogStates.Restart.toString()))
                {
                    ModeFragment modeFragment = new ModeFragment();

                    fragmentConfig.ReplaceFragment(modeFragment, android.R.id.content,
                            getFragmentManager(), modeFragment.GetTag(), false);
                }      */
            }
        }

        else
        {
            //show to exit or go back to main menu

            ShowDialog();

            //dialog.show(getFragmentManager(), "pauseDialog");

//            finish();
//
//            System.exit(0);
        }
    }

    @Override
    public void onBackPressed()
    {

        ShowDialog();
        /*Fragment currentFragment = getFragmentManager().findFragmentById(android.R.id.content);

        Bundle currentFragmentArguments = currentFragment.getArguments();

        if (currentFragmentArguments != null)
           //currentPlayer = currentFragmentArguments.getInt("currentPlayer");
                currentPlayer = (Players)currentFragmentArguments.getSerializable("currentPlayer");

        //if not on last fragment(fight) then check if can go back to last fragment
        if (!currentFragment.getTag().contains("fight") && !currentFragment.getTag().contains("#"))
        {
            // Is there more than one fragment saved(1 is main menu fragment) and is player 1?
            // Player 2 can only go back when drawing
            if (getFragmentManager().getBackStackEntryCount() > 1  && *//*currentPlayer < 2*//*
                    (currentPlayer == null || currentPlayer.equals(Players.p1)))
            {
                getFragmentManager().popBackStack();
            }

            //Player 2 can't go back if picking weapon
            else if (*//*currentPlayer > 1*//* currentPlayer.equals(Players.p2)
                    && !currentFragment.getTag().contains("weapon"))
            {
                getFragmentManager().popBackStack();
            }

            else
            {
                //show to exit or go back to main menu

                ShowDialog();

                *//*dialog.show(getFragmentManager(), "pauseDialog");

                String dialogStateTxt = dialog.GetDialogStateTxt();

                if (dialogStateTxt.equals(Dialog.DialogStates.Exit.toString()))
                {
                    finish();

                    System.exit(0);
                }

                else if (dialogStateTxt.equals(Dialog.DialogStates.Restart.toString()))
                {
                    ModeFragment modeFragment = new ModeFragment();

                    fragmentConfig.ReplaceFragment(modeFragment, android.R.id.content,
                            getFragmentManager(), modeFragment.GetTag(), false);
                }      *//*
            }
        }

        else
        {
            //show to exit or go back to main menu

            ShowDialog();

            //dialog.show(getFragmentManager(), "pauseDialog");

//            finish();
//
//            System.exit(0);
        }*/
    }

    @Override
    public void ShowDialog()
    {
        if(isSoundOn)
            sound.GetCurrentlyPlayingSong().pause();

        Dialog d = new Dialog();

        d.SetDialogTitle("About to exit...");

        d.show(getFragmentManager(), "pauseDialog");

        String dialogStateTxt = d.GetDialogStateTxt();

        /*if (dialogStateTxt.equals(Dialog.DialogStates.Exit.toString()))
        {
            finish();

            System.exit(0);
        }

        else if (dialogStateTxt.equals(Dialog.DialogStates.Restart.toString()))
        {
            ModeFragment modeFragment = new ModeFragment();

            fragmentConfig.ReplaceFragment(modeFragment, android.R.id.content,
                    getFragmentManager(), modeFragment.GetTag(), false);
        }*/
    }

    @Override
    public void SetIsSoundOn(Boolean isOn)
    {
        isSoundOn = isOn;
    }

    @Override
    public void SetPlayer1WeaponId(int weaponId)
    {
        player1WeaponId = weaponId;
    }

    @Override
    public void SetPlayer2WeaponId(int weaponId)
    {
        player2WeaponId = weaponId;
    }

    @Override
    public void SetAIWeaponId(int weaponId)
    {
        aiWeaponId = weaponId;
    }

    @Override
    public void SetCurrentPlayer(/*int _currentPlayer*/ Players _currentPlayer)
    {
        currentPlayer = _currentPlayer;
    }

    public void SetCanvas(Canvas _canvas)
    {
        canvas = _canvas;
    }

    public void SetPath(Path _path)
    {
        path = _path;
    }

    public Canvas GetCanvas()
    {
        return canvas;
    }

    public Path GetPath()
    {
        return path;
    }

    public static boolean GetIsSoundOn()
    {
        return isSoundOn;
    }

    public int GetModeId()
    {
        return modeId;
    }

    public Players GetCurrentPlayer()
    {
        return currentPlayer;
    }

    public Sound GetSoundObj()
    {
        return sound;
    }

}
