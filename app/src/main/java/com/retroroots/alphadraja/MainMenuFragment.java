package com.retroroots.alphadraja;

import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Field;

import com.retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;

public class MainMenuFragment extends Fragment {

    public enum Mode
    {
        Pvp,
        Pve
    }

    private final String FRAGMENT_TAG = "mainMenuFragment";

    private Button startBtn;

    private ImageButton soundImgBtn;

    private FragmentConfig fragmentConfig;

    public MainMenuFragment()
    {
        fragmentConfig = new FragmentConfig();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //region Initiators

        View  view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        TextView headerLbl = (TextView) view.findViewById(R.id.mainHeaderLbl),
                buildNumLbl = (TextView) view.findViewById(R.id.buildNum);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/edosz.ttf");

        headerLbl.setTypeface(tf);

        buildNumLbl.setTypeface(tf);

        startBtn = (Button)view.findViewById(R.id.startBtn);

        startBtn.setTypeface(tf);

        soundImgBtn = (ImageButton)view.findViewById(R.id.soundImgBtn);
        //endregion

        //region Listeners
        startBtn.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        StartBtnClick(v);
                    }
                }
        );

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

        //Is sound active? then change icon to active
        if (((Main)getActivity()).GetIsSoundOn())
            soundImgBtn.setImageResource(R.mipmap.vol_on);

        else
            soundImgBtn.setImageResource(R.mipmap.vol_off);

        // Inflate the layout for this fragment
        return view;
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

    private void StartBtnClick(View v)
    {
        if (((Main)getActivity()).GetIsSoundOn())
        {
            ((Main) getActivity()).GetSoundObj().PlayFX(R.raw.btnfx, getActivity(), false);
        }

        //fragment that replaces main

        Bundle activityBundle = new Bundle();

        activityBundle.putBoolean("soundFlag", ((Main)getActivity()).GetIsSoundOn());

        activityBundle.putSerializable("mode", Mode.Pvp);

        WeaponFragment weaponFragment = new WeaponFragment();

        fragmentConfig.ReplaceFragment(weaponFragment, activityBundle, android.R.id.content,
                getFragmentManager(), weaponFragment.GetTag(), true);
    }

    public String GetTag()
    {
        return FRAGMENT_TAG;
    }


    public void RestartGame(FragmentManager fragmentManager)
    {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();

        fragmentConfig.ReplaceFragment(mainMenuFragment, android.R.id.content,
                fragmentManager, mainMenuFragment.GetTag(), true);

    }
}
