package com.freedcam.apis.sonyremote.camera.parameters.modes;

import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.utils.StringUtils;

import java.util.Set;

import static com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.grayscale;
import static com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.off;
import static com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.on;

/**
 * Created by troop on 04.12.2015.
 */
public class NightModeSony extends BaseModeParameterSony
{
    private String currentval = StringUtils.OFF;
    private SimpleStreamSurfaceView simpleStreamSurfaceView;
    private final String GRAYSCALE = "GrayScale";
    final String ZOOMPREVIEW = "ZoomPreview";

    public NightModeSony(Handler handler, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(handler, null, null, null, null);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        switch (valueToSet) {
            case StringUtils.ON:
                simpleStreamSurfaceView.nightmode = on;
                break;
            case GRAYSCALE:
                simpleStreamSurfaceView.nightmode = grayscale;
                break;
            default:
                simpleStreamSurfaceView.nightmode = off;
                break;
        }
    }

    @Override
    public String GetValue()
    {
        switch (simpleStreamSurfaceView.nightmode)
        {
            case on:
                return StringUtils.ON;
            case off:
                return StringUtils.OFF;
            case grayscale:
                return GRAYSCALE;
           /* case zoompreview:
                return ZOOMPREVIEW;*/
            default:
                return StringUtils.OFF;
        }
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF, GRAYSCALE/*, ZOOMPREVIEW*/};
    }

    @Override
    public boolean IsSupported() {
        return Build.VERSION.SDK_INT >= 18;
    }


    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        //super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        //super.processValuesToSet(valueToSet);
    }

    @Override
    protected String processGetString() {
        return null;
    }

    @Override
    protected String[] processValuesToReturn() {
        return null;
    }
}
