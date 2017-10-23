package retroroots.alphadraja;

import android.app.FragmentManager;
import android.app.WallpaperInfo;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Game.Animating;
import retroroots.alphadraja.CanisterEngine.Android.Game.Animation;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.FileManage;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.FileManager;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import retroroots.alphadraja.CanisterEngine.Android.Widgets.Dialog;

public class fightFragment extends Fragment implements FileManager, Animating
{
    private final String FRAGMENT_TAG = "fightFragment";

    public enum WinnerState
    {
        P1,
        P2,
        DRAW
    }

    private android.os.Handler handler = null;

    private WinnerState _winnerState;

    private Bundle fragmentBundle = null;

    private FragmentConfig fragmentConfig = null;

    private WinnerDialogFragment d = null;

    private String p1SavedWeaponUri = "",
            p2SavedWeaponUri = "";

    private File p1File,
            chosenAiFile,
            aiDir,
            p2File,
            aiFile,
            aiFile2;

    private boolean isSoundOn = false;

    private File[] allAiFiles;

    private MediaPlayer currentlyPlayingSong;

    private static VideoView p1WbVw,
            p2WbVw;

    /*private static Button videoBtn,
    videoBtn2;*/

    private static View viewHack;

    private ImageView p1Img,
            p2Img,
            p1WeaponImg,
            p2WeaponImg,
            p1Mark1,
            p1Mark2,
            p1Mark3,
            p2Mark1,
            p2Mark2,
            p2Mark3;

    private int amountOfFiles = 0, //Holds how many files are on ai directory.
    p1Loses = 0, // Holds a count of many time they've lost
    p2Loses = 0;

    private final int bestOutOf = 3; // How many turns will be played for

    private Random rand;

    public fightFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DrawingView.RecycleCanvasBitmap();

        //region Init variables
        View view = inflater.inflate(R.layout.fragment_fight, container, false);

        try
        {
            //Saves ai directory
            aiDir = new File(getActivity().getExternalFilesDir(null), "aiWeapons");

            //Gets all files in that directory
            allAiFiles = aiDir.listFiles();

            //Gets count of directory files as an integer
            amountOfFiles = aiDir.listFiles().length;
        }

        catch (Exception e)
        {
        }

        /*p1WbVw.loadUrl("file:///android_asset/stand.gif");

        p2WbVw.loadUrl("file:///android_asset/stand.gif");*/

//        p1Img = (ImageView) view.findViewById(R.id.p1Img);
//
//        p2Img = (ImageView) view.findViewById(R.id.p2Img);

        viewHack = (View)view.findViewById(R.id.videoHackView);

        /*videoBtn = (Button)view.findViewById(R.id.videoHackBtn);

        videoBtn2 = (Button) view.findViewById(R.id.videoHackBtn2);*/

        p1WbVw = (VideoView) view.findViewById(R.id.p1wbVw);

        p2WbVw = (VideoView) view.findViewById(R.id.p2WbVw);

