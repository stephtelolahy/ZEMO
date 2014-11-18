package com.telolahy.mariosokoban.core;

import android.graphics.Point;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by stephanohuguestelolahy on 11/18/14.
 */
public class BoxSprite extends AnimatedSprite {

    public Point position;
    public boolean ok;

    public BoxSprite(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int pXPos, int pYPos, boolean pOK) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        position = new Point(pXPos, pYPos);
        ok = pOK;
    }
}
