package retroroots.alphadraja;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Random;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import retroroots.alphadraja.CanisterEngine.Android.Widgets.CheckBoxManager;

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

        previousBundle = getArguments();

        Button okBtn = (Button)view.findViewById(R.id.drawBtn);

        checkBoxManager = new CheckBoxManager();

        swordChckBx = (CheckBox)view.findViewById(R.id.swordChkBx);

        shurikenChckBx = (CheckBox)view.findViewById(R.id.shurikenChkBx);

        bombChckBx = (CheckBox)view.findViewById(R.id.bombChkBx);

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
            public void onClick(View v) {
                ((Main) getActivity()).onBackBtnPressed();
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
            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);

        else
            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode);

        checkBoxes.add(shurikenChckBx);

        checkBoxes.add(swordChckBx);

        checkBoxes.add(bombChckBx);

        currentPlayer = (Main.Players)previousBundle.getSerializable("currentPlayer");

        //If currentPlayer is null means p1 in this case, back btn is visible
        if (/*currentPlayer == 0*/ currentPlayer == null)
        {
            backImgBtn.setVisibility(View.VISIBLE);

            //currentPlayer = 1;
            currentPlayer = Main.Players.p1;
        }

        else
            //Make back button invisible
            backImgBtn.setVisibility(View.INVISIBLE);

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

        //if (isSoundOn)
            //Play btn sound

        if (shurikenChckBx.isChecked())
        {
            //SavePickedWeapon(0);
            //fragmentBundle = SavePickedWeapon(1, currentPlayer, mode /*modeId*/, fragmentBundle);

            fragmentBundle = SavePickedWeapon(Weapons.shuriken, currentPlayer, mode, fragmentBundle);

            fragmentConfig.ReplaceFragment(drawFragment, fragmentBundle, android.R.id.content,
                    getFragmentManager(), drawFragment.GetTag(), true);
        }

        else if (bombChckBx.isChecked())
        {
            //SavePickedWeapon(1);
            //fragmentBundle = SavePickedWeapon(2, currentPlayer, mode /*modeId*/, fragmentBundle);

            fragmentBundle = SavePickedWeapon(Weapons.bomb, currentPlayer, mode, fragmentBundle);

            fragmentConfig.ReplaceFragment(drawFragment, fragmentBundle, android.R.id.content,
                    getFragmentManager(), drawFragment.GetTag(), true);
        }

        else if (swordChckBx.isChecked())
        {
            //SavePickedWeapon(2);
            //fragmentBundle = SavePickedWeapon(3, currentPlayer, mode /*modeId*/, fragmentBundle);

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

            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode);
        }

        else
        {
            ((Main)getActivity()).SetIsSoundOn(true);

            ((Main)getActivity()).GetSoundObj().PlaySong(R.raw.drajamainmenueddited, true, getActivity());

            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
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

                if (/*currentPlayer == 1*/ currentPlayer.equals(Main.Players.p1))
                    bundle.putSerializable("player1Weapon", weapon);
                    //bundle.putInt("player1WeaponId", weaponId);
                    //onWeaponPicked.SetPlayer1WeaponId(weaponId);

                else
                    bundle.putSerializable("player2Weapon", weapon);
                    //bundle.putInt("player2WeaponId", weaponId);
                //onWeaponPicked.SetPlayer2WeaponId(weaponId);

                return bundle;

            case Pve:

                bundle.putSerializable("player1Weapon", weapon);

                //bundle.putInt("player1WeaponId", weaponId);

                //weapon is randomly picked for AI
                Random random = new Random();

                //3 cause that's the amount of weapons
                int randNum = random.nextInt(3);
                //bundle.putInt("aiWeaponId", random.nextInt(3));

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

                //bundle.putInt("aiWeaponId", randNum);

                return bundle;

            default:

                return null;
        }

        /*if (*//*modeId == 0*//* _mode)
        {
            if (currentPlayer == 1)
                bundle.putInt("player1WeaponId", weaponId);
                //onWeaponPicked.SetPlayer1WeaponId(weaponId);

            else
                bundle.putInt("player2WeaponId", weaponId);
                //onWeaponPicked.SetPlayer2WeaponId(weaponId);

            return bundle;
        }*/

        //pve
        /*else
        {
            //onWeaponPicked.SetPlayer1WeaponId(weaponId);

            bundle.putInt("player1WeaponId", weaponId);

            //weapon is randomly picked for AI
            Random random = new Random();

            int randNum = random.nextInt(3);
            //bundle.putInt("aiWeaponId", random.nextInt(3));

            //Avoid 0 cause I consider it null
            while (randNum == 0)
            {
                randNum = random.nextInt(3);
            }

            bundle.putInt("aiWeaponId", randNum);

            return bundle;

            //onWeaponPicked.SetAIWeaponId(random.nextInt(2));
        }*/
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
