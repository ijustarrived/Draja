package retroroots.alphadraja;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.File;
import java.util.Random;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Game.Animating;
import retroroots.alphadraja.CanisterEngine.Android.Game.Animation;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.FileManage;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.FileManager;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;

public class fightFragment extends Fragment implements FileManager, Animating
{
    private final String FRAGMENT_TAG = "fightFragment";

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

        WebView p1WbVw = (WebView) view.findViewById(R.id.p1wbVw),
                p2WbVw = (WebView) view.findViewById(R.id.p2WbVw);

        p1WbVw.loadUrl("file:///android_asset/stand.gif");

        p2WbVw.loadUrl("file:///android_asset/stand.gif");

//        p1Img = (ImageView) view.findViewById(R.id.p1Img);
//
//        p2Img = (ImageView) view.findViewById(R.id.p2Img);

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
        //endregion

        //Play song if active
        if (isSoundOn)
        {
            sound.PlaySong(R.raw.fightsongedited, false, getActivity());

            //DisplayPlayersWeaponImgs();

            //Display player imgs
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

            currentlyPlayingSong = sound.GetCurrentlyPlayingSong();

            //When song ends, fighter images are replaced with either winner or draw gif
            currentlyPlayingSong.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);
                }
            });
        }

        else
        {
            //DisplayPlayersWeaponImgs();

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

            //Go straight to gifs
            startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);
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
//        Main.Players currentPlayer = Main.Players.p1;
//
//        Bundle fragBundle = new Bundle();

        //PVP
        if (p2Weapon != null)
        {
            CheckWhoWon(p1Weapon, p2Weapon);

            //Saves players imgs for ai's use
            Create();

//            currentPlayer = (Main.Players)previousBundle.getSerializable("currentPlayer");
//
//            if (currentPlayer.equals(Main.Players.p1))
//            {
//                currentPlayer = Main.Players.p2;
//            }
//
//            else
//            {
//                currentPlayer = Main.Players.p1;
//            }
        }

        //PVE
        else
        {
            CheckWhoWon(p1Weapon, aiWeapon);
        }

        Animation.Wait(250);

        FragmentConfig fragmentConfig = new FragmentConfig();

        Sound sound = ((Main) getActivity()).GetSoundObj();

        if(isSoundOn)
        {
            sound.PlaySong(R.raw.drajamainmenueddited, true, getActivity());
        }

        Bundle fragmentBundle = previousBundle;

        fragmentBundle.putInt("p1Loses", p1Loses);

        fragmentBundle.putInt("p2Loses",p2Loses);



        // If loses don't equal max turns, play another round
        if ( (p1Loses + p2Loses) != bestOutOf )
        {
            WeaponFragment weaponFragment = new WeaponFragment();

            fragmentConfig.ReplaceFragment(weaponFragment, fragmentBundle, android.R.id.content,
                    getActivity().getFragmentManager(), weaponFragment.GetTag(), false);
        }

        else
        {
            ModeFragment modeFragment = new ModeFragment();

            fragmentConfig.ReplaceFragment(modeFragment, fragmentBundle, android.R.id.content,
                    getActivity().getFragmentManager(), modeFragment.GetTag(), false);
        }

//        fragmentConfig.ReplaceFragment(newFrag,android.R.id.content,
//                getActivity().getFragmentManager(), mainMenuFragment.GetTag(), false);
    }

    /**
     * Verifies who won
     *
     * @param secondaryPlayerWeapon could be either player2 or ai
     */
    public void CheckWhoWon(WeaponFragment.Weapons p1Weapon,
                            WeaponFragment.Weapons secondaryPlayerWeapon)
    {
        //region Player 1 weapon is shuriken
        //Draw
        if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken)) {
            //Play shuriken draw gif
        }

        //Shuriken beats bomb
        else if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            p2Loses++;
            //Play P1 shuriken wins gif
        }

        //Sword beats shuriken
        else if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            p1Loses++;
            //Play P2 sword wins gif
        }
        //endregion

        //region Player1 weapon is bomb

        //Shuriken beats bomb
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            p1Loses++;
            //Play P1 bomb wins gif
        }

        //Draw
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            //Play bomb draw gif
        }

        //Bomb beats sword
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            p2Loses++;
            //Play P1 bomb wins gif
        }
        //endregion

        //region Player1 weapon is sword

        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            p2Loses++;
            //Play p1 sword wins gif
        }

        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            p1Loses++;
            //Play secondary bomb wins gif
        }

        //Draw
        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword)) {
            //Play sword draw gif
        }
        //endregion
    }

    /**
     * Internal method: Displays weapon imgs
     */
    private void DisplayPlayersWeaponImgs()
    {
            /*p1WeaponImg.setImageDrawable(Drawable.createFromPath(p1SavedWeaponUri));

            p2WeaponImg.setImageDrawable(Drawable.createFromPath(p2SavedWeaponUri));*/

            //p1WeaponImg.setImageDrawable(Drawable.createFromPath(p1SavedWeaponUri));

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
