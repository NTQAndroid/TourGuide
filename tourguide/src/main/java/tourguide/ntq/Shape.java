package tourguide.ntq;

/**
 * Created by HungHN on 5/9/17.
 */
public class Shape {

    public enum Style {
        CIRCLE, RECTANGLE, ROUNDED_RECTANGLE, NO_HOLE
    }

    public final static int NOT_SET = -1;
    public Style mStyle;
    public int mHoleOffsetLeft = 0;
    public int mHoleOffsetTop = 0;
    public int mHoleRadius = NOT_SET;
    public int mPaddingDp = 10;
    public int mRoundedCornerRadiusDp = 0;

    public Shape() {
        this.mStyle = Style.CIRCLE;
    }

    public Shape(Style style) {
        this.mStyle = style;
    }

    public Shape setStyle(Style style) {
        mStyle = style;
        return this;
    }

    /**
     * This method sets the hole's radius.
     * If this is not set, the size of view hole fill follow the max(view.width, view.height)
     * If this is set, it will take precedence
     * It only has effect when {@link Style#CIRCLE} is chosen
     *
     * @param holeRadius the radius of the view hole, setting 0 will make the hole disappear, in pixels
     * @return return {@link Overlay} instance for chaining purpose
     */
    public Shape setHoleRadius(int holeRadius) {
        mHoleRadius = holeRadius;
        return this;
    }


    /**
     * This method sets offsets to the hole's position relative the position of the targeted view.
     *
     * @param offsetLeft left offset, in pixels
     * @param offsetTop  top offset, in pixels
     * @return {@link Overlay} instance for chaining purpose
     */
    public Shape setHoleOffsets(int offsetLeft, int offsetTop) {
        mHoleOffsetLeft = offsetLeft;
        mHoleOffsetTop = offsetTop;
        return this;
    }

    /**
     * This method sets the padding to be applied to the hole cutout from the overlay
     *
     * @param paddingDp padding, in dp
     * @return {@link Overlay} intance for chaining purpose
     */
    public Shape setHolePadding(int paddingDp) {
        mPaddingDp = paddingDp;
        return this;
    }

    /**
     * This method sets the radius for the rounded corner
     * It only has effect when {@link Style#ROUNDED_RECTANGLE} is chosen
     *
     * @param roundedCornerRadiusDp padding, in pixels
     * @return {@link Overlay} intance for chaining purpose
     */
    public Shape setRoundedCornerRadius(int roundedCornerRadiusDp) {
        mRoundedCornerRadiusDp = roundedCornerRadiusDp;
        return this;
    }
}