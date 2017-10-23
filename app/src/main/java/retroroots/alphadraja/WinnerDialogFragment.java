package retroroots.alphadraja;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import retroroots.alphadraja.CanisterEngine.Android.Game.Animation;
import retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;
import retroroots.alphadraja.CanisterEngine.Android.Widgets.Dialog;

/**
 * Created by DevTop on 4/14/2017.
 */
public class WinnerDialogFragment extends DialogFragment implements DialogInterface.OnDismissListener
{
    private String dialogTitle = "";

    private boolean isCancelable = false;

    private boolean isLastRound = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        android.view.View view = inflater.inflate(R.layout.winner_dialog, null);

        setCancelable(isCancelable);

        Button resumeBtn = (Button) view.findViewById(R.id.continueBtn);

        resumeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isSoundOn = getArguments().getBoolean("soundFlag");

                fightFragment.MakeVideoBtnsVisible();

                if(isSoundOn)
                {
                    Sound sound = ((Main) getActivity()).GetSoundObj();

                    sound.PlaySong(R.raw.drajamainmenueddited, true, getActivity());
                }

                dismiss();
            }
        });

        getDialog().setTitle(dialogTitle);

        return  view;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        new fightFragment().NextRound(isLastRound, getArguments(), getFragmentManager());
    }

    public void SetDialogTitle(String title)
    {
        dialogTitle = title;
    }

    public void SetIsLastRound(boolean lastRound)
    {
        isLastRound = lastRound;
    }
}
