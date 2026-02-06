package tourguide.ntq;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class FrameLayoutWithHole extends FrameLayout {
    private Activity mActivity;
    private TourGuide.MotionType mMotionType;
    private Paint mEraser;

    Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private ArrayList<View> mViewHoleList; // This is the targeted view to be highlighted, where the hole should be placed
    private ArrayList<Integer> mRadiusList;
    private ArrayList<int[]> mPosList;
    private float mDensity;
    private Overlay mOverlay;
    private ArrayList<Shape> mShapeList;
    private ArrayList<RectF> mRectFList;

    private ArrayList<AnimatorSet> mAnimatorSetArrayList;

    public void addAnimatorSet(AnimatorSet animatorSet) {
        if (mAnimatorSetArrayList == null) {
            mAnimatorSetArrayList = new ArrayList<>();
        }
        mAnimatorSetArrayList.add(animatorSet);
    }

    public void enforceMotionType() {
        if (mViewHoleList != null) {
            for (View view : mViewHoleList) {
                enforceMotionTypeForView(view);
            }
        }
    }

    private void enforceMotionTypeForView(final View viewHole) {
        Log.d("tourguide", "enforceMotionTypeForView 1");
        if (viewHole != null) {
            Log.d("tourguide", "enforceMotionTypeForView 2");
            if (mMotionType != null && mMotionType == TourGuide.MotionType.CLICK_ONLY) {
                Log.d("tourguide", "enforceMotionTypeForView 3");
                Log.d("tourguide", "only Clicking");
                viewHole.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        viewHole.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
            } else if (mMotionType != null && mMotionType == TourGuide.MotionType.SWIPE_ONLY) {
                Log.d("tourguide", "enforceMotionTypeForView 4");
                Log.d("tourguide", "only Swiping");
                viewHole.setClickable(false);
            }
        }
    }

    public FrameLayoutWithHole(Activity context, ArrayList<View> viewHoleList, TourGuide.MotionType motionType, Overlay overlay, ArrayList<Shape> shapeArrayList) {
        super(context);
        mActivity = context;
        init(null, 0);
        if(viewHoleList == null) {
            viewHoleList = new ArrayList<>();
        } else {
            mViewHoleList = viewHoleList;
        }
        enforceMotionType();
        mOverlay = overlay;
        mPosList = new ArrayList<>();
        mRectFList = new ArrayList<>();
        mRadiusList = new ArrayList<>();
        if (shapeArrayList == null) {
            mShapeList = new ArrayList<>();
        } else {
            mShapeList = shapeArrayList;
        }

        mDensity = context.getResources().getDisplayMetrics().density;
        int padding = (int) (20 * mDensity);

        for (View viewHole : mViewHoleList) {
            int[] pos = new int[2];
            viewHole.getLocationOnScreen(pos);
            mPosList.add(pos);
            int radius;
            if (viewHole.getHeight() > viewHole.getWidth()) {
                radius = viewHole.getHeight() / 2 + padding;
            } else {
                radius = viewHole.getWidth() / 2 + padding;
            }

            mRadiusList.add(radius);
        }

        mMotionType = motionType;

        // Init a RectF to be used in OnDraw for a ROUNDED_RECTANGLE Style Overlay
        for (int i = 0; i < mShapeList.size(); i++) {
            int[] pos = mPosList.get(i);
            View viewHole = mViewHoleList.get(i);
            Shape shape = shapeArrayList.get(i);
            if (shape != null && shape.mStyle == Shape.Style.ROUNDED_RECTANGLE) {
                int recfFPaddingPx = (int) (shape.mPaddingDp * mDensity);
                RectF rectF = new RectF(pos[0] - recfFPaddingPx + shape.mHoleOffsetLeft,
                        pos[1] - recfFPaddingPx + shape.mHoleOffsetTop,
                        pos[0] + viewHole.getWidth() + recfFPaddingPx + shape.mHoleOffsetLeft,
                        pos[1] + viewHole.getHeight() + recfFPaddingPx + shape.mHoleOffsetTop);
                mRectFList.add(rectF);
            } else {
                mRectFList.add(new RectF());
            }
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, FrameLayoutWithHole, defStyle, 0);
//
//
//        a.recycle();
        setWillNotDraw(false);
        // Set up a default TextPaint object
        TextPaint mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        Point size = new Point();
        size.x = mActivity.getResources().getDisplayMetrics().widthPixels;
        size.y = mActivity.getResources().getDisplayMetrics().heightPixels;

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);

        Paint mPaint = new Paint();
        mPaint.setColor(0xcc000000);
        Paint transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);

    }

    private boolean mCleanUpLock = false;

    protected void cleanUp() {
        if (getParent() != null) {
            if (mOverlay != null && mOverlay.mExitAnimation != null) {
                performOverlayExitAnimation(mOverlay);
            } else {
                ((ViewGroup) this.getParent()).removeView(this);
            }
        }
    }

    private void performOverlayExitAnimation(Overlay mOverlay) {
        if (!mCleanUpLock) {
            final FrameLayout _pointerToFrameLayout = this;
            mCleanUpLock = true;
            Log.d("tourguide", "Overlay exit animation listener is overwritten...");
            mOverlay.mExitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) _pointerToFrameLayout.getParent()).removeView(_pointerToFrameLayout);
                }
            });
            this.startAnimation(mOverlay.mExitAnimation);
        }
    }

    /* comment this whole method to cause a memory leak */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /* cleanup reference to prevent memory leak */
        mEraserCanvas.setBitmap(null);
        mEraserBitmap = null;

        if (mAnimatorSetArrayList != null && !mAnimatorSetArrayList.isEmpty()) {
            for (int i = 0; i < mAnimatorSetArrayList.size(); i++) {
                mAnimatorSetArrayList.get(i).end();
                mAnimatorSetArrayList.get(i).removeAllListeners();
            }
        }
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private static void dumpEvent(MotionEvent event) {
        String[] names = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d("tourguide", sb.toString());
    }

    private boolean isDisableClickThroughHole() {
        if (!mOverlay.mDisableClickThroughHole) {
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //first check if the location button should handle the touch event
        if (mViewHoleList != null) {
            if (isWithinButton(ev) && mOverlay != null && isDisableClickThroughHole()) {
                Log.d("tourguide", "block user clicking through hole");
                // block it
                return true;
            } else if (isWithinButton(ev)) {
                // let it pass through
                return false;
            }
        }
        // do nothing, just propagating up to super
        return super.dispatchTouchEvent(ev);
    }

    private boolean isWithinButton(MotionEvent ev) {
        for (View viewHole : mViewHoleList) {
            if (!isWithinButton(ev, viewHole)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinButton(MotionEvent ev, View mViewHole) {
        int[] pos = new int[2];
        mViewHole.getLocationOnScreen(pos);
        return ev.getRawY() >= pos[1] &&
                ev.getRawY() <= (pos[1] + mViewHole.getHeight()) &&
                ev.getRawX() >= pos[0] &&
                ev.getRawX() <= (pos[0] + mViewHole.getWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        if (mOverlay != null) {
            mEraserCanvas.drawColor(mOverlay.mBackgroundColor);
        }

        for (int i = 0; i < mShapeList.size(); i++) {
            Shape shape = this.mShapeList.get(i);
            View viewHole = mViewHoleList.get(i);
            int[] mPos = mPosList.get(i);
            RectF mRectF = mRectFList.get(i);
            int mRadius = mRadiusList.get(i);
            if (shape != null && viewHole != null) {
                int padding = (int) (shape.mPaddingDp * mDensity);
                if (shape.mStyle == Shape.Style.RECTANGLE) {
                    mEraserCanvas.drawRect(
                            mPos[0] - padding + shape.mHoleOffsetLeft,
                            mPos[1] - padding + shape.mHoleOffsetTop,
                            mPos[0] + viewHole.getWidth() + padding + shape.mHoleOffsetLeft,
                            mPos[1] + viewHole.getHeight() + padding + shape.mHoleOffsetTop, mEraser);

                } else if (shape.mStyle == Shape.Style.NO_HOLE) {
                    mEraserCanvas.drawCircle(
                            mPos[0] + viewHole.getWidth() / 2 + shape.mHoleOffsetLeft,
                            mPos[1] + viewHole.getHeight() / 2 + shape.mHoleOffsetTop,
                            0, mEraser);

                } else if (shape.mStyle == Shape.Style.ROUNDED_RECTANGLE) {
                    int roundedCornerRadiusPx;
                    if (shape.mRoundedCornerRadiusDp != 0) {
                        roundedCornerRadiusPx = (int) (shape.mRoundedCornerRadiusDp * mDensity);
                    } else {
                        roundedCornerRadiusPx = (int) (10 * mDensity);
                    }
                    mEraserCanvas.drawRoundRect(mRectF, roundedCornerRadiusPx, roundedCornerRadiusPx, mEraser);

                } else {
                    int holeRadius = shape.mHoleRadius != Shape.NOT_SET ? shape.mHoleRadius : mRadius;
                    mEraserCanvas.drawCircle(
                            mPos[0] + viewHole.getWidth() / 2 + shape.mHoleOffsetLeft,
                            mPos[1] + viewHole.getHeight() / 2 + shape.mHoleOffsetTop,
                            holeRadius, mEraser);

                }
            }
        }
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOverlay != null && mOverlay.mEnterAnimation != null) {
            this.startAnimation(mOverlay.mEnterAnimation);
        }
    }

    /**
     * Convenient method to obtain screen width in pixel
     *
     * @param activity
     * @return screen width in pixel
     */
    public int getScreenWidth(Activity activity) {
        return activity.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Convenient method to obtain screen height in pixel
     *
     * @param activity
     * @return screen width in pixel
     */
    public int getScreenHeight(Activity activity) {
        return activity.getResources().getDisplayMetrics().heightPixels;
    }
}
