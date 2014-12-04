package com.telolahy.mariosokoban.utils.snow;

import org.andengine.entity.IEntity;
import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.initializer.IParticleInitializer;

import java.util.Random;

/**
 * Created by stephanohuguestelolahy on 12/4/14.
 */
public class RegisterXSwingEntityModifierInitializer<T extends IEntity> implements
        IParticleInitializer<T> {

    private float mDuration;
    private float mFromValue;
    private float mToValue;
    private float mFromMagnitude;
    private float mToMagnitude;
    private boolean mRandomize;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public RegisterXSwingEntityModifierInitializer(float pDuration, float pFromValue, float pToValue,
                                                   float pFromMagnitude, float pToMagnitude,
                                                   boolean pRandomize) {
        mDuration = pDuration;
        mFromValue = pFromValue;
        mToValue = pToValue;
        mFromMagnitude = pFromMagnitude;
        mToMagnitude = pToMagnitude;
        mRandomize = pRandomize;
    }

    @Override
    public void onInitializeParticle(Particle<T> pParticle) {
        if (mRandomize) {
            pParticle.getEntity().registerEntityModifier(
                    new PositionXSwingModifier(mDuration,
                            mFromValue, mFromValue + RANDOM.nextFloat() * (mToValue - mFromValue),
                            mFromMagnitude, mFromMagnitude + RANDOM.nextFloat() * (mToMagnitude - mFromMagnitude)));
        } else {
            pParticle.getEntity().registerEntityModifier(
                    new PositionXSwingModifier(mDuration,
                            mFromValue, mToValue,
                            mFromMagnitude, mToMagnitude));
        }
    }
}
