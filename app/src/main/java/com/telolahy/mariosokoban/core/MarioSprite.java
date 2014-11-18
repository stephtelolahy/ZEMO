package com.telolahy.mariosokoban.core;

import android.graphics.Point;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by stephanohuguestelolahy on 11/18/14.
 */
public class MarioSprite extends AnimatedSprite {

    public Point position;
    public boolean active;

    public MarioSprite(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int pXPos, int pYPos) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        position = new Point(pXPos, pYPos);
    }
}
