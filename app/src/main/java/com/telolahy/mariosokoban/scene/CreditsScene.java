package com.telolahy.mariosokoban.scene;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.telolahy.mariosokoban.Constants;
import com.telolahy.mariosokoban.R;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

import java.util.Calendar;

/**
 * Created by stephanohuguestelolahy on 12/3/14.
 */
public class CreditsScene extends BaseScene {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private Sprite mBackground;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onCreateScene(int... params) {

        mBackground = new Sprite(Constants.SCREEN_WIDTH / 2, mResourcesManager.menuCreditsBackgroundTextureRegion.getHeight() / 2, mResourcesManager.menuCreditsBackgroundTextureRegion, mVertexBufferObjectManager);
        attachChild(mBackground);

        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2, mResourcesManager.menuCreditsFont, mActivity.getResources().getString(R.string.game_developer), mVertexBufferObjectManager));
        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2 - 40, mResourcesManager.menuLevelFont, mActivity.getResources().getString(R.string.game_developer_value), mVertexBufferObjectManager));

        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2 - 100, mResourcesManager.menuCreditsFont, mActivity.getResources().getString(R.string.game_designer), mVertexBufferObjectManager));
        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2 - 140, mResourcesManager.menuLevelFont, mActivity.getResources().getString(R.string.game_designer_value), mVertexBufferObjectManager));

        try {
            PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            String versionName = pInfo.versionName;
            String versionDescription = mActivity.getResources().getString(R.string.app_name) + " version " + versionName;
            attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2 - 200, mResourcesManager.menuCreditsFont, versionDescription, mVertexBufferObjectManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String gameStudioDescription = mActivity.getResources().getString(R.string.game_studio) + year;
        attachChild(new Text(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2 - 240, mResourcesManager.menuCreditsFont, gameStudioDescription, mVertexBufferObjectManager));
    }

    @Override
    protected void onDisposeScene() {

    }

    @Override
    public void onBackKeyPressed() {

    }
}
