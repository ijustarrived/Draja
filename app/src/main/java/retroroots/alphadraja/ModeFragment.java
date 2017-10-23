package retroroots.alphadraja;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.LinkedList;

import retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import retroroots.alphadraja.CanisterEngine.Android.Widgets.CheckBoxManager;

public class ModeFragment extends Fragment
{
    //private OnModePicked onModePicked;

    public enum Mode
    {
        Pvp,
        Pve
    }

    private final String FRAGMENT_TAG = "modeFragment";

    private CheckBox pvpChkBx = null,
                     pveChkBx = null;

    private int modeId = 0; // 0 is pvp and 1 is pve

    private CheckBoxManager checkBoxManager;

    //Holds a list of check for checked verification
    private LinkedList<CheckBox> checkBoxes = new LinkedList<CheckBox>();

    public ModeFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_mode, container, false);

        //region Init variables
        Button okBtn = (Button)view.findViewById(R.id.okBtn);

        checkBoxManager = new CheckBoxManager();

        pvpChkBx = (CheckBox)view.findViewById(R.id.pvpChkBx);

        pveChkBx = (CheckBox)view.findViewById(R.id.pveChkBx);

        ImageButton backImgBtn = (ImageButton)view.findViewById(R.id.modeBackBtnImg);
        //endregion

        //region Listeners
        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnOkClick();
            }
        });

        pvpChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OnPvpClick(buttonView.getId());
                }
            }
        });

        pveChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    OnPveClick(buttonView.getId());
                }
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
        //endregion

        checkBoxes.add(pveChkBx);

        checkBoxes.add(pvpChkBx);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        //onModePicked = (OnModePicked)activity;
    }

    private void OnOkClick()
    {
        //Play sound if active
        if (((Main)getActivity()).GetIsSoundOn())
        {
            //Just in case
            //((Main)getActivity()).GetSoundObj().StopFX();

            ((Main) getActivity()).GetSoundObj().PlayFX(R.raw.btnfx, getActivity(), false);
        }

        /*Create bundle because if it's pvp, all values on the activity might reset when on draw screen and when it
         comes back to weapon screen all values will be reset.*/
        Bundle activityBundle = new Bundle();

        WeaponFragment weaponFragment = new WeaponFragment();

        FragmentConfig fragmentConfig = new FragmentConfig();

        activityBundle.putBoolean("soundFlag", ((Main) getActivity()).GetIsSoundOn());

         if (!pveChkBx.isChecked() && !pvpChkBx.isChecked())
        {
            //AlertDialog.Builder alertDialog = new AlertDialog.Builder(((Main)getActivity())).create();
            //Show alert dialog
        }

        else if (pvpChkBx.isChecked())
         {
             //activityBundle.putInt("modeId", 0);

             activityBundle.putSerializable("mode", Mode.Pvp);

             fragmentConfig.ReplaceFragment(weaponFragment, activityBundle, android.R.id.content, getFragmentManager()
                     , weaponFragment.GetTag(), true);
         }

        else if (pveChkBx.isChecked())
         {
             //activityBundle.putInt("modeId", 1);

             activityBundle.putSerializable("mode", Mode.Pve);

             fragmentConfig.ReplaceFragment(weaponFragment, activityBundle, android.R.id.content, getFragmentManager()
                     , weaponFragment.GetTag(), true);
         }
    }

    private void OnPvpClick(int pvpChckBxId)
    {
        checkBoxManager.UnCheckCheckBoxes(checkBoxes, pvpChckBxId);
    }

    private void OnPveClick( int pveChckBxId)
    {
        checkBoxManager.UnCheckCheckBoxes(checkBoxes, pveChckBxId);
    }

    public String GetTag()
    {
        return FRAGMENT_TAG;
    }

   /* public interface OnModePicked
    {
        public void SetModeId(int modeId);
    }*/

}
