package com.retroroots.alphadraja;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

import com.retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import com.retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import com.retroroots.alphadraja.CanisterEngine.Android.Widgets.CheckBoxManager;

public class WeaponFragment extends Fragment
{
    private OnWeaponPicked onWeaponPicked;

    public enum Weapons
    {
        shuriken,
        bomb,
        sword
    }

    private final String FRAGMENT_TAG = "weaponFragment";

    private CheckBox swordChckBx = null,
                     bombChckBx = null,
                     shurikenChckBx = null;

    private ImageButton backImgBtn = null,
                        soundImgBtn = null;

    private Main.Players currentPlayer = Main.Players.p1;

    private CheckBoxManager checkBoxManager;

    //Holds all checkbox to verify for checked
    private LinkedList<CheckBox> checkBoxes = new LinkedList<CheckBox>();

    private Bundle previousBundle = null;

    public WeaponFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        onWeaponPicked = (OnWeaponPicked)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Save instance
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_weapon, container, false);

        //region Init variables

        final TextView titleLbl = (TextView) view.findViewById(R.id.weaponTitleLbl),
                playerLbl = (TextView) view.findViewById(R.id.playerWeaponLbl);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/edosz.ttf");

        titleLbl.setTypeface(tf);

        playerLbl.setTypeface(tf);

        previousBundle = getArguments();

        Button okBtn = (Button)view.findViewById(R.id.drawBtn);

        okBtn.setTypeface(tf);

        checkBoxManager = new CheckBoxManager();

        swordChckBx = (CheckBox)view.findViewById(R.id.swordChkBx);

        swordChckBx.setTypeface(tf);

        shurikenChckBx = (CheckBox)view.findViewById(R.id.shurikenChkBx);

        shurikenChckBx.setTypeface(tf);

        bombChckBx = (CheckBox)view.findViewById(R.id.bombChkBx);

        bombChckBx.setTypeface(tf);

        backImgBtn = (ImageButton)view.findViewById(R.id.weaponBackBtnImg);

        ImageButton restartImgBtn = (ImageButton) view.findViewById(R.id.weaponRestartImgBtn);

        soundImgBtn = (ImageButton)view.findViewById(R.id.weapongSoungImgBtn);
        //endregion

        //region Listeners
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnOkClick();
            }
        });

        swordChckBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    OnSwordChckBxClick(buttonView.getId());
            }
        });

        bombChckBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    OnBombChckBxClick(buttonView.getId());
            }
        });

        shurikenChckBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    OnShurikenChckBxClick(buttonView.getId());
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(((Main)getActivity()).GetIsSoundOn())
                    new Sound().PlayFX(R.raw.btnfx, getActivity(), false);

                ((Main) getActivity()).onBackBtnPressed();
            }
        });

        restartImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(((Main)getActivity()).GetIsSoundOn())
                    new Sound().PlayFX(R.raw.btnfx, getActivity(), false);

                MainMenuFragment main = new MainMenuFragment();

                main.RestartGame(getActivity().getFragmentManager());
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
        //endregion

        if (((Main)getActivity()).GetIsSoundOn())
            soundImgBtn.setImageResource(R.mipmap.vol_on);

        else
            soundImgBtn.setImageResource(R.mipmap.vol_off);

        checkBoxes.add(shurikenChckBx);

        checkBoxes.add(swordChckBx);

        checkBoxes.add(bombChckBx);

        //region Setup controls based on the player

        currentPlayer = (Main.Players)previousBundle.getSerializable("currentPlayer");

        StringBuilder builder = new StringBuilder(getActivity().getResources().getString(R.string.playerRes));

        //If currentPlayer is null means p1 in this case, back btn is visible
        if (currentPlayer == null)
        {
            backImgBtn.setVisibility(View.VISIBLE);

            //currentPlayer = 1;
            currentPlayer = Main.Players.p1;

            builder.append(" 1");
        }

        else
        {
            //Make back button invisible
            backImgBtn.setVisibility(View.INVISIBLE);

            if(currentPlayer == Main.Players.p2)
                builder.append(" 2");

            else
                builder.append(" 1");
        }

        playerLbl.setText(builder.toString());

        //endregion

        // Inflate the layout for this fragment
        return view;
}

    private void OnShurikenChckBxClick(int shurikenChckBxId)
    {
        checkBoxManager.UnCheckCheckBoxes(checkBoxes, shurikenChckBxId);
    }

    private void OnBombChckBxClick(int bombChckBxId)
    {
        checkBoxManager.UnCheckCheckBoxes(checkBoxes, bombChckBxId);
    }

    private void OnSwordChckBxClick(int swordChckBxId)
    {
        checkBoxManager.UnCheckCheckBoxes(checkBoxes, swordChckBxId);
    }

    private void OnOkClick()
    {
        Bundle fragmentBundle = new Bundle();

        FragmentConfig fragmentConfig = new FragmentConfig();

        DrawFragment drawFragment = new DrawFragment();

        if (previousBundle.containsKey("p1Loses"))
        {
            fragmentBundle.putInt("p1Loses", previousBundle.getInt("p1Loses"));

            fragmentBundle.putInt("p2Loses", previousBundle.getInt("p2Loses"));
        }

        boolean isSoundOn = previousBundle.getBoolean("soundFlag"),
                _isSoundOn = ((Main)getActivity()).GetIsSoundOn();

        //If the previous fragment was mute and then this one isn't save this value
        if(isSoundOn != _isSoundOn)
            isSoundOn = _isSoundOn;

        fragmentBundle.putBoolean("soundFlag", isSoundOn);

        String p1SavedWeaponImgUri = previousBundle.getString("p1SavedWeaponImgUri");

        //Is uri available in bundle? Yes, then assign
        if (p1SavedWeaponImgUri != null)
            fragmentBundle.putString("p1SavedWeaponImgUri", p1SavedWeaponImgUri);

        MainMenuFragment.Mode mode = (MainMenuFragment.Mode) previousBundle.getSerializable("mode");

        Weapons player1Weapon = null;

        player1Weapon = (Weapons) previousBundle.getSerializable("player1Weapon");

        //Is weapon data available in bundle? Yes? then place in new bundle
        if (player1Weapon != null)
                fragmentBundle.putSerializable("player1Weapon", player1Weapon);

        fragmentBundle.putSerializable("mode", mode);

        fragmentBundle.putSerializable("currentPlayer",currentPlayer);

        Sound sound = new Sound();

        //play fx if active
        if (isSoundOn)
        {
            sound.StopFX();

            sound.PlayFX(R.raw.btnfx, getActivity(), false);
        }

        if (shurikenChckBx.isChecked())
        {
            fragmentBundle = SavePickedWeapon(Weapons.shuriken, currentPlayer, mode, fragmentBundle);

            fragmentConfig.ReplaceFragment(drawFragment, fragmentBundle, android.R.id.content,
                    getFragmentManager(), drawFragment.GetTag(), true);
        }

        else if (bombChckBx.isChecked())
        {
            fragmentBundle = SavePickedWeapon(Weapons.bomb, currentPlayer, mode, fragmentBundle);

            fragmentConfig.ReplaceFragment(drawFragment, fragmentBundle, android.R.id.content,
                    getFragmentManager(), drawFragment.GetTag(), true);
        }

        else if (swordChckBx.isChecked())
        {
            fragmentBundle = SavePickedWeapon(Weapons.sword, currentPlayer, mode, fragmentBundle);

            fragmentConfig.ReplaceFragment(drawFragment, fragmentBundle, android.R.id.content,
                    getFragmentManager(), drawFragment.GetTag(), true);
        }

        else if (!shurikenChckBx.isChecked() && !bombChckBx.isChecked() && !swordChckBx.isChecked())
        {
            //Show alert dialog
        }
    }

    private void SoundImgBtnClick(View v)
    {
        //if sound is on and clicked, turn off
        if (((Main)getActivity()).GetIsSoundOn())
        {
            ((Main)getActivity()).SetIsSoundOn(false);

            ((Main)getActivity()).GetSoundObj().StopSong();

            ((Main)getActivity()).GetSoundObj().StopFX();

            soundImgBtn.setImageResource(R.mipmap.vol_off);
        }

        else
        {
            ((Main)getActivity()).SetIsSoundOn(true);

            ((Main)getActivity()).GetSoundObj().PlaySong(R.raw.drajamainmenueddited, true, getActivity());

            soundImgBtn.setImageResource(R.mipmap.vol_on);
        }
    }

    /**
     * Places weapon in bundle according to mode
     *
     * @return Built bundle
    * */
    private Bundle SavePickedWeapon (Weapons weapon , Main.Players currentPlayer,
                                     MainMenuFragment.Mode _mode, Bundle bundle)
    {
        switch (_mode)
        {
            case Pvp:

                if (currentPlayer.equals(Main.Players.p1))
                    bundle.putSerializable("player1Weapon", weapon);

                else
                    bundle.putSerializable("player2Weapon", weapon);

                return bundle;

            case Pve:

                bundle.putSerializable("player1Weapon", weapon);

                //weapon is randomly picked for AI
                Random random = new Random();

                //3 cause that's the amount of weapons
                int randNum = random.nextInt(3);

                //Avoid 0 cause I consider it null
                while (randNum == 0)
                {
                    randNum = random.nextInt(3);
                }

                switch (randNum)
                {
                    case 1:
                        bundle.putSerializable("aiWeapon", Weapons.shuriken);

                        break;

                    case 2:
                        bundle.putSerializable("aiWeapon", Weapons.bomb);

                        break;

                    case 3:
                        bundle.putSerializable("aiWeapon", Weapons.sword);

                        break;
                }

                return bundle;

            default:

                return null;
        }
    }

    public String GetTag()
    {
        return FRAGMENT_TAG;
    }

    public interface OnWeaponPicked
    {
        public void SetPlayer1WeaponId(int weaponId);

        public void SetPlayer2WeaponId(int weaponId);

        public void SetAIWeaponId(int weaponId);
    }
}
