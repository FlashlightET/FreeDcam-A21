package com.troop.freedcam.camera2.modules;

import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    BaseCameraHolderApi2 cameraHolder;

    final String TAG = "freedcam.ModuleHandler";

    public  ModuleHandlerApi2 (AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder,appSettingsManager);
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        this.appSettingsManager = appSettingsManager;
        moduleList  = new HashMap<String, AbstractModule>();
        moduleEventHandler = new ModuleEventHandler();
        PictureModules = new ArrayList<String>();
        PictureModules.add(ModuleHandler.MODULE_PICTURE);
        PictureModules.add(ModuleHandler.MODULE_BURST);
        PictureModules.add(ModuleHandler.MODULE_HDR);
        //PictureModules.add();
        VideoModules = new ArrayList<String>();
        VideoModules.add(ModuleHandler.MODULE_VIDEO);
        AllModules = new ArrayList<String>();
        AllModules.add(ModuleHandler.MODULE_ALL);
        LongeExpoModules = new ArrayList<String>();
        LongeExpoModules.add(ModuleHandler.MODULE_LONGEXPO);
        initModules();

    }


    @Override
    public void SetModule(String name) {
        if (currentModule !=null)
            currentModule.UnloadNeededParameters();
        currentModule = moduleList.get(name);
        currentModule.LoadNeededParameters();
        moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
        Log.d(TAG, "Set Module to " + name);
    }

    @Override
    public String GetCurrentModuleName() {
        if (currentModule != null)
            return currentModule.name;
        else return "";
    }

    @Override
    public AbstractModule GetCurrentModule() {
        if (currentModule != null)
            return currentModule;
        return null;
    }

    @Override
    public boolean DoWork() {
        if (currentModule != null) {
            currentModule.DoWork();
            return true;
        }
        else
            return false;
    }

    private void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
