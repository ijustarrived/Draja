package com.retroroots.alphadraja.CanisterEngine.Android.Widgets;

import android.widget.CheckBox;

import java.util.LinkedList;

public class CheckBoxManager
{
    public CheckBoxManager()
    {
    }

    /**If one checkbox is checked this will uncheck the rest. That way there won't be multiple checked
     *
     * @param checkBoxes List of all available checkboxes that should be unchecked
     *
     * @param checkedCheckBoxId Id of the only checkbox that should remain checked
    * */
    public void UnCheckCheckBoxes(LinkedList<CheckBox> checkBoxes, int checkedCheckBoxId)
    {
        for (int i = 0; i < checkBoxes.size(); i++ )
        {
            //is it checked and it's not the one that should be check?
            if (checkBoxes.get(i).isChecked() && checkBoxes.get(i).getId() != checkedCheckBoxId)
                checkBoxes.get(i).toggle();
        }
    }

}
