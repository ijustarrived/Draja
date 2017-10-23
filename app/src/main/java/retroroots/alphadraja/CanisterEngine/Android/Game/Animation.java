package retroroots.alphadraja.CanisterEngine.Android.Game;

import java.util.Calendar;
import java.util.Date;

public class Animation
{
    public Animation()
    {

    }

    /**
     * Simple loop that's suppose to stop the app using two timestamps.
     * Good for waiting till an animation is over.
     * Doesn't run on a separate thread.
     *
     * @param seconds how many seconds should the loop run
     */
    public static void Wait(int seconds)
    {
        Calendar currentTime = Calendar.getInstance(),
                finalTime = Calendar.getInstance();

        currentTime.setTime(new Date());

        finalTime.setTime(new Date());

        finalTime.add(Calendar.SECOND, seconds);

        while(finalTime.after(currentTime))
        {
            currentTime.setTime(new Date());
        }
    }
}
