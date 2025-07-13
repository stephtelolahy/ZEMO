package com.telolahy.mariosokoban;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.telolahy.mariosokoban.manager.GameManager;
import com.telolahy.mariosokoban.manager.ResourcesManager;
import com.telolahy.mariosokoban.manager.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public class MainActivity extends BaseGameActivity {

    private ZoomCamera mCamera;

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 60);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.heightPixels;
        int height = metrics.widthPixels;
        Constants.SCREEN_HEIGHT = height * Constants.SCREEN_WIDTH / width;

        Constants.TOTAL_LEVELS_COUNT = GameManager.getInstance().countLevelsAssetFiles(this);

//        mCamera = new BoundCamera(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        mCamera = new ZoomCamera(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, Constants.SCREEN_ORIENTATION, new FillResolutionPolicy(), mCamera);
        engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
        engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {

        ResourcesManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {

        SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ResourcesManager.getInstance().menuMusic != null && ResourcesManager.getInstance().menuMusic.isPlaying()) {
            ResourcesManager.getInstance().menuMusic.pause();
        }
    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
        System.gc();
        if (ResourcesManager.getInstance().menuMusic != null && SceneManager.getInstance().isOnMenu() && GameManager.getInstance().isMusicEnabled()) {
            ResourcesManager.getInstance().menuMusic.play();
        }
    }
}
