package com.freedcam.apis.basecamera.camera.parameters.modes;

import android.os.Handler;

/**
 * Created by George on 3/9/2015.
 */
public class ThemeList extends AbstractModeParameter
{

    private String value;
    public ThemeList(Handler uiHandler)
    {
        super(uiHandler);
    }





    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(final String valueToSet, boolean setToCam)
    {
        value = valueToSet;
        //BackgroundValueHasChanged(valueToSet);

    }

    @Override
    public String GetValue()
    {
        if (value.equals(""))
            return "Classic";
        else
            return value;
    }

    @Override
    public String[] GetValues()
    {
        //defcomg was 24/01/15 Rearranged and added new Guides

        return new String[]{/*"Classic",*/"Sample"};
    }



}
