package com.retroroots.alphadraja;

import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.retroroots.alphadraja.CanisterEngine.Android.Fragment.FragmentConfig;
import com.retroroots.alphadraja.CanisterEngine.Android.Utilities.Sound;

public class fightFragment extends Fragment
{
    private final String FRAGMENT_TAG = "fightFragment";

    public enum WinnerState
    {
        P1,
        P2,
        DRAW
    }

    private android.os.Handler handler = null;

    private WinnerState _winnerState;

    private Bundle fragmentBundle = null;

    private boolean p1M1Changed = false, //Flag that indicates if one of the marks changed on this round
            p1M2Changed = false,
            p1M3Changed = false,
            p2M1Changed = false,
            p2M2Changed = false,
            p2M3Changed = false;

    private FragmentConfig fragmentConfig = null;

    private String p1SavedWeaponUri = "",
            p2SavedWeaponUri = "";

    private boolean isSoundOn = false;

    private MediaPlayer currentlyPlayingSong;

    private static VideoView p1WbVw,
            p2WbVw,
            p1m1Vw,
            p1m2Vw,
            p1m3Vw,
            p2m1Vw,
            p2m2Vw,
            p2m3Vw,
            winnerVdVw,
            p1ConjVdVw,
            p2ConjVdVw;

    private ImageView
            p1WeaponImg,
            p2WeaponImg,
            p1Mark1,
            p1Mark2,
            p1Mark3,
            p2Mark1,
            p2Mark2,
            p2Mark3;

    private static ImageView viewHack,
    bigImgVw;

    private int p1Loses = 0, // Holds a count of many time they've lost
    p2Loses = 0;

    private final int bestOutOf = 3; // How many turns will be played for

    public fightFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Main.SetIsFightCurrentFrag(true);

        DrawingView.RecycleCanvasBitmap();

        //region Init variables

        View view = inflater.inflate(R.layout.fragment_fight, container, false);

        final TextView p1Lbl = (TextView) view.findViewById(R.id.p1Lbl),
                p2Lbl = (TextView) view.findViewById(R.id.p2Lbl);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/edosz.ttf");

        p1Lbl.setTypeface(tf);

        p2Lbl.setTypeface(tf);

        viewHack = (ImageView) view.findViewById(R.id.videoHackView);

        bigImgVw = (ImageView) view.findViewById(R.id.bigImgView);

        winnerVdVw = (VideoView) view.findViewById(R.id.winnerVdVw);

        p1ConjVdVw = (VideoView) view.findViewById(R.id.p1ConjVdVw);

        p2ConjVdVw = (VideoView) view.findViewById(R.id.p2ConjVdVw);

        //Make it transparent
        winnerVdVw.setAlpha(0f);

        p1m1Vw = (VideoView) view.findViewById(R.id.p1M1VdVw);

