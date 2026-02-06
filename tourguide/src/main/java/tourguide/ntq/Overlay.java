package tourguide.ntq;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;

/**
 * {@link Overlay} shows a tinted background to cover up the rest of the screen. A 'hole' will be made on this overlay to let users obtain focus on the targeted element.
 */
public class Overlay {
    int mBackgroundColor;
    boolean mDisableClick;
    boolean mDisableClickThroughHole;
    Animation mEnterAnimation, mExitAnimation;
    View.OnClickListener mOnClickListener;

    public Overlay() {
        mDisableClick = true;
        mBackgroundColor = Color.parseColor("#CC000000");
    }

    public Overlay(boolean disableClick, int backgroundColor) {
        mDisableClick = disableClick;
        mBackgroundColor = backgroundColor;
    }

    /**
     * Set background color
     * @param backgroundColor
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setBackgroundColor(int backgroundColor){
        mBackgroundColor = backgroundColor;
        return this;
    }

    /**
     * Set to true if you want to block all user input to pass through this overlay, set to false if you want to allow user input under the overlay
     * @param yesNo
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay disableClick(boolean yesNo){
        mDisableClick = yesNo;
        return this;
    }

    /**
     * Set to true if you want to disallow the highlighted view to be clicked through the hole,
     * set to false if you want to allow the highlighted view to be clicked through the hole
     * @param yesNo
     * @return return Overlay instance for chaining purpose
     */
    public Overlay disableClickThroughHole(boolean yesNo){
        mDisableClickThroughHole = yesNo;
        return this;
    }

    /**
     * Set enter animation
     * @param enterAnimation
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setEnterAnimation(Animation enterAnimation){
        mEnterAnimation = enterAnimation;
        return this;
    }
    /**
     * Set exit animation
     * @param exitAnimation
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setExitAnimation(Animation exitAnimation){
        mExitAnimation = exitAnimation;
        return this;
    }

    /**
     * Set {@link Overlay#mOnClickListener} for the {@link Overlay}
     * @param onClickListener
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Overlay setOnClickListener(View.OnClickListener onClickListener){
        mOnClickListener=onClickListener;
        return this;
    }
}
