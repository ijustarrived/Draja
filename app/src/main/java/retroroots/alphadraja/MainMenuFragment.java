package retroroots.alphadraja;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.lang.reflect.Field;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Game.GameManager;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Dialoger;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;

public class MainMenuFragment extends Fragment {

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

        startBtn = (Button)view.findViewById(R.id.startBtn);

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
            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);

        else
            soundImgBtn.setImageResource(android.R.drawable.ic_lock_silent_mode);

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

    private void StartBtnClick(View v)
    {
        if (((Main)getActivity()).GetIsSoundOn())
        {
            //Just in case
            //((Main)getActivity()).GetSoundObj().StopFX();

            ((Main) getActivity()).GetSoundObj().PlayFX(R.raw.btnfx, getActivity(), false);
        }

        //fragment that replaces main
        ModeFragment modeFragment = new ModeFragment();

        fragmentConfig.ReplaceFragment(modeFragment, android.R.id.content, getFragmentManager(), modeFragment.GetTag(), true);
    }

    public String GetTag()
    {
        return FRAGMENT_TAG;
    }


    public void RestartGame(FragmentManager fragmentManager)
    {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();

        fragmentConfig.ReplaceFragment(mainMenuFragment, android.R.id.content,
               fragmentManager, mainMenuFragment.GetTag(), false);
    }
}
