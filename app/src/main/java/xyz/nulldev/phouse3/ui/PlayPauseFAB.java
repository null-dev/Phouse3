package xyz.nulldev.phouse3.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import carbon.shadow.Shadow;
import carbon.shadow.ShadowGenerator;
import carbon.shadow.ShadowShape;
import carbon.shadow.ShadowView;
import xyz.nulldev.phouse3.R;

/**
 * SOURCE: https://github.com/alexjlockwood/material-pause-play-animation/blob/master/app/src/main/java/com/alexjlockwood/example/playpauseanimation/PlayPauseView.java
 *
 * Fully custom FloatingActionButton reimplemented from scratch with custom shadow rendering :P and swag animations
 */
public class PlayPauseFAB extends FrameLayout implements ShadowView {

    private static final Property<PlayPauseFAB, Integer> COLOR =
            new Property<PlayPauseFAB, Integer>(Integer.class, "color") {
                @Override
                public Integer get(PlayPauseFAB v) {
                    return v.getColor();
                }

                @Override
                public void set(PlayPauseFAB v, Integer value) {
                    v.setColor(value);
                }
            };

    private static final long PLAY_PAUSE_ANIMATION_DURATION = 400;

    private final PlayPauseDrawable mDrawable;
    private final Paint mPaint = new Paint();
    private final int mPauseBackgroundColor;
    private final int mPlayBackgroundColor;

    private AnimatorSet mAnimatorSet;
    private int mBackgroundColor;
    private int mWidth;
    private int mHeight;

    private float elevation = 0;
    private float translationZ = 0;
    private Shadow shadow;

    public PlayPauseFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mBackgroundColor = getResources().getColor(R.color.red);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mDrawable = new PlayPauseDrawable(context);
        mDrawable.setCallback(this);

        mPauseBackgroundColor = getResources().getColor(R.color.red);
        mPlayBackgroundColor = getResources().getColor(R.color.green);

        setElevation(12);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
        mWidth = w;
        mHeight = h;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            setClipToOutline(true);
        }
    }

    private void setColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    private int getColor() {
        return mBackgroundColor;
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBackgroundColor);
        final float radius = Math.min(mWidth, mHeight) / 2f;
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint);
        mDrawable.draw(canvas);
    }

    public void toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        final boolean isPlay = mDrawable.isPlay();
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, COLOR, isPlay ? mPauseBackgroundColor : mPlayBackgroundColor);
        colorAnim.setEvaluator(new ArgbEvaluator());
        final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
        mAnimatorSet.playTogether(colorAnim, pausePlayAnim);
        mAnimatorSet.start();
    }

    public boolean isPlaying() {
        return !mDrawable.isPlay();
    }

    public void setPlaying(boolean play) {
        if(isPlaying() != play) {
            toggle();
        }
    }

    //Elevation
    @Override
    public float getElevation() {
        return elevation;
    }

    public synchronized void setElevation(float elevation) {
        if (elevation == this.elevation)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.setElevation(elevation);
        this.elevation = elevation;
        if (getParent() != null)
            ((View) getParent()).postInvalidate();
    }

    @Override
    public float getTranslationZ() {
        return translationZ;
    }

    public synchronized void setTranslationZ(float translationZ) {
        if (translationZ == this.translationZ)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.setTranslationZ(translationZ);
        this.translationZ = translationZ;
        if (getParent() != null)
            ((View) getParent()).postInvalidate();
    }

    public ShadowShape getShadowShape() {
        int cornerRadius = 0;
        if (cornerRadius == getWidth() / 2 && getWidth() == getHeight())
            return ShadowShape.CIRCLE;
        return ShadowShape.RECT;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setTranslationZ(enabled ? 0 : -elevation);
    }

    public Shadow getShadow() {
        float elevation = getElevation() + getTranslationZ();
        if (elevation >= 0.01f && getWidth() > 0 && getHeight() > 0) {
            if (shadow == null || shadow.elevation != elevation)
                shadow = ShadowGenerator.generateShadow(this, elevation);
            return shadow;
        }
        return null;
    }

    public void invalidateShadow() {
        shadow = null;
        if (getParent() != null && getParent() instanceof View)
            ((View) getParent()).postInvalidate();
    }
}
