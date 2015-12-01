package grandroid.fancyview.zoomableimage;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import java.util.ArrayList;

public class ImageZoomView extends View implements Observer {

    protected final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    protected final RectF mRectSrc = new RectF();
    protected final RectF mRectDst = new RectF();
    protected float mAspectQuotient;
    protected Bitmap mBitmap;
    protected ZoomState mState;
    protected SimpleZoomListener mZoomListener;
    protected Matrix currMatrix;
    protected boolean debug;
    protected int gravity;

    public ImageZoomView(Context context) {
        super(context);
        gravity = Gravity.CENTER;
    }

    public void debug() {
        debug = true;
    }

    public void setZoomState(ZoomState state) {
        if (mState != null) {
            mState.deleteObserver(this);
        }
        mState = state;
        mState.addObserver(this);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null && mState != null) {
            if (mState.getZoom() <= 1.0) {
                mState.setPanX(0.5f);
                mState.setPanY(0.5f);
                mState.setZoom(1);
            }
            final int viewWidth = getWidth();
            final int viewHeight = getHeight();
            final int bitmapWidth = mBitmap.getWidth();
            final int bitmapHeight = mBitmap.getHeight();

            final float zoomX = mState.getZoomX(mAspectQuotient) * viewWidth
                    / bitmapWidth;
            final float zoomY = mState.getZoomY(mAspectQuotient) * viewHeight
                    / bitmapHeight;

            RectF rBoundary = getPanBoundary(bitmapWidth, bitmapHeight, viewWidth, viewHeight, zoomX, zoomY);
            if (rBoundary.width() > 0) {
                if (mState.getPanX() < rBoundary.left) {
                    mState.mPanX = rBoundary.left;
                } else if (mState.getPanX() > rBoundary.right) {
                    mState.mPanX = rBoundary.right;
                }
            } else {
                if (mState.getPanX() > rBoundary.left) {
                    mState.mPanX = rBoundary.left;
                } else if (mState.getPanX() < rBoundary.right) {
                    mState.mPanX = rBoundary.right;
                }
            }
            if (rBoundary.height() > 0) {
                if (mState.getPanY() >= rBoundary.bottom && mState.getPanY() <= rBoundary.top) {
                    if (gravity == Gravity.TOP) {
                        mState.mPanY = rBoundary.top;
                    } else if (gravity == Gravity.BOTTOM) {
                        mState.mPanY = rBoundary.bottom;
                    } else {
                        mState.mPanY = rBoundary.centerY();
                    }

                } else if (mState.getPanY() > rBoundary.bottom) {
                    mState.mPanY = rBoundary.bottom;
                } else if (mState.getPanY() < rBoundary.top) {
                    mState.mPanY = rBoundary.top;
                }
            } else {
                if (gravity == Gravity.TOP) {
                    mState.mPanY = rBoundary.top;
                } else if (gravity == Gravity.BOTTOM) {
                    mState.mPanY = rBoundary.bottom;
                } else {
                    mState.mPanY = rBoundary.centerY();
                }
            }
            final float panX = mState.getPanX();
            final float panY = mState.getPanY();
            // Setup source and destination rectangles  
            mRectSrc.left = (int) (panX * bitmapWidth - viewWidth / (zoomX * 2));
            mRectSrc.top = (int) (panY * bitmapHeight - viewHeight
                    / (zoomY * 2));
            mRectSrc.right = (int) (mRectSrc.left + viewWidth / zoomX);
            mRectSrc.bottom = (int) (mRectSrc.top + viewHeight / zoomY);

            mRectDst.left = getLeft();
            mRectDst.top = getTop();
            mRectDst.right = getRight();
            mRectDst.bottom = getBottom();

            // Adjust source rectangle so that it fits within the source image.  
            if (mRectSrc.left < 0) {
                mRectDst.left += -mRectSrc.left * zoomX;
                mRectSrc.left = 0;
            }
            if (mRectSrc.right > bitmapWidth) {
                mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
                mRectSrc.right = bitmapWidth;
            }
            if (mRectSrc.top < 0) {
                mRectDst.top += -mRectSrc.top * zoomY;
                mRectSrc.top = 0;
            }
            if (mRectSrc.bottom > bitmapHeight) {
                mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
                mRectSrc.bottom = bitmapHeight;
            }

//            canvas.drawBitmap(mBitmap, mRectSrc, mRectDst, mPaint);  
            Rect rSrc = new Rect();
            mRectSrc.round(rSrc);
            Rect rDst = new Rect();
            mRectDst.round(rDst);
            canvas.drawBitmap(mBitmap, rSrc, rDst, mPaint);

        }
    }

    public void update(Observable observable, Object data) {
        if (data == null) {
            invalidate();
        } else {
            mState.setPanX(0.5f);
            mState.setPanY(0.5f);
            mState.setZoom(1);
            mState.notifyObservers();
        }
    }

    private void calculateAspectQuotient() {
        if (mBitmap != null) {
            mAspectQuotient
                    = (((float) mBitmap.getWidth()) / mBitmap.getHeight())
                    / (((float) getWidth()) / getHeight());
        }
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        calculateAspectQuotient();
        invalidate();

        mState = new ZoomState();
        setZoomState(mState);
        mZoomListener = new SimpleZoomListener();
        mZoomListener.setZoomState(mState);
        setOnTouchListener(mZoomListener);
        mState.setPanX(0.5f);
        mState.setPanY(0.5f);
        mState.setZoom(1f);
        mState.notifyObservers();
    }

    public ZoomState getZoomState() {
        return mState;
    }

    public RectF getPanBoundary(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight, float zoomX, float zoomY) {
        RectF r = new RectF();

        r.left = (viewWidth / (zoomX * 2)) / bitmapWidth;
        r.top = (viewHeight / (zoomY * 2)) / bitmapHeight;
        r.right = 0.5f + (0.5f - r.left);
        r.bottom = 0.5f + (0.5f - r.top);
        //Log.d("grandroid", "r.left=" + r.left + " , r.top=" + r.top + ", r.right=" + r.right + ", r.bottom=" + r.bottom);
        return r;
    }

    public SimpleZoomListener getZoomListener() {
        return mZoomListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calculateAspectQuotient();
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }
}
