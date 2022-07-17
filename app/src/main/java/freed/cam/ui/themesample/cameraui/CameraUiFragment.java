/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.cameraui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.troop.freedcam.R;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.databinding.CamerauiFragmentBinding;
import com.troop.freedcam.databinding.NextgenCamerauiTextSwitchBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.ActivityAbstract;
import freed.FreedApplication;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.histogram.HistogramController;
import freed.cam.histogram.MyHistogram;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.ui.I_swipe;
import freed.cam.ui.SwipeMenuListner;
import freed.cam.ui.guide.GuideHandler;
import freed.cam.ui.themenextgen.view.NextGenCameraUiTextSwitch;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.PagingViewTouchState;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.cameraui.modelview.InfoOverlayModelView;
import freed.cam.ui.themesample.handler.FocusImageHandler;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.BooleanSettingModeInterface;
import freed.update.ReleaseChecker;
import freed.update.VersionView;
import freed.utils.LocationManager;
import freed.utils.Log;

/**
 * Created by troop on 14.06.2015.
 */
@AndroidEntryPoint
public class CameraUiFragment extends AbstractFragment implements
        SettingsChildAbstract.CloseChildClick,
        I_swipe,
        OnClickListener,
        CameraHolderEvent
{
    final String TAG = CameraUiFragment.class.getSimpleName();

    private CamerauiFragmentBinding binding;

    //hold the button wich opened the horizontalValuesFragment
    private NextGenCameraUiTextSwitch currentOpendChild;
    //Shows the values when a uibutton got clicked
    private HorizontalValuesFragment horizontalValuesFragment;
    //there the horizontalValuesFragment gets inflated
    private View camerauiValuesFragmentHolder;
    //handels the touch events that happen on screen
    private SwipeMenuListner touchHandler;
    //well that get clicked when we want to start an action
    private ShutterButton shutterButton;
    //holds the manualButtons
    private ManualFragment manualModesFragment;
    //used to inflate the manualsModesFragment
    private FrameLayout manualModes_holder;
    //open state from manualModesFragment
    private boolean manualsettingsIsOpen;
    //Handel the animation/visibility for focus trigger, meteringarea, manualwbarea
    private FocusImageHandler focusImageHandler;
    //show the time,sdspace, pic/video size
    private InfoOverlayModelView infoOverlayModelView;
    //private SampleInfoOverlayHandler infoOverlayHandler;
    //holds guide
    private GuideHandler guideHandler;

    private HorizontLineFragment horizontLineFragment;

    @Inject
    public SettingsManager settingsManager;
    @Inject
    UserMessageHandler userMessageHandler;
    @Inject
    CameraApiManager cameraApiManager;
    @Inject
    PagingViewTouchState pagingViewTouchState;
    @Inject
    HistogramController histogramController;
    @Inject
    PreviewController preview;
    @Inject
    LocationManager locationManager;

    private Handler handler = new Handler();

    public CameraUiFragment()
    {
    }

    private void setCameraToUi(CameraWrapperInterface wrapper) {

            binding.leftUiHolder.removeAllViews();
            binding.rightUiHolderTop.removeAllViews();
            binding.rightUiItemsBottom.removeAllViews();
        if (wrapper == null) {
            if (focusImageHandler != null) {
                focusImageHandler.AEMeteringSupported(false);
                focusImageHandler.TouchToFocusSupported(false);
                shutterButton.setVisibility(View.GONE);
                if (isAdded())
                    hide_ManualSettings();
            }
        }
        else {

            ParameterHandler parameterHandler = wrapper.getParameterHandler();
            if (parameterHandler == null)
                return;

            if (parameterHandler.get(SettingKeys.Focuspeak) != null)
                addUiTextSwitch(binding.leftUiHolder, (BooleanSettingModeInterface) parameterHandler.get(SettingKeys.Focuspeak), FreedApplication.getStringFromRessources(R.string.font_focuspeak_on));
            if (parameterHandler.get(SettingKeys.HISTOGRAM) != null)
                addUiTextSwitch(binding.leftUiHolder, (BooleanSettingModeInterface) parameterHandler.get(SettingKeys.HISTOGRAM), FreedApplication.getStringFromRessources(R.string.font_flash_histogram));
            if (parameterHandler.get(SettingKeys.CLIPPING) != null)
                addUiTextSwitch(binding.leftUiHolder, (BooleanSettingModeInterface) parameterHandler.get(SettingKeys.CLIPPING),FreedApplication.getStringFromRessources(R.string.font_clipping));

            /*if (parameterHandler.get(SettingKeys.IsoMode) != null)
                setUiItem(binding.leftUiHolder, parameterHandler.get(SettingKeys.IsoMode), R.drawable.quck_set_iso_png);*/
            if (parameterHandler.get(SettingKeys.FlashMode) != null)
                addUiTextSwitch(binding.leftUiHolder, (AbstractParameter) parameterHandler.get(SettingKeys.FlashMode), onNextGenButtonClick);
            if (parameterHandler.get(SettingKeys.FocusMode) != null)
                addUiTextSwitchWithValue(binding.leftUiHolder, (AbstractParameter) parameterHandler.get(SettingKeys.FocusMode),FreedApplication.getStringFromRessources(R.string.font_manual_focus),onNextGenButtonClick);
            if (parameterHandler.get(SettingKeys.ExposureLock) != null)
                addUiTextSwitch(binding.leftUiHolder, (BooleanSettingModeInterface) parameterHandler.get(SettingKeys.ExposureLock),FreedApplication.getStringFromRessources(R.string.font_exposurelock));

            addUiTextSwitchWithValue(binding.leftUiHolder, (AbstractParameter) parameterHandler.get(SettingKeys.selfTimer),FreedApplication.getStringFromRessources(R.string.font_exposuretime),onNextGenButtonClick);
            addUiTextSwitchWithValue(binding.leftUiHolder, (AbstractParameter) parameterHandler.get(SettingKeys.Module),FreedApplication.getStringFromRessources(R.string.font_image), onNextGenButtonClick);
            addUiTextSwitchWithValue(binding.leftUiHolder, (AbstractParameter) parameterHandler.get(SettingKeys.CAMERA_SWITCH),FreedApplication.getStringFromRessources(R.string.font_camera), onNextGenButtonClick);

            /*if (parameterHandler.get(SettingKeys.ExposureMode) != null)
                setUiItem(binding.leftUiHolder, parameterHandler.get(SettingKeys.ExposureMode), R.drawable.quck_set_ae);*/
            /*if (parameterHandler.get(SettingKeys.AE_PriorityMode) != null)
                setUiItem(binding.leftUiHolder, parameterHandler.get(SettingKeys.AE_PriorityMode), R.drawable.ae_priority);
            if (parameterHandler.get(SettingKeys.ContShootMode) != null)
                setUiItem(binding.leftUiHolder, parameterHandler.get(SettingKeys.ContShootMode), R.drawable.quck_set_contin);
            if (parameterHandler.get(SettingKeys.HDRMode) != null)
                setUiItem(binding.leftUiHolder, parameterHandler.get(SettingKeys.HDRMode), R.drawable.quck_set_hdr);*/

            /*if (wrapper.getParameterHandler().get(SettingKeys.NightMode) != null) {
                UiSettingsChild night = new UiSettingsChild(getContext());
                night.SetMenuItemClickListner(this, true);
                night.SetParameter(parameterHandler.get(SettingKeys.NightMode));
                night.setBackgroundResource(R.drawable.quck_set_night);
                binding.leftUiHolder.addView(night);
            }*/

            //right camera top camerui itmes

            if (isAdded()) {
                focusImageHandler.SetCamerUIWrapper(wrapper);

                shutterButton.setVisibility(View.VISIBLE);
                shutterButton.SetCameraUIWrapper(wrapper);

                guideHandler.setCameraUiWrapper(wrapper);

                horizontLineFragment.setCameraUiWrapper(wrapper);

                //restore view state for the manuals
                if (settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).get())
                    showManualSettings();
                //remove the values fragment from ui when a new api gets loaded and it was open.
                if (horizontalValuesFragment != null && horizontalValuesFragment.isAdded())
                    removeHorizontalFragment();
            }
        }
    }

    private final int iconsize = 33;
    private final int frontsize = 12;
    private void addUiTextSwitch(LinearLayout root, AbstractParameter parameter, OnClickListener onClickListener) {
        NextGenCameraUiTextSwitch nextgenCamerauiTextSwitchBinding = new NextGenCameraUiTextSwitch(getContext());
        nextgenCamerauiTextSwitchBinding.setParameter(parameter,iconsize);
        nextgenCamerauiTextSwitchBinding.setOnClickListener(onClickListener);
        nextgenCamerauiTextSwitchBinding.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.END);
        root.addView(nextgenCamerauiTextSwitchBinding);
    }

    private void addUiTextSwitchWithValue(LinearLayout root, AbstractParameter parameter,String background, OnClickListener onClickListener) {
        NextGenCameraUiTextSwitch nextgenCamerauiTextSwitchBinding = new NextGenCameraUiTextSwitch(getContext());
        nextgenCamerauiTextSwitchBinding.setParameter(parameter,true,background,iconsize,frontsize);
        nextgenCamerauiTextSwitchBinding.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.END);
        nextgenCamerauiTextSwitchBinding.setOnClickListener(onClickListener);
        root.addView(nextgenCamerauiTextSwitchBinding);
    }

    private void addUiTextSwitch(LinearLayout root, BooleanSettingModeInterface parameter, String icon) {
        NextGenCameraUiTextSwitch nextgenCamerauiTextSwitchBinding = new NextGenCameraUiTextSwitch(getContext());
        nextgenCamerauiTextSwitchBinding.setBooleanSettingModeInterface(parameter, icon,iconsize);
        nextgenCamerauiTextSwitchBinding.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.END);
        root.addView(nextgenCamerauiTextSwitchBinding);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        binding = CamerauiFragmentBinding.inflate(inflater);
        Log.d(TAG, "####################ONCREATEDVIEW####################");
        touchHandler = new SwipeMenuListner(this);
        manualsettingsIsOpen = settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).get();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userMessageHandler.setMessageTextView(view.findViewById(id.textView_usermessage));
        manualModes_holder = binding.manualModesHolder;
        cameraApiManager.addEventListner(this);
        infoOverlayModelView = new ViewModelProvider(this).get(InfoOverlayModelView.class);
        getLifecycle().addObserver(infoOverlayModelView);
        infoOverlayModelView.setCameraApiManager(cameraApiManager);
        infoOverlayModelView.setSettingsManager(settingsManager);
        infoOverlayModelView.setLocationManager(locationManager);
        binding.infoOverlay.setInfoOverlayModel(infoOverlayModelView.getInfoOverlayModel());

        focusImageHandler = new FocusImageHandler(view, (ActivityAbstract) getActivity(), pagingViewTouchState);

        shutterButton = binding.shutterButton;

        view.setOnTouchListener(onTouchListener);

        MyHistogram histogram = binding.hisotview;
        histogramController.setMyHistogram(histogram);
        ImageView waveform = binding.imageViewWaveform;
        waveform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preview.isColorWaveForm())
                    preview.setColorWaveForm(false);
                else
                    preview.setColorWaveForm(true);
            }
        });
        histogramController.setWaveFormView(waveform);


        manualModesFragment = new ManualFragment();

        horizontLineFragment = new HorizontLineFragment();

        guideHandler =GuideHandler.getInstance();

        manualModes_holder.setVisibility(View.GONE);
        camerauiValuesFragmentHolder = binding.camerauiValuesFragmentHolder;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(id.guideHolder, guideHandler, "Guide");
        transaction.commit();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.bottom_to_top_enter, anim.empty);
        transaction.replace(id.manualModesHolder, manualModesFragment);
        transaction.commit();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.empty, anim.empty);
        transaction.replace(id.horHolder, horizontLineFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        boolean showhelp = settingsManager.getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser));
            transaction.addToBackStack(null);
            transaction.commit();
        }
        setCameraToUi(cameraApiManager.getCamera());
        checkForUpdate();

    }

    private void checkForUpdate() {
        if (ReleaseChecker.isGithubRelease && settingsManager.getGlobal(SettingKeys.CHECKFORUPDATES).get()) {
            new ReleaseChecker(new ReleaseChecker.UpdateEvent() {
                @Override
                public void onUpdateAvailable() {
                    binding.framelayoutVersion.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.framelayoutVersion.addView(new VersionView(getContext(), new VersionView.ButtonEvents() {
                                @Override
                                public void onDownloadClick() {
                                    startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KillerInk/FreeDcam/releases/latest")), "Choose browser"));
                                    binding.framelayoutVersion.removeAllViews();
                                }

                                @Override
                                public void onCloseClick() {
                                    try {
                                        if (binding.framelayoutVersion != null)
                                            binding.framelayoutVersion.removeAllViews();
                                    }
                                    catch (ActivityNotFoundException ex)
                                    {
                                        Log.WriteEx(ex);
                                    }

                                }
                            }));
                        }
                    });
                }
            }).isUpdateAvailable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        userMessageHandler.setMessageTextView(null);
        cameraApiManager.removeEventListner(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onPause()
    {
        Log.d(TAG,"onPause");
        settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).set(manualsettingsIsOpen);
        super.onPause();

    }

    private void hide_ManualSettings()
    {
        manualsettingsIsOpen = false;
        Log.d(TAG, "HideSettings");
        manualModes_holder.animate().translationY(manualModes_holder.getHeight()).setDuration(300);
    }

    private void showManualSettings()
    {
        Log.d(TAG, "ShowSettings");
        manualsettingsIsOpen = true;
        manualModes_holder.animate().translationY(0).setDuration(300);
        manualModes_holder.setVisibility(View.VISIBLE);
    }

    private OnClickListener onNextGenButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentOpendChild == v)
            {
                removeHorizontalFragment();
                currentOpendChild = null;
                return;
            }
            if (currentOpendChild != null)
            {
                removeHorizontalFragment();
                currentOpendChild = null;
            }
            if (horizontalValuesFragment != null)
                horizontalValuesFragment.Clear();

            currentOpendChild = (NextGenCameraUiTextSwitch) v;
            horizontalValuesFragment = new HorizontalValuesFragment();
            String[] tmo = currentOpendChild.getParameter().getStringValues();
            if (tmo != null && tmo.length >0)
                horizontalValuesFragment.SetStringValues(tmo, CameraUiFragment.this);
            inflateIntoHolder(id.cameraui_values_fragment_holder, horizontalValuesFragment);
        }
    };



    private void inflateIntoHolder(int id, HorizontalValuesFragment fragment)
    {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.left_to_right_enter, 0);
        transaction.replace(id, fragment);
        transaction.commit();
    }

    private void removeHorizontalFragment()
    {
        getChildFragmentManager().beginTransaction().remove(horizontalValuesFragment).setCustomAnimations(0, anim.right_to_left_exit).commit();
    }

    @Override
    public void onCloseClicked(String value) {
        currentOpendChild.getParameter().setStringValue(value,true);
        currentOpendChild.setText(value);
        removeHorizontalFragment();
        currentOpendChild = null;
    }



    @Override
    public void  doRightToLeftSwipe() {

    }

    @Override
    public void doLeftToRightSwipe(){
    }

    @Override
    public void doTopToBottomSwipe(){
        hide_ManualSettings();
    }

    @Override
    public void doBottomToTopSwipe(){
        showManualSettings();
    }

    @Override
    public void onClick(int x, int y) {
        if (focusImageHandler != null)
            focusImageHandler.OnClick(x,y);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (focusImageHandler != null)
            focusImageHandler.onTouchEvent(event);
    }

    OnTouchListener onTouchListener = new OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event)
        {
            return touchHandler.onTouchEvent(event);
        }

    };

    //On Settings Menu Click
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        handler.post(() -> setCameraToUi(cameraApiManager.getCamera()));
    }

    @Override
    public void onCameraClose() {
        handler.post(() -> setCameraToUi(null));
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }

    interface i_HelpFragment
    {
        void Close(Fragment fragment);
    }

    private final i_HelpFragment helpfragmentCloser = fragment -> {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    };
}
