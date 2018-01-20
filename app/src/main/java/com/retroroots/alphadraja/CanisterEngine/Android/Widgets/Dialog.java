package com.retroroots.alphadraja.CanisterEngine.Android.Widgets;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import com.retroroots.alphadraja.Main;
import com.retroroots.alphadraja.R;

/**
 * Created by devilsLap on 3/2/2016.
 */
public class Dialog extends DialogFragment implements DialogInterface.OnDismissListener
{
    public enum DialogStates
    {
        Exit
    }

    private String dialogStateTxt = "",
    dialogTitle = "";

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState)
    {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    private boolean isCancelable = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_dialog, null);

        setCancelable(isCancelable);

        Button resumeBtn = (Button) view.findViewById(R.id.resumeBtn),
                exitBtn = (Button)view.findViewById(R.id.exitBtn);

        TextView title = (TextView)view.findViewById(R.id.exitTitleLbl);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/edosz.ttf");

        title.setTypeface(tf);

        resumeBtn.setTypeface(tf);

        exitBtn.setTypeface(tf);

        resumeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogStateTxt = DialogStates.Exit.toString();

                dismiss();

                Main main = new Main();

                main.KillApp();
            }
        });

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //getDialog().setTitle(dialogTitle);

        return  view;
    }

    public void SetCancelable(boolean cancelable)
    {
        isCancelable = cancelable;
    }

    public boolean GetCancelable()
    {
        return isCancelable;
    }

    public String GetDialogStateTxt()
    {
        return dialogStateTxt;
    }

    public String GetDialogTitle()
    {
        return dialogTitle;
    }

    public void SetDialogTitle(String title)
    {
        dialogTitle = title;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Sound sound = ((Main) getActivity()).GetSoundObj();

        if(Main.GetIsSoundOn())
            sound.GetCurrentlyPlayingSong().start();
    }
}
