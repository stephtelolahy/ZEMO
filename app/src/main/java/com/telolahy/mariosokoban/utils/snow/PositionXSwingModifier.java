package com.telolahy.mariosokoban.utils.snow;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;

/**
 * Created by stephanohuguestelolahy on 12/4/14.
 */
public class PositionXSwingModifier extends SingleValueSpanEntityModifier {

    private float mInitialX;

    private float mFromMagnitude;
    private float mToMagnitude;


    public PositionXSwingModifier(float pDuration, float pFromValue, float pToValue,
                                  float pFromMagnitude, float pToMagnitude) {
        super(pDuration, pFromValue, pToValue);
        mFromMagnitude = pFromMagnitude;
        mToMagnitude = pToMagnitude;
    }

    @Override
    protected void onSetInitialValue(IEntity pItem, float pValue) {
        mInitialX = pItem.getX();

    }

    @Override
    protected void onSetValue(IEntity pItem, float pPercentageDone, float pValue) {
        float currentMagnitude = mFromMagnitude + (mToMagnitude - mFromMagnitude) * pPercentageDone;
        float currentSinValue = (float) Math.sin(pValue);
        pItem.setX(mInitialX + currentMagnitude * currentSinValue);
    }

    @Override
    public PositionXSwingModifier deepCopy() throws DeepCopyNotSupportedException {
        throw new DeepCopyNotSupportedException();
    }
}
