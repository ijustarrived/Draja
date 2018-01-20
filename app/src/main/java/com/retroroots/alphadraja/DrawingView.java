package com.retroroots.alphadraja;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/*Class used to manage everything related to drawing
*  
* Maybe change clear to erase and instead of clearing the whole thing, you can just erase specific things.
* */
public class DrawingView extends View
{
    private static Path drawPath; // where it's going to draw

    private Paint drawPaint,
                  canvasPaint; //paint objects for canvas and drawing

    private final static int paintColor = Color.BLACK; // paint color code

    private static Canvas drawCanvas; // Canvas used to draw on

    private static Bitmap canvasBitmap;

    private String p1ImgPath,
                   p2ImgPath;

    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        InitDrawing();
    }

    @Override // When view is assigned a size
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //Init canvas
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        //Give position for drawing
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float posX = event.getX(),
                posY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                //Set starting point
                drawPath.moveTo(posX, posY);

                break;

            case MotionEvent.ACTION_MOVE:

                //draw line in the coordinates indicated
                drawPath.lineTo(posX, posY);

                break;

            //Refresh canvas
            case MotionEvent.ACTION_UP:

                drawCanvas.drawPath(drawPath, drawPaint);

                drawPath.reset();

                break;

            default:

                return false;
        }

        //Will cause to execute onDraw
        invalidate();

        //return super.onTouchEvent(event);

        return true;
    }

    //Initiate objects
    private void InitDrawing()
    {
        //Necessary if you want drawingCache to have a value
        setDrawingCacheEnabled(true);

        drawPath = new Path();

        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);

        drawPaint.setStrokeWidth(20);

        drawPaint.setStyle(Paint.Style.STROKE);

        drawPaint.setStrokeJoin(Paint.Join.ROUND);

        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    /**Creates a new canvas giving the clear effect
    * */
    public void ClearCanvas()
    {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    /** Saves canvas content as an image to use on fight fragment.
     *
     * @param context Pass the activity.
    * */
    public void SavePlayerImg( String imgName,
                                   Activity context, String playerDirName)
    {
        try
        {
            //Holds directory for saving
            File playerDir = new File(context.getExternalFilesDir(null), playerDirName);

            playerDir.mkdir();

            //Holds player file path
            File playerFile = new File(playerDir , imgName);

            if(playerFile.exists())
                playerFile.delete();

            playerFile.createNewFile();

            /*Saves player path depending if the file is player 1 or 2.
            Checks for a 1 cause the names start with p1 or p2.*/
            if (imgName.contains("1"))
                p1ImgPath = playerFile.getAbsolutePath();

            else
                p2ImgPath = playerFile.getAbsolutePath();

            //Create stream for canvas(bitmap)
            FileOutputStream playerFOut = new FileOutputStream(playerFile
            );

            //Saves canvas(bitmap) contents
            getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, playerFOut);

            playerFOut.close();

            //To avoid redundant content
            destroyDrawingCache();
        }

        catch (Exception e)   {}
    }

    public String GetPlayer1SaveImgPath()
    {
        return p1ImgPath;
    }

    public String GetPlayer2SaveImgPath()
    {
        return p2ImgPath;
    }

    public static void RecycleCanvasBitmap()
    {
        canvasBitmap.recycle();
    }
}
