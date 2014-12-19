package com.troop.freedcam.i_camera.modules;

import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_ModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 09.12.2014.
 */
public class AbstractModuleHandler implements I_ModuleHandler
{
    public ModuleEventHandler moduleEventHandler;
    public ArrayList<String> PictureModules;
    public ArrayList<String> LongeExpoModules;
    public ArrayList<String> VideoModules;
    public ArrayList<String> AllModules;
    public HashMap<String, AbstractModule> moduleList;
    protected AppSettingsManager appSettingsManager;
    protected AbstractModule currentModule;
    AbstractCameraHolder cameraHolder;

    public AbstractModuleHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = cameraHolder;
        this.appSettingsManager = appSettingsManager;
        moduleList  = new HashMap<String, AbstractModule>();
    }

    @Override
    public void SetModule(String name) {

    }

    @Override
    public String GetCurrentModuleName() {
        return null;
    }

    @Override
    public AbstractModule GetCurrentModule() {
        return null;
    }

    @Override
    public boolean DoWork() {
        return false;
    }
}
