package com.freedcam.ui.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.camera1.camera.ExtendedSurfaceView;
import com.freedcam.apis.sonyremote.camera.CameraUiWrapperSony;
import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildCameraSwitch extends UiSettingsChild
{
    private AbstractCameraUiWrapper cameraUiWrapper;
    private int currentCamera = 0;
    public UiSettingsChildCameraSwitch(Context context) {
        super(context);
    }

    public UiSettingsChildCameraSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue,AppSettingsManager appSettingsManager) {
        super.SetStuff(i_activity, settingvalue,appSettingsManager);

        currentCamera = appSettingsManager.GetCurrentCamera();
        valueText.setText(getCamera(currentCamera));
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
        {
            this.setVisibility(GONE);
        }
        else {
            this.setVisibility(VISIBLE);
        }
    }

    private void switchCamera()
    {
        int maxcams = cameraUiWrapper.cameraHolder.CameraCout();
        if (currentCamera++ >= maxcams - 1)
            currentCamera = 0;

        appSettingsManager.SetCurrentCamera(currentCamera);
        sendLog("Stop Preview and Camera");
        if (cameraUiWrapper.getSurfaceView() != null &&  cameraUiWrapper.getSurfaceView() instanceof ExtendedSurfaceView)
        {
            ((ExtendedSurfaceView)cameraUiWrapper.getSurfaceView()).SwitchViewMode();
        }
        cameraUiWrapper.StopCamera();
        cameraUiWrapper.StartCamera();
        valueText.setText(getCamera(currentCamera));
    }

    private String getCamera(int i)
    {
        if (i == 0)
            return "Back";
        else if (i == 1)
            return "Front";
        else
            return "3D";
    }

    @Override
    public String[] GetValues() {
        return null;
    }


}
