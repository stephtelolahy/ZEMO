package com.telolahy.mariosokoban.utils;

import android.graphics.Point;

import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.BaseDetector;

/**
 * Created by stephanohuguestelolahy on 11/20/14.
 */
public class LongScrollDetector extends BaseDetector {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final float TRIGGER_SCROLL_MINIMUM_DISTANCE_DEFAULT = 40;

    // ===========================================================
    // Fields
    // ===========================================================

    private float mTriggerScrollMinimumDistance;
    private final IScrollDetectorListener mScrollDetectorListener;

    private int mPointerID = TouchEvent.INVALID_POINTER_ID;

    private boolean mTriggering;

    private float mLastX;
    private float mLastY;
    private Point mLastVector;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LongScrollDetector(final IScrollDetectorListener pScrollDetectorListener) {
        this(LongScrollDetector.TRIGGER_SCROLL_MINIMUM_DISTANCE_DEFAULT, pScrollDetectorListener);
    }

    public LongScrollDetector(final float pTriggerScrollMinimumDistance, final IScrollDetectorListener pScrollDetectorListener) {
        this.mTriggerScrollMinimumDistance = pTriggerScrollMinimumDistance;
        this.mScrollDetectorListener = pScrollDetectorListener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public float getTriggerScrollMinimumDistance() {
        return this.mTriggerScrollMinimumDistance;
    }

    public void setTriggerScrollMinimumDistance(final float pTriggerScrollMinimumDistance) {
        this.mTriggerScrollMinimumDistance = pTriggerScrollMinimumDistance;
    }

    public boolean isTriggering() {
        return this.mTriggering;
    }

    public Point getVector() {

        return this.mLastVector;
    }

    @Override
    public void setEnabled(boolean pEnabled) {
        super.setEnabled(pEnabled);
        reset();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void reset() {
        if (this.mTriggering) {
            triggerOnScrollFinished(0, 0);
        }

        this.mLastX = 0;
        this.mLastY = 0;
        this.mTriggering = false;
        this.mLastVector = null;
        this.mPointerID = TouchEvent.INVALID_POINTER_ID;
    }

    @Override
    public boolean onManagedTouchEvent(final TouchEvent pSceneTouchEvent) {
        final float touchX = this.getX(pSceneTouchEvent);
        final float touchY = this.getY(pSceneTouchEvent);

        switch (pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
                this.prepareScroll(pSceneTouchEvent.getPointerID(), touchX, touchY);
                return true;
            case TouchEvent.ACTION_MOVE:
                if (this.mPointerID == TouchEvent.INVALID_POINTER_ID) {
                    this.prepareScroll(pSceneTouchEvent.getPointerID(), touchX, touchY);
                    return true;
                } else if (this.mPointerID == pSceneTouchEvent.getPointerID()) {
                    final float distanceX = touchX - this.mLastX;
                    final float distanceY = touchY - this.mLastY;

                    final float triggerScrollMinimumDistance = this.mTriggerScrollMinimumDistance;
                    if (Math.abs(distanceX) > triggerScrollMinimumDistance || Math.abs(distanceY) > triggerScrollMinimumDistance) {

                        if (!this.mTriggering) {
                            this.triggerOnScrollStarted(distanceX, distanceY);
                        } else {
                            this.triggerOnScroll(distanceX, distanceY);
                        }

                        this.mLastX = touchX;
                        this.mLastY = touchY;
                    }
                    return true;
                } else {
                    return false;
                }
            case TouchEvent.ACTION_UP:
            case TouchEvent.ACTION_CANCEL:
                if (this.mPointerID == pSceneTouchEvent.getPointerID()) {
                    final float distanceX = touchX - this.mLastX;
                    final float distanceY = touchY - this.mLastY;

                    if (this.mTriggering) {
                        this.triggerOnScrollFinished(distanceX, distanceY);
                    }

                    this.mPointerID = TouchEvent.INVALID_POINTER_ID;
                }
                return true;
            default:
                return false;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void prepareScroll(final int pPointerID, final float pTouchX, final float pTouchY) {
        this.mLastX = pTouchX;
        this.mLastY = pTouchY;
        this.mTriggering = false;
        this.mLastVector = null;
        this.mPointerID = pPointerID;
    }

    private void triggerOnScrollStarted(final float pDistanceX, final float pDistanceY) {
        this.mTriggering = true;
        if (this.mPointerID != TouchEvent.INVALID_POINTER_ID) {
//            this.mScrollDetectorListener.onScrollStarted(this, this.mPointerID, pDistanceX, pDistanceY);
            triggerOnScrollVector(pDistanceX, pDistanceY);
        }
    }

    private void triggerOnScroll(final float pDistanceX, final float pDistanceY) {
        if (this.mPointerID != TouchEvent.INVALID_POINTER_ID) {
//            this.mScrollDetectorListener.onScroll(this, this.mPointerID, pDistanceX, pDistanceY);
            triggerOnScrollVector(pDistanceX, pDistanceY);
        }
    }

    private void triggerOnScrollFinished(final float pDistanceX, final float pDistanceY) {
        this.mTriggering = false;
        this.mLastVector = null;
        if (this.mPointerID != TouchEvent.INVALID_POINTER_ID) {
//            this.mScrollDetectorListener.onScrollFinished(this, this.mPointerID, pDistanceX, pDistanceY);
        }
    }

    private void triggerOnScrollVector(final float pDistanceX, final float pDistanceY) {

        Point vector;
        if (Math.abs(pDistanceX) > Math.abs(pDistanceY)) {
            if (pDistanceX > 0)
                vector = new Point(1, 0);
            else
                vector = new Point(-1, 0);
        } else {
            if (pDistanceY > 0)
                vector = new Point(0, 1);
            else
                vector = new Point(0, -1);
        }
        this.mLastVector = vector;
        this.mScrollDetectorListener.onScrollVector(this, this.mPointerID, vector);
    }

    protected float getX(final TouchEvent pTouchEvent) {
        return pTouchEvent.getX();
    }

    protected float getY(final TouchEvent pTouchEvent) {
        return pTouchEvent.getY();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static interface IScrollDetectorListener {
        // ===========================================================
        // Constants
        // ===========================================================

        // ===========================================================
        // Methods
        // ===========================================================

//        public void onScrollStarted(final GameDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY);

//        public void onScroll(final GameDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY);

//        public void onScrollFinished(final GameDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY);

        public void onScrollVector(final LongScrollDetector pScollDetector, final int pPointerID, final Point vector);
    }
}