        p1m1Vw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.lost_mark));

        p1m1Vw.setAlpha(0f);

        p1m2Vw = (VideoView) view.findViewById(R.id.p1M2VdVw);

        p1m2Vw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.lost_mark));

        p1m2Vw.setAlpha(0f);

        p1m3Vw = (VideoView) view.findViewById(R.id.p1M3VdVw);

        p1m3Vw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.lost_mark));

        p1m3Vw.setAlpha(0f);

        p2m1Vw = (VideoView) view.findViewById(R.id.p2M1VdVw);

        p2m1Vw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.lost_mark));

        p2m1Vw.setAlpha(0f);

        p2m2Vw = (VideoView) view.findViewById(R.id.p2M2VdVw);

        p2m2Vw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.lost_mark));

        p2m2Vw.setAlpha(0f);

        p2m3Vw = (VideoView) view.findViewById(R.id.p2M3VdVw);

        p2m3Vw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.lost_mark));

        p2m3Vw.setAlpha(0f);

        p1WbVw = (VideoView) view.findViewById(R.id.p1wbVw);

        p2WbVw = (VideoView) view.findViewById(R.id.p2WbVw);

        p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.idle_left));

        p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.idle_right));

        p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                R.raw.shuri_conj));

        p1WeaponImg = (ImageView) view.findViewById(R.id.p1WeaponImg);

        p2WeaponImg = (ImageView) view.findViewById(R.id.p2WeaponImg);

        p1Mark1 = (ImageView) view.findViewById(R.id.p1Mark1);

        p1Mark2 = (ImageView) view.findViewById(R.id.p1Mark2);

        p1Mark3 = (ImageView) view.findViewById(R.id.p1Mark3);

        p2Mark1 = (ImageView) view.findViewById(R.id.p2Mark1);

        p2Mark2 = (ImageView) view.findViewById(R.id.p2Mark2);

        p2Mark3 = (ImageView) view.findViewById(R.id.p2Mark3);

        final Bundle previousBundle = getArguments();

        if (previousBundle.containsKey("p1Loses"))
        {
            p1Loses = previousBundle.getInt("p1Loses");

            p2Loses = previousBundle.getInt("p2Loses");

            SetPlayerLossVisibilities();
        }

        p1SavedWeaponUri = previousBundle.getString("p1SavedWeaponImgUri");

        p2SavedWeaponUri = previousBundle.getString("p2SavedWeaponImgUri");

        final WeaponFragment.Weapons player1Weapon = (WeaponFragment.Weapons) previousBundle.getSerializable("player1Weapon"),
                player2Weapon = (WeaponFragment.Weapons) previousBundle.getSerializable("player2Weapon"),
                aiWeapon = (WeaponFragment.Weapons) previousBundle.getSerializable("aiWeapon");

        isSoundOn = previousBundle.getBoolean("soundFlag");

        Sound sound = ((Main) getActivity()).GetSoundObj();

        p1WbVw.start();

        p2WbVw.start();

        handler = new android.os.Handler();

        //region Wait 6.3sec before playing conj vids

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p1ConjVdVw.start();

                p1ConjVdVw.animate().alpha(1f).setDuration(500);

                p2ConjVdVw.start();

                p2ConjVdVw.animate().alpha(1f).setDuration(500);
            }
        }, 6300);

        //endregion

        //Cover videos while black flicker happens
        viewHack.animate().alpha(0f).setDuration(4000);

        p1WeaponImg.setAlpha(0f);

        p2WeaponImg.setAlpha(0f);
        //endregion

        //region Set player img an play song

        if (isSoundOn)
        {
            winnerVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                    R.raw.tie));

            sound.PlaySong(R.raw.fightsongedited, false, getActivity());

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 8;

            Bitmap p1Bitmap = BitmapFactory.decodeFile(p1SavedWeaponUri, options);

            p1WeaponImg.setImageBitmap(p1Bitmap);

            //region Set p1 conj video src

            switch (player1Weapon)
            {
                case shuriken:

                    p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                            R.raw.shuri_conj));

                    break;

                case sword:

                    p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                            R.raw.sword_conj));

                    break;

                default:

                    p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                            R.raw.tali_conj));

                    break;
            }

            //endregion

            //PVP
            if (player2Weapon != null)
            {
                Bitmap p2Bitmap = BitmapFactory.decodeFile(p2SavedWeaponUri, options);

                p2WeaponImg.setImageBitmap(p2Bitmap);

                //region Set p2 conj video src

                switch (player2Weapon)
                {
                    case shuriken:

                        p2ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                R.raw.shuri_conj));

                        break;

                    case sword:

                        p2ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                R.raw.sword_conj));

                        break;

                    default:

                        p2ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                R.raw.tali_conj));

                        break;
                }
                //endregion

                //p2WeaponImg.animate().alpha(1f).setDuration(3000);
            }

            //region Wait 7.8 secs before showing player drawings
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {

                    p1ConjVdVw.setAlpha(0f);

                    p2ConjVdVw.setAlpha(0f);

                    p1WeaponImg.animate().alpha(1f).setDuration(400);

                    p2WeaponImg.animate().alpha(1f).setDuration(400);
                }
            }, 7800);

            //endregion

            currentlyPlayingSong = sound.GetCurrentlyPlayingSong();

            //When song ends, fighter images are replaced with either winner or draw gif
            currentlyPlayingSong.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    p1WbVw.stopPlayback();

                    p2WbVw.stopPlayback();

                    startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);

                    //region Set video resources

                    switch (_winnerState)
                    {
                        case P1:

                            if (player1Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_right));
                            }

                            else if(player1Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_right));
                            }

                            else
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_right));
                            }

                            break;

                        case P2:

                            if (player2Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_left));
                            }

                            else if(player2Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_left));
                            }

                            else
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_left));
                            }

                            break;

                        default:

                            if (player1Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_left));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_right));
                            }

                            else if(player1Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_left));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_right));
                            }

                            else
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_left));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_right));
                            }

                            break;
                    }

                    //endregion

                    p1WbVw.start();

                    p2WbVw.start();
                }
            });
        }

        else
        {
            winnerVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                    R.raw.tie_s));

            p1WeaponImg.setImageDrawable(Drawable.createFromPath(p1SavedWeaponUri));

            //region Set p1 conj video src

            switch (player1Weapon)
            {
                case shuriken:

                    p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                            R.raw.shuri_c_s));

                    break;

                case sword:

                    p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                            R.raw.sword_c_s));

                    break;

                default:

                    p1ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                            R.raw.tali_c_s));

                    break;
            }

            //endregion

            //PVP
            if (player2Weapon != null)
            {
                p2WeaponImg.setImageDrawable(Drawable.createFromPath(p2SavedWeaponUri));

                //region Set p2 conj video src

                switch (player2Weapon)
                {
                    case shuriken:

                        p2ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                R.raw.shuri_c_s));

                        break;

                    case sword:

                        p2ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                R.raw.sword_c_s));

                        break;

                    default:

                        p2ConjVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                R.raw.tali_c_s));

                        break;
                }

                //endregion
            }

            //region Wait 7.8 secs before showing player drawings
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {

                    p1ConjVdVw.setAlpha(0f);

                    p2ConjVdVw.setAlpha(0f);

                    p1WeaponImg.animate().alpha(1f).setDuration(400);

                    p2WeaponImg.animate().alpha(1f).setDuration(400);
                }
            }, 7800);

            //endregion

            //region Wait 7.7sec before changing video src

            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //Go straight to gifs
                    p1WbVw.stopPlayback();

                    p2WbVw.stopPlayback();

                    startFight(player1Weapon, player2Weapon, aiWeapon, previousBundle);

                    //region Set video resources

                    switch (_winnerState)
                    {
                        case P1:

                            if (player1Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_right_silent));
                            }

                            else if(player1Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_right_silent));
                            }

                            else
                            {
                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_right_silent));
                            }

                            break;

                        case P2:

                            if (player2Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_left_silent));
                            }

                            else if(player2Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_left_silent));
                            }

                            else
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_left_silent));
                            }

                            break;

                        default:

                            if (player1Weapon.equals(WeaponFragment.Weapons.shuriken))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_left_silent));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.shuriken_right_silent));
                            }

                            else if(player1Weapon.equals(WeaponFragment.Weapons.bomb))
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_left_silent));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.bomb_right_silent));
                            }

                            else
                            {
                                p1WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_left_silent));

                                p2WbVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.sword_right_silent));
                            }

                            break;
                    }

                    //endregion

                    p1WbVw.start();

                    p2WbVw.start();
                }
            }, 7700);

            //endregion
        }
        //endregion

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Start the fight. Also play some videos that go after the battle starts
     */
    public void startFight(WeaponFragment.Weapons p1Weapon, WeaponFragment.Weapons p2Weapon,
                           WeaponFragment.Weapons aiWeapon, Bundle previousBundle)
    {
        p1WeaponImg.setVisibility(View.VISIBLE);

        p2WeaponImg.setVisibility(View.VISIBLE);

        final WinnerState winnerState;

        //PVP
        if (p2Weapon != null)
        {
            winnerState = CheckWhoWon(p1Weapon, p2Weapon);
        }

        //PVE
        else
        {
            winnerState = CheckWhoWon(p1Weapon, aiWeapon);
        }

        _winnerState = winnerState;

        fragmentConfig = new FragmentConfig();

        fragmentBundle = previousBundle;

        fragmentBundle.putInt("p1Loses", p1Loses);

        fragmentBundle.putInt("p2Loses",p2Loses);

        //Wait 1sec before playing mark vid, setting winner src and playing, and changing round

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //region Set winner video src,  play it and player mark animation

                if(winnerState != WinnerState.DRAW)
                {
                    switch (winnerState)
                    {
                        case P1:

                            if(isSoundOn)
                                winnerVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.p1_w));

                            else
                                winnerVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.p1_w_s));

                            break;

                        case P2:

                            if(isSoundOn)
                                winnerVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.p2_w));

                            else
                                winnerVdVw.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" +
                                        R.raw.p2_w_s));

                            break;
                    }

                    if (p1M1Changed)
                    {
                        p1m1Vw.setVisibility(View.VISIBLE);

                        p1m1Vw.start();

                        p1m1Vw.setAlpha(0f);

                        p1m1Vw.animate().alpha(1f).setDuration(800);

                        p1m1Vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                        {
                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                p1Mark1.setImageResource(R.mipmap.lost_img);

                                p1m1Vw.animate().alpha(0f).setDuration(800);
                            }
                        });
                    }

                    else if (p1M2Changed)
                    {
                        p1m2Vw.setVisibility(View.VISIBLE);

                        p1m2Vw.start();

                        p1m2Vw.animate().alpha(1f).setDuration(800);

                        p1m2Vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                        {
                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                p1Mark2.setImageResource(R.mipmap.lost_img);

                                p1m2Vw.animate().alpha(0f).setDuration(800);
                            }
                        });
                    }

                    else if (p1M3Changed)
                    {
                        p1m3Vw.setVisibility(View.VISIBLE);

                        p1m3Vw.start();

                        p1m3Vw.animate().alpha(1f).setDuration(800);

                        p1m3Vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                        {
                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                p1Mark3.setImageResource(R.mipmap.lost_img);

                                p1m3Vw.animate().alpha(0f).setDuration(800);
                            }
                        });
                    }

                    else if (p2M1Changed)
                    {
                        p2m1Vw.start();

                        p2m1Vw.setVisibility(View.VISIBLE);

                        p2m1Vw.animate().alpha(1f).setDuration(800);

                        p2m1Vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                        {
                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                p2Mark1.setImageResource(R.mipmap.lost_img);

                                p2m1Vw.animate().alpha(0f).setDuration(800);
                            }
                        });
                    }

                    else if (p2M2Changed)
                    {
                        p2m2Vw.start();

                        p2m2Vw.setVisibility(View.VISIBLE);

                        p2m2Vw.animate().alpha(1f).setDuration(800);

                        p2m2Vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                        {
                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                p2Mark2.setImageResource(R.mipmap.lost_img);

                                p2m2Vw.animate().alpha(1f).setDuration(800);
                            }
                        });
                    }

                    else if (p2M3Changed)
                    {
                        p2m3Vw.start();

                        p2m3Vw.setVisibility(View.VISIBLE);

                        p2m3Vw.animate().alpha(1f).setDuration(800);

                        p2m3Vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                        {
                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                p2Mark3.setImageResource(R.mipmap.lost_img);

                                p2m3Vw.animate().alpha(1f).setDuration(800);
                            }
                        });
                    }
                }

                winnerVdVw.start();

                winnerVdVw.animate().alpha(1f).setDuration(800);

                //endregion

            }
        }, 1000);

        //Wait 6.9.5 sec before making stuff invs
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                MakeVideoBtnsVisible();
            }
        }, 6950);

        //Wait 7 secs before changing fragment
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                boolean w = (p1Loses == bestOutOf) || (p2Loses == bestOutOf);

                NextRound(w, fragmentBundle, getFragmentManager());
            }

        }, 7000);
    }

    /**
     * Verifies who won
     *
     * @param secondaryPlayerWeapon could be either player2 or ai
     */
    public WinnerState CheckWhoWon(WeaponFragment.Weapons p1Weapon,
                                     WeaponFragment.Weapons secondaryPlayerWeapon)
    {
        //region Player 1 weapon is shuriken
        //Draw
        if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            return WinnerState.DRAW;
            //Play shuriken draw gif
        }

        //Shuriken beats bomb
        else if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            p2Loses++;

            SetP2MFlag();

            return WinnerState.P1;
            //Play P1 shuriken wins gif
        }

        //Sword beats shuriken
        else if (p1Weapon.equals(WeaponFragment.Weapons.shuriken) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            p1Loses++;

            SetP1MFlag();

            return WinnerState.P2;
            //Play P2 sword wins gif
        }
        //endregion

        //region Player1 weapon is bomb

        //Shuriken beats bomb
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb) &&
                secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            p1Loses++;

            SetP1MFlag();

            return WinnerState.P2;
            //Play P1 bomb wins gif
        }

        //Draw
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            return WinnerState.DRAW;
            //Play bomb draw gif
        }

        //Bomb beats sword
        else if (p1Weapon.equals(WeaponFragment.Weapons.bomb)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            p2Loses++;

            SetP2MFlag();

            return WinnerState.P1;
            //Play P1 bomb wins gif
        }
        //endregion

        //region Player1 weapon is sword

        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.shuriken))
        {
            p2Loses++;

            SetP2MFlag();

            return WinnerState.P1;
            //Play p1 sword wins gif
        }

        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.bomb))
        {
            p1Loses++;

            SetP1MFlag();

            return WinnerState.P2;
            //Play secondary bomb wins gif
        }

        //Draw
        else if (p1Weapon.equals(WeaponFragment.Weapons.sword)
                && secondaryPlayerWeapon.equals(WeaponFragment.Weapons.sword))
        {
            return WinnerState.DRAW;
            //Play sword draw gif
        }

        return WinnerState.DRAW;
        //endregion
    }

    public String GetTag() {
        return FRAGMENT_TAG;
    }

    /**
     * Sets visibility for loss images
     */
    public void SetPlayerLossVisibilities()
    {
        //region Set loss visibility for p1

        if (p1Loses != 0)
        {
            if (p1Loses < 2)
            {
                p1Mark1.setImageResource(R.mipmap.lost_img);
            }

            else if (p1Loses < 3)
            {
                p1Mark1.setImageResource(R.mipmap.lost_img);

                p1Mark2.setImageResource(R.mipmap.lost_img);
            }

            else
            {
                p1Mark1.setImageResource(R.mipmap.lost_img);

                p1Mark2.setImageResource(R.mipmap.lost_img);

                p1Mark3.setImageResource(R.mipmap.lost_img);
            }
        }
        //endregion

        //region Set loss visibility for p2

        if (p2Loses != 0)
        {
            if (p2Loses < 2)
            {
                p2Mark1.setImageResource(R.mipmap.lost_img);
            }

            else if (p2Loses < 3)
            {
                p2Mark1.setImageResource(R.mipmap.lost_img);

                p2Mark2.setImageResource(R.mipmap.lost_img);
            }

            else
            {
                p2Mark1.setImageResource(R.mipmap.lost_img);

                p2Mark2.setImageResource(R.mipmap.lost_img);

                p2Mark3.setImageResource(R.mipmap.lost_img);
            }
        }
        //endregion
    }

    public void NextRound(boolean isLastRound, Bundle bundle, FragmentManager fragmentManager)
    {
        Main.SetIsFightCurrentFrag(false);

        if(isSoundOn)
        {
            Sound sound = ((Main) getActivity()).GetSoundObj();

            sound.PlaySong(R.raw.drajamainmenueddited, true, getActivity());
        }

        if(isLastRound)
        {
            MainMenuFragment mainMenuFragment = new MainMenuFragment();

            new FragmentConfig().ReplaceFragment(mainMenuFragment, bundle, android.R.id.content,
                    fragmentManager, mainMenuFragment.GetTag(), false);
        }

        else
        {
            WeaponFragment weaponFragment = new WeaponFragment();

            new FragmentConfig().ReplaceFragment(weaponFragment, bundle, android.R.id.content,
                    fragmentManager, weaponFragment.GetTag() + "#", true);
        }
    }

    /**
     * Set P1 marked flag
     */
    private void SetP1MFlag()
    {
        //1
        if(p1Loses < (bestOutOf - 1))
        {
            p1M1Changed = true;

        }

        //2
        else if(p1Loses < bestOutOf)
        {
            p1M2Changed = true;
        }

        //3
        else if(p1Loses == bestOutOf)
        {
            p1M3Changed = true;
        }
    }

    /**
     * Set P2 marked flag
     */
    private void SetP2MFlag()
    {
        //1
        if(p2Loses < (bestOutOf - 1))
        {
            p2M1Changed = true;
        }

        //2
        else if(p2Loses < bestOutOf)
        {
            p2M2Changed = true;
        }

        //3
        else if(p2Loses == bestOutOf)
        {
            p2M3Changed = true;
        }
    }

    public void MakeVideoBtnsVisible()
    {
        viewHack.animate().alpha(1f).setDuration(5);

        winnerVdVw.animate().alpha(0f).setDuration(5);

        bigImgVw.animate().alpha(1f).setDuration(30);
    }
}
