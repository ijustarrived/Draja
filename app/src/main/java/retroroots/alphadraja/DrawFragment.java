package retroroots.alphadraja;

import android.app.Activity;
import android.app.FragmentManager;
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

    private ImageButton soundImgBtn = null;

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
        //region Init variables
        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        drawView = (DrawingView)view.findViewById(R.id.drawView);

        Button doneBtn = (Button) view.findViewById(R.id.doneBtn),
                clearBtn = (Button)view.findViewById(R.id.clearBtn);

        ImageButton backImgBtn = (ImageButton) view.findViewById(R.id.drawBackBtnImg),
                restartImgBtn = (ImageButton) view.findViewById(R.id.drawRestartImgBtn);

        soundImgBtn = (ImageButton) view.findViewById(R.id.drawSoungImgBtn);
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
                ((Main)getActivity()).onBackBtnPressed();
            }
        });

        soundImgBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        SoundImgBtnClick(v);
                    }
                }
        );

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

        if (((Main)getActivity()).GetIsSoundOn())
            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);

        else
            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode);

        // Inflate the layout for this fragment
        return view;
    }

    private void SoundImgBtnClick(View v)
    {
        //if sound is on and clicked, turn off
        if (((Main)getActivity()).GetIsSoundOn())
        {
            ((Main)getActivity()).SetIsSoundOn(false);

            ((Main)getActivity()).GetSoundObj().StopSong();

            ((Main)getActivity()).GetSoundObj().StopFX();

            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode);

            //soundImgBtn.setBackgroundResource(android.R.drawable.ic_lock_silent_mode);
        }

        else
        {
            ((Main)getActivity()).SetIsSoundOn(true);

            ((Main)getActivity()).GetSoundObj().PlaySong(R.raw.drajamainmenueddited, true, getActivity());

            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);

            //soundImgBtn.setBackgroundResource(android.R.drawable.ic_lock_silent_mode_off);
        }
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

        MainMenuFragment.Mode mode = (MainMenuFragment.Mode)previousBundle.getSerializable("mode");

        WeaponFragment.Weapons player1Weapon = (WeaponFragment.Weapons)previousBundle.getSerializable("player1Weapon"),
                player2Weapon = null,
                aiWeapon = null;

        fragmentBundle.putSerializable("player1Weapon", player1Weapon);

        player2Weapon = (WeaponFragment.Weapons)previousBundle.getSerializable("player2Weapon");

        //0 means null
        if (player2Weapon != null)
            fragmentBundle.putSerializable("player2Weapon", player2Weapon);

        aiWeapon = (WeaponFragment.Weapons)previousBundle.getSerializable("aiWeapon");

        if (aiWeapon != null)
            fragmentBundle.putSerializable("aiWeapon", aiWeapon);

        fragmentBundle.putSerializable("mode", mode);

        boolean isSoundOn = previousBundle.getBoolean("soundFlag"),
                _isSoundOn = ((Main)getActivity()).GetIsSoundOn();

        //If the previous fragment was mute and then this one isn't save this value
        if(isSoundOn != _isSoundOn)
            isSoundOn = _isSoundOn;

        fragmentBundle.putBoolean("soundFlag", isSoundOn);

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

                if (currentPlayer.equals(Main.Players.p1))
                {
                    currentPlayer = Main.Players.p2;

                    fragmentBundle.putSerializable("currentPlayer", currentPlayer);

                    drawView.SavePlayerImg("p1WeaponImg", getActivity(), p1SavePath);

                    savedP1WeaponImgUri = drawView.GetPlayer1SaveImgPath();

                    fragmentBundle.putString("p1SavedWeaponImgUri", savedP1WeaponImgUri);

                    fragmentConfig.ReplaceFragment(weaponFragment, fragmentBundle,
                            android.R.id.content, getFragmentManager(), weaponFragment.GetTag(),
                            true);
                }

                else
                {
                    currentPlayer = Main.Players.p1;

                    fragmentBundle.putSerializable("currentPlayer", currentPlayer);

                    fragmentBundle.putString("p1SavedWeaponImgUri", (previousBundle.getString("p1SavedWeaponImgUri")));

                    drawView.SavePlayerImg("p2WeaponImg", getActivity(), p2SavePath/*, aiSavePath*/);

                    savedP2WeaponImgUri = drawView.GetPlayer2SaveImgPath();

                    fragmentBundle.putString("p2SavedWeaponImgUri", savedP2WeaponImgUri);

                    fragmentConfig.ReplaceFragment(_fightFragment, fragmentBundle,
                            android.R.id.content, getFragmentManager(), _fightFragment.GetTag(),
                            false);
                }

                break;

           /* case Pve:

                //savedImgUri = drawView.SaveImg(getActivity().getContentResolver(), "player1Img", "Contains player1's img");

                drawView.SavePlayerImg("p1WeaponImg", getActivity(), p1SavePath*//*, aiSavePath*//*);

                savedP1WeaponImgUri = drawView.GetPlayer1SaveImgPath();

                fragmentBundle.putString("p1SavedWeaponImgUri", savedP1WeaponImgUri);

                fragmentConfig.ReplaceFragment(_fightFragment, fragmentBundle,
                        android.R.id.content, getFragmentManager(), _fightFragment.GetTag(), false);

                break;*/
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
