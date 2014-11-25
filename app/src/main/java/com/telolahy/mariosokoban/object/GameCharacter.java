package com.telolahy.mariosokoban.object;

import android.graphics.Point;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by stephanohuguestelolahy on 11/20/14.
 */
public class GameCharacter extends AnimatedSprite {

    public Point gamePosition;
    public boolean moving;
    public int state;

    public GameCharacter(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int pXPos, int pYPos) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        gamePosition = new Point(pXPos, pYPos);
    }
}
