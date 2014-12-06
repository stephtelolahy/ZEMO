package com.telolahy.mariosokoban.scene;

import android.util.Log;

import com.telolahy.mariosokoban.MainActivity;
import com.telolahy.mariosokoban.manager.ResourcesManager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by stephanohuguestelolahy on 11/15/14.
 */
public abstract class BaseScene extends Scene {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    protected ResourcesManager mResourcesManager;
    protected VertexBufferObjectManager mVertexBufferObjectManager;
    protected ZoomCamera mCamera;
    protected MainActivity mActivity;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BaseScene(int... params) {

        mResourcesManager = ResourcesManager.getInstance();
        mVertexBufferObjectManager = ResourcesManager.getInstance().vertexBufferObjectManager;
        mCamera = ResourcesManager.getInstance().camera;
        mActivity = ResourcesManager.getInstance().activity;

        onCreateScene(params);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Abstraction
    // ===========================================================

    protected abstract void onCreateScene(int... params);

    protected abstract void onDisposeScene();

    public abstract void onBackKeyPressed();


    // ===========================================================
    // METHODS
    // ===========================================================

    public void disposeScene() {

        Engine.EngineLock engineLock = mActivity.getEngine().getEngineLock();
        engineLock.lock();

        onDisposeScene();

        engineLock.unlock();

        this.detachSelf();
        this.dispose();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        Log.i("", "finalize " + this.getClass().getName());
    }

}
