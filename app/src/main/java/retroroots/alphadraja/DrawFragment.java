package retroroots.alphadraja;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.FileManager;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;

/*To do:
* */

public class DrawFragment extends Fragment
{

    private WeaponFragment weaponFragment = new WeaponFragment();

    private final String FRAGMENT_TAG = "drawFragment";

    private DrawingView drawView;

    private OnPlayerDoneDrawing onPlayerDoneDrawing;

    private Main.Players currentPlayer = null;

    public DrawFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Save fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //region Init variables
        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        drawView = (DrawingView)view.findViewById(R.id.drawView);

        Button doneBtn = (Button) view.findViewById(R.id.doneBtn),
                clearBtn = (Button)view.findViewById(R.id.clearBtn);

        ImageButton backImgBtn = (ImageButton) view.findViewById(R.id.drawBackBtnImg),
                restartImgBtn = (ImageButton) view.findViewById(R.id.drawRestartImgBtn);
        //endregion

        //region Listeners
        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnDoneClick();
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((Main)getActivity()).onBackPressed();
            }
        });

        restartImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainMenuFragment main = new MainMenuFragment();

                main.RestartGame(getActivity().getFragmentManager());
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnClearClick();
            }
        });
        //endregion

        //currentPlayer = ((Main)getActivity()).GetCurrentPlayer();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        onPlayerDoneDrawing = (OnPlayerDoneDrawing)activity;
    }

    public void onDetach()
    {
        super.onDetach();

        try
        {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        }

        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }

        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void OnDoneClick()
    {
        Bundle previousBundle = getArguments(),
                fragmentBundle = new Bundle();

        if (previousBundle.containsKey("p1Loses"))
        {
            fragmentBundle.putInt("p1Loses", previousBundle.getInt("p1Loses"));

            fragmentBundle.putInt("p2Loses", previousBundle.getInt("p2Loses"));
        }

        /*int modeId = previousBundle.getInt("modeId"),*/

        ModeFragment.Mode mode = (ModeFragment.Mode)previousBundle.getSerializable("mode");

        WeaponFragment.Weapons player1Weapon = (WeaponFragment.Weapons)previousBundle.getSerializable("player1Weapon"),
                player2Weapon = null,
                aiWeapon = null;

        //fragmentBundle.putInt("player1WeaponId", player1WeaponId);

        fragmentBundle.putSerializable("player1Weapon", player1Weapon);

        //player2WeaponId = previousBundle.getInt("player2WeaponId");

        player2Weapon = (WeaponFragment.Weapons)previousBundle.getSerializable("player2Weapon");

        //0 means null
        if (/*player2WeaponId > 0*/ player2Weapon != null)
            //fragmentBundle.putInt("player2WeaponId", player2WeaponId);
        fragmentBundle.putSerializable("player2Weapon", player2Weapon);

        //aiWeaponId = previousBundle.getInt("aiWeaponId");

        aiWeapon = (WeaponFragment.Weapons)previousBundle.getSerializable("aiWeapon");

        if (/*aiWeaponId > 0*/ aiWeapon != null)
            //fragmentBundle.putInt("aiWeaponId", aiWeaponId);
                fragmentBundle.putSerializable("aiWeapon", aiWeapon);

        //fragmentBundle.putInt("modeId", modeId);

        fragmentBundle.putSerializable("mode", mode);

        boolean isSoundOn = previousBundle.getBoolean("soundFlag");

        fragmentBundle.putBoolean("soundFlag", isSoundOn);

        //currentPlayer = previousBundle.getInt("currentPlayer");

        currentPlayer = (Main.Players)previousBundle.getSerializable("currentPlayer");

        Sound sound = new Sound();

        if (isSoundOn)
        {
            sound.StopFX();

            sound.PlayFX(R.raw.btnfx, getActivity(), false);
        }

        FragmentConfig fragmentConfig = new FragmentConfig();

        WeaponFragment weaponFragment = new WeaponFragment();

        fightFragment _fightFragment = new fightFragment();

        String savedP1WeaponImgUri = "",
                savedP2WeaponImgUri = "",
                p1SavePath = "P1Weapons",
                p2SavePath = "P2Weapons",
                aiSavePath = "aiWeapons";

        switch (mode)
        {
            case Pvp:

                if (/*currentPlayer == 1*/ currentPlayer.equals(Main.Players.p1))
                {
                    /*currentPlayer++;*/

                    currentPlayer = Main.Players.p2;

                    fragmentBundle.putSerializable("currentPlayer", currentPlayer);

                    //savedImgUri =  drawView.SaveImg(getActivity().getContentResolver(), "player1Img", "Contains player1's img");

                    //fragmentBundle.putString("p1SavedWeaponImgUri", savedImgUri);

                    drawView.SavePlayerImg("p1WeaponImg", getActivity(), p1SavePath/*, aiSavePath*/);

                    savedP1WeaponImgUri = drawView.GetPlayer1SaveImgPath();

                    fragmentBundle.putString("p1SavedWeaponImgUri", savedP1WeaponImgUri);

                    fragmentConfig.ReplaceFragment(weaponFragment, fragmentBundle, android.R.id.content, getFragmentManager(), weaponFragment.GetTag(), false);
                }

                else
                {
                    currentPlayer = Main.Players.p1;

                    fragmentBundle.putSerializable("currentPlayer", currentPlayer);

                    fragmentBundle.putString("p1SavedWeaponImgUri", (previousBundle.getString("p1SavedWeaponImgUri")));

                    /*savedImgUri =  drawView.SaveImg(getActivity().getContentResolver(), "player2Img", "Contains player2's img");

                    fragmentBundle.putString("p2SavedWeaponImgUri", savedImgUri);*/

                    drawView.SavePlayerImg("p2WeaponImg", getActivity(), p2SavePath/*, aiSavePath*/);

                    savedP2WeaponImgUri = drawView.GetPlayer2SaveImgPath();

                    fragmentBundle.putString("p2SavedWeaponImgUri", savedP2WeaponImgUri);

                    fragmentConfig.ReplaceFragment(_fightFragment, fragmentBundle, android.R.id.content, getFragmentManager(), _fightFragment.GetTag(), false);
                }

                break;

            case Pve:

                //savedImgUri = drawView.SaveImg(getActivity().getContentResolver(), "player1Img", "Contains player1's img");

                drawView.SavePlayerImg("p1WeaponImg", getActivity(), p1SavePath/*, aiSavePath*/);

                savedP1WeaponImgUri = drawView.GetPlayer1SaveImgPath();

                fragmentBundle.putString("p1SavedWeaponImgUri", savedP1WeaponImgUri);

                fragmentConfig.ReplaceFragment(_fightFragment, fragmentBundle, android.R.id.content, getFragmentManager(), _fightFragment.GetTag(), false);

                break;
        }
    }

    private void OnClearClick()
    {
        drawView.ClearCanvas();
    }

    public interface OnPlayerDoneDrawing
    {
        public void SetCurrentPlayer(Main.Players currentPlayer);
    }

    public String GetTag()
    {
        return FRAGMENT_TAG;
    }

}