        p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.idle_left));

        p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.idle_right));

        p1WeaponImg = (ImageView) view.findViewById(R.id.p1WeaponImg);

        p2WeaponImg = (ImageView) view.findViewById(R.id.p2WeaponImg);

        p1Mark1 = (ImageView) view.findViewById(R.id.p1Mark1);

        p1Mark2 = (ImageView) view.findViewById(R.id.p1Mark2);

        p1Mark3 = (ImageView) view.findViewById(R.id.p1Mark3);

        p2Mark1 = (ImageView) view.findViewById(R.id.p2Mark1);

        p2Mark2 = (ImageView) view.findViewById(R.id.p2Mark2);

        p2Mark3 = (ImageView) view.findViewById(R.id.p2Mark3);

        final Bundle previousBundle = getArguments();

        if (previousBundle.containsKey("p1Loses"))
        {
            p1Loses = previousBundle.getInt("p1Loses");

            p2Loses = previousBundle.getInt("p2Loses");

            SetPlayerLossVisibilities();
        }

        p1SavedWeaponUri = previousBundle.getString("p1SavedWeaponImgUri");

        p2SavedWeaponUri = previousBundle.getString("p2SavedWeaponImgUri");

        final WeaponFragment.Weapons player1Weapon = (WeaponFragment.Weapons) previousBundle.getSerializable("player1Weapon"),
                player2Weapon = (WeaponFragment.Weapons) previousBundle.getSerializable("player2Weapon"),
                aiWeapon = (WeaponFragment.Weapons) previousBundle.getSerializable("aiWeapon");

        isSoundOn = previousBundle.getBoolean("soundFlag");

        Sound sound = ((Main) getActivity()).GetSoundObj();

        rand = new Random();

        //It's only used to cover the video flicker before it plays
        android.os.Handler waitFiveSecsThread = new android.os.Handler();

        waitFiveSecsThread.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                viewHack.setVisibility(View.INVISIBLE);

                /*videoBtn.setVisibility(View.INVISIBLE);

                videoBtn2.setVisibility(View.INVISIBLE);*/
            }

        }, 1000);

        p1WbVw.start();

        p2WbVw.start();
        //endregion

        //Play song if active
        if (isSoundOn)
        {
            sound.PlaySong(R.raw.fightsongedited, false, getActivity());

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 8;

            Bitmap p1Bitmap = BitmapFactory.decodeFile(p1SavedWeaponUri, options);

            p1WeaponImg.setImageBitmap(p1Bitmap);

            //Display player imgs
            //p1WeaponImg.setImageDrawable(Drawable.createFromPath(p1SavedWeaponUri));

            //PVP
            if (player2Weapon != null)
            {
                Bitmap p2Bitmap = BitmapFactory.decodeFile(p2SavedWeaponUri, options);

                p2WeaponImg.setImageBitmap(p2Bitmap);

                //p2WeaponImg.setImageDrawable(Drawable.createFromPath(p2SavedWeaponUri));
            }

            //PVE
            else
            {
                chosenAiFile = allAiFiles[rand.nextInt(amountOfFiles)];

                p2WeaponImg.setImageDrawable(Drawable.createFromPath(chosenAiFile.getAbsolutePath()));
            }

            currentlyPlayingSong = sound.GetCurrentlyPlayingSong();

            //When song ends, fighter images are replaced with either winner or draw gif
            currentlyPlayingSong.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    p1WbVw.stopPlayback();

                    p2WbVw.stopPlayback();

                    startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);

                    //region Set video resources

                    switch (_winnerState)
                    {
                        case P1:

                            if (player1Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_right));
                            }

                            else if(player1Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_right));
                            }

                            else
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_right));
                            }

                            break;

                        case P2:

                            if (player2Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_left));
                            }

                            else if(player2Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_left));
                            }

                            else
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_left));
                            }

                            break;

                        default:

                            if (player1Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_left));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_right));
                            }

                            else if(player1Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_left));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_right));
                            }

                            else
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_left));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_right));
                            }

                            break;
                    }

                    //endregion

                    p1WbVw.start();

                    p2WbVw.start();
                }
            });
        }

        else
        {
            p1WeaponImg.setImageDrawable(Drawable.createFromPath(p1SavedWeaponUri));

            //PVP
            if (player2Weapon != null)
            {
                p2WeaponImg.setImageDrawable(Drawable.createFromPath(p2SavedWeaponUri));
            }

            //PVE
            else
            {
                chosenAiFile = allAiFiles[rand.nextInt(amountOfFiles)];

                p2WeaponImg.setImageDrawable(Drawable.createFromPath(chosenAiFile.getAbsolutePath()));
            }

            //android.os.Handler handler = new android.os.Handler();

            handler = new android.os.Handler();

            //Wait 5sec before running
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //Go straight to gifs
                    startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);
                }
            }, 5000);

            //Go straight to gifs
            //startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);
        }

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Start the fight
     */
    public void startFight(WeaponFragment.Weapons p1Weapon, WeaponFragment.Weapons p2Weapon,
                           WeaponFragment.Weapons aiWeapon, Bundle previousBundle)
    {
        p1WeaponImg.setVisibility(View.VISIBLE);

        p2WeaponImg.setVisibility(View.VISIBLE);

        WinnerState winnerState;

        d = new WinnerDialogFragment();

        //PVP
        if (p2Weapon != null)
        {
            winnerState = CheckWhoWon(p1Weapon, p2Weapon);

            //CheckWhoWon(p1Weapon, p2Weapon);

            //Saves players imgs for ai's use
            //Create();
        }

        //PVE
        else
        {
            winnerState = CheckWhoWon(p1Weapon, aiWeapon);
        }

        final WinnerState _winState = winnerState;

        _winnerState = winnerState;

        fragmentConfig = new FragmentConfig();

        fragmentBundle = previousBundle;

        fragmentBundle.putInt("p1Loses", p1Loses);

        fragmentBundle.putInt("p2Loses",p2Loses);

        //android.os.Handler handler = new android.os.Handler();

        handler = new android.os.Handler();

        //Wait 5sec before running
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // If loses don't equal max turns, play another round
                if ( (p1Loses + p2Loses) != bestOutOf )
                {
                    d = ShowDialog(_winState, false, d);

                    d.SetIsLastRound(false);

                    d.setArguments(fragmentBundle);
                }

                else
                {
                    d = ShowDialog(_winState, true, d);

                    d.SetIsLastRound(true);

                    d.setArguments(fragmentBundle);
                }

            }
        }, 5000);
    }

    /**
     * Verifies who won
     *
     * @param secondaryPlayerWeapon could be either player2 or ai
     */
    public WinnerState CheckWhoWon(WeaponFragment.Weapons p1Weapon,
                                     WeaponFragment.Weapons secondaryPlayerWeapon)
    {
        //region Player 1 weapon is shuriken
        //Draw
        if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken)) {

            return WinnerState.DRAW;
            //Play shuriken draw gif
        }

        //Shuriken beats bomb
        else if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            p2Loses++;

            return WinnerState.P1;
            //Play P1 shuriken wins gif
        }

        //Sword beats shuriken
        else if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            p1Loses++;

            return WinnerState.P2;
            //Play P2 sword wins gif
        }
        //endregion

        //region Player1 weapon is bomb

        //Shuriken beats bomb
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            p1Loses++;

            return WinnerState.P2;
            //Play P1 bomb wins gif
        }

        //Draw
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            return WinnerState.DRAW;
            //Play bomb draw gif
        }

        //Bomb beats sword
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            p2Loses++;

            return WinnerState.P1;
            //Play P1 bomb wins gif
        }
        //endregion

        //region Player1 weapon is sword

        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            p2Loses++;

            return WinnerState.P1;
            //Play p1 sword wins gif
        }

        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            p1Loses++;

            return WinnerState.P2;
            //Play secondary bomb wins gif
        }

        //Draw
        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            return WinnerState.DRAW;
            //Play sword draw gif
        }

        return WinnerState.DRAW;
        //endregion
    }

    public String GetTag() {
        return FRAGMENT_TAG;
    }

    /**
     * Internal method: Assigns aiFiles
     */
    public void AssignAiFiles(File aiDir, String p1FileName, String p2FileName)
    {
        aiFile = new File(aiDir, p1FileName);

        aiFile2 = new File(aiDir, p2FileName);
    }

    /**
     * Internal Method: copies both player 1 and 2 files to the same ai directory
     */
    public void CopyPlayerFilesToAiFiles(FileManage fileManage)
    {
        fileManage.Copy(p1File, aiFile);

        fileManage.Copy(p2File, aiFile2);
    }

    /**
     * Sets visibility for loss images
     */
    public void SetPlayerLossVisibilities()
    {
        //region Set loss visibility for p1

        if (p1Loses != 0)
        {
            if (p1Loses < 2)
            {
                p1Mark1.setVisibility(View.VISIBLE);
            }

            else if (p1Loses < 3)
            {
                p1Mark1.setVisibility(View.VISIBLE);

                p1Mark2.setVisibility(View.VISIBLE);
            }

            else
            {
                p1Mark1.setVisibility(View.VISIBLE);

                p1Mark2.setVisibility(View.VISIBLE);

                p1Mark3.setVisibility(View.VISIBLE);
            }
        }
        //endregion

        //region Set loss visibility for p2

        if (p2Loses != 0)
        {
            if (p2Loses < 2)
            {
                p2Mark1.setVisibility(View.VISIBLE);
            }

            else if (p2Loses < 3)
            {
                p2Mark1.setVisibility(View.VISIBLE);

                p2Mark2.setVisibility(View.VISIBLE);
            }

            else
            {
                p2Mark1.setVisibility(View.VISIBLE);

                p2Mark2.setVisibility(View.VISIBLE);

                p2Mark3.setVisibility(View.VISIBLE);
            }
        }
        //endregion
    }

    public WinnerDialogFragment ShowDialog(WinnerState winnerState, boolean isLastRound,
                                           WinnerDialogFragment d)
    {
        switch (winnerState)
        {
            case P1:

                if(isLastRound)
                    d.SetDialogTitle("The Winner is Player 1");

                else
                    d.SetDialogTitle("Player 1 Wins this Round");

                break;

            case P2:

                if(isLastRound)
                    d.SetDialogTitle("The Winner is Player 2");

                else
                    d.SetDialogTitle("Player 2 Wins this Round");

                break;

            case DRAW:

                d.SetDialogTitle("Draw");

                break;
        }

        d.show(getFragmentManager(), "winnerDialog");

        return d;
    }

    public void NextRound(boolean isLastRound, Bundle bundle, FragmentManager fragmentManager)
    {
        MakeVideoBtnsVisible();

        if(isLastRound)
        {
           /* ModeFragment modeFragment = new ModeFragment();

            new FragmentConfig().ReplaceFragment(modeFragment, bundle, android.R.id.content,
                    fragmentManager, modeFragment.GetTag(), false);*/

            MainMenuFragment mainMenuFragment = new MainMenuFragment();

            new FragmentConfig().ReplaceFragment(mainMenuFragment, bundle, android.R.id.content,
                    fragmentManager, mainMenuFragment.GetTag(), false);
        }

        else
        {
            WeaponFragment weaponFragment = new WeaponFragment();

            new FragmentConfig().ReplaceFragment(weaponFragment, bundle, android.R.id.content,
                    fragmentManager, weaponFragment.GetTag() + "#", true);

            /*new FragmentConfig().ReplaceFragment(weaponFragment, bundle, android.R.id.content,
                    getActivity().getFragmentManager(), weaponFragment.GetTag() + "#", true);*/
        }
    }

    public static void MakeVideoBtnsVisible()
    {
        viewHack.setVisibility(View.VISIBLE);

        /*videoBtn.setVisibility(View.VISIBLE);

        videoBtn2.setVisibility(View.VISIBLE);*/
    }

    //region FileManager overrides

    /*Creates a file according to specifications. In this case checks if there's any player files
     in ai directory. If there is, file names of the next to copy have the amount of files in that
    directory plus one, concatenated. If there's no files, copies them with default names*/
    @Override
    public void Create()
    {
        //Extract location of player files
        //region Extract player 1 and 2 img path

        //Split by /, last index is skipped cause is file name.

        //Holds every directory leading to player file
        String[] p1Dirs = p1SavedWeaponUri.split("/"),
                p2Dirs = p2SavedWeaponUri.split("/");

        String p1SplittedWeaponDir = "",
                p2SplittedWeaponDir = "";

        //region P1 splitting
        for (int i = 0; i < p1Dirs.length; i++)
        {
            //Start creating path to player file if index is not empty
            if (!p1Dirs[i].isEmpty())
                p1SplittedWeaponDir += File.separator + p1Dirs[i];

            //skip last index
            if ((i + 1) >= ( p1Dirs.length - 1))
            {
                break;
            }
        }
        //endregion spl

        //region P2 splitting
        for (int i = 0; i < p2Dirs.length; i++)
        {
            if (!p1Dirs[i].isEmpty())
                p2SplittedWeaponDir += File.separator + p2Dirs[i];

            //skip last index
            if ((i + 1) >= ( p2Dirs.length - 1))
            {
                break;
            }
        }
        //endregion

        //endregion

        //Holds player Files
        p1File = new File(p1SplittedWeaponDir, "p1WeaponImg");

        p2File = new File(p2SplittedWeaponDir, "p2WeaponImg");

        FileManage fileManage = new FileManage();

        File /*aiDir = new File(getActivity().getExternalFilesDir(null), "aiWeapons"),*/ //Holds directory for ai files
                aiExistingFile = new File(aiDir, "playerWeapon"); //Holds the 1st existing player file in ai directory

        //Append.
        //Is there an exiting file just like it? Copy player files using new names.
        if (aiExistingFile.exists())
        {
            //Holds how many files are on ai directory.
            //int amountOfFiles = aiDir.listFiles().length;

            //Internal method.
            AssignAiFiles(aiDir, "playerWeapon" + Integer.toString(amountOfFiles + 1),
                    "playerWeapon" + Integer.toString(amountOfFiles + 2));

            //Does file already exist? then reassign with new name and copy player file to ai
            // directory.
            if (aiFile.exists())
            {
                amountOfFiles++;

                AssignAiFiles(aiDir, "playerWeapon" + Integer.toString(amountOfFiles + 1),
                        "playerWeapon" + Integer.toString(amountOfFiles + 2));

                //Internal method
                CopyPlayerFilesToAiFiles(fileManage);
            }

            else
            {
                CopyPlayerFilesToAiFiles(fileManage);
            }
        }

        //Create ai directory and copy player files to it
        else
        {
            //Creates ai directory
            aiDir.mkdir();

            //Internal method
            AssignAiFiles(aiDir, "playerWeapon", "playerWeapon2");

            CopyPlayerFilesToAiFiles(fileManage);
        }
    }

    @Override
    public void Delete() {

    }

    @Override
    public void Copy() {

    }

    @Override
    public void Append() {

    }

    @Override
    public void Play() {

    }
    //endregion
}
