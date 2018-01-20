package com.retroroots.alphadraja.CanisterEngine.Android.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileManage
{
    public void Create()
    {

    }

    /**
     * Copies a file from source directory to destination directory
     *
     * @param source file that holds source file data.
     * @param destination file that holds destination file data*/
    public void Copy(File source, File destination)
    {
        try
        {
            FileChannel srcChannel = new FileInputStream(source).getChannel(),
                    destChannel = new FileOutputStream(destination).getChannel();

            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        }

        catch (Exception e)
        {
            String m = e.getMessage();
        }
    }

    public void GetFiles(File dir)
    {

    }

    public void Append()
    {

    }
}
