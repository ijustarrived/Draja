package retroroots.alphadraja;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import java.net.PortUnreachableException;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;


public class optionsFragment extends Fragment
{
    private MainMenuFragment mainMenuFragment = new MainMenuFragment();

    //private boolean isSoundOn = true;

    private OnSoundChanged onSoundChanged;

    private final String FRAGMENT_TAG = "optionFragment",
                         PREVIOUS_FRAGMENT_TAG = mainMenuFragment.GetTag();

    private Switch soundSwitch;

    //private Bundle previousBundle;

    public optionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //region Initiators
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        Button okBtn = (Button)view.findViewById(R.id.optionsOkBtn);

        soundSwitch = (Switch)view.findViewById(R.id.soundSwitch);

        ImageButton aboutUsBtn = (ImageButton)view.findViewById(R.id.AboutImgBtn);

        //previousBundle = getArguments();
        //endregion

        //region Listeners
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnOkClick();
            }
        });

        aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnAboutUsClick();
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    onSoundChanged.SetIsSoundOn(true);

                else
                    onSoundChanged.SetIsSoundOn(false);
            }
        });
        //endregion

        //isSoundOn  = previousBundle.getBoolean("SoundFlag");

        //Toggle on if on
        if (/*isSoundOn*/ ((Main)getActivity()).GetIsSoundOn() && !soundSwitch.isChecked())
            soundSwitch.toggle();

        // Inflate the layout for this fragment
        return view;
    }

    private void OnAboutUsClick()
    {

    }

    private void OnOkClick()
    {
        FragmentConfig fragmentConfig = new FragmentConfig();

        /*Sound sound = ((Main)getActivity()).GetSoundObj();

        if(soundSwitch.isChecked())
        {
           sound.
        }*/

        /*Bundle bundle = new Bundle();

        bundle.putBoolean("soundFlag", isSoundOn);*/

        fragmentConfig.ReplaceFragment(mainMenuFragment, /*bundle,*/ android.R.id.content, getFragmentManager(), mainMenuFragment.GetTag(), true);
    }

    public String GetTag()
    {
        return FRAGMENT_TAG;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        onSoundChanged = (OnSoundChanged)activity;
    }

    public interface OnSoundChanged
    {
        public void SetIsSoundOn(Boolean isOn);
    }


}
