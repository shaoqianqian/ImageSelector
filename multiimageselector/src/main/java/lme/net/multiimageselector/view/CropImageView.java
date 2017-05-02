package lme.net.multiimageselector.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import lme.net.multiimageselector.utils.XxtBitmapUtil;

/**
 * Created by Administrator on 2016/6/20.
 */
public class CropImageView extends ImageView {
    // 单点触摸的时候
    private float oldX = 0;
    private float oldY = 0;

    // 多点触摸的时候
    private float oldx_0 = 0;
    private float oldy_0 = 0;

    private float oldx_1 = 0;
    private float oldy_1 = 0;
    //多点缩放时上一次的位置
    private int old_left = 0;
    private int old_right = 0;
    private int old_top = 0;
    private int old_bottom = 0;
    // 状态
    private final int STATUS_Touch_SINGLE = 1;// 单点
    private final int STATUS_TOUCH_MULTI_START = 2;// 多点开始
    private final int STATUS_TOUCH_MULTI_TOUCHING = 3;// 多点拖拽中

    private int mStatus = STATUS_Touch_SINGLE;

    // 默认的裁剪图片宽度与高度
    private final int defaultCropWidth = 300;
    private final int defaultCropHeight = 300;
    private int cropWidth = defaultCropWidth;
    private int cropHeight = defaultCropHeight;

    protected float oriRationWH = 0;// 原始宽高比率
    protected final float maxZoomOut = 5.0f;// 最大扩大到多少倍
    protected final float minZoomIn = 0.3333f;// 最小缩小到多少倍

    protected Drawable mDrawable;// 原图
    protected FloatDrawable mFloatDrawable;// 浮层
    protected Rect mDrawableSrc = new Rect();
    protected Rect mDrawableDst = new Rect();
    protected Rect mDrawableFloat = new Rect();// 浮层选择框，就是头像选择框
    protected boolean isFrist = true;

    protected Context mContext;

    public CropImageView(Context context) {
        super(context);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        this.mContext = context;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                this.setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFloatDrawable = new FloatDrawable(context);// 头像选择框
    }

    public void setDrawable(Drawable mDrawable, int cropWidth, int cropHeight) {
        this.mDrawable = mDrawable;
        this.cropWidth = cropWidth>getWidth()?getWidth():cropWidth;
        this.cropHeight=this.cropWidth;
        if (mDrawable.getIntrinsicWidth() < cropWidth|| mDrawable.getIntrinsicHeight() < cropHeight) {
            this.mDrawable = XxtBitmapUtil.zoomDrawable(mDrawable,cropWidth);
        }
        this.isFrist = true;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            if (mStatus == STATUS_Touch_SINGLE) {
                mStatus = STATUS_TOUCH_MULTI_START;

                oldx_0 = event.getX(0);
                oldy_0 = event.getY(0);

                oldx_1 = event.getX(1);
                oldy_1 = event.getY(1);
            } else if (mStatus == STATUS_TOUCH_MULTI_START) {
                mStatus = STATUS_TOUCH_MULTI_TOUCHING;
            }
        } else {
            if (mStatus == STATUS_TOUCH_MULTI_START|| mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                oldx_0 = 0;
                oldy_0 = 0;

                oldx_1 = 0;
                oldy_1 = 0;

                oldX = event.getX();
                oldY = event.getY();
            }

            mStatus = STATUS_Touch_SINGLE;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();
                oldY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                checkBounds();
                break;

            case MotionEvent.ACTION_POINTER_1_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                if (mStatus == STATUS_TOUCH_MULTI_TOUCHING) {// 多点拖拽
                    float newx_0 = event.getX(0);
                    float newy_0 = event.getY(0);

                    float newx_1 = event.getX(1);
                    float newy_1 = event.getY(1);

                    float oldWidth = Math.abs(oldx_1 - oldx_0);
                    float oldHeight = Math.abs(oldy_1 - oldy_0);
                    float newWidth = Math.abs(newx_1 - newx_0);
                    float newHeight = Math.abs(newy_1 - newy_0);
                    boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math.abs(newWidth - oldWidth);
                    float ration = isDependHeight ? (newHeight / oldHeight): (newWidth / oldWidth);

                    int centerX = mDrawableDst.centerX();
                    int centerY = mDrawableDst.centerY();
                    int _newWidth = (int) (mDrawableDst.width() * ration);
                    int _newHeight = (int) ((float) _newWidth / oriRationWH);

                    float tmpZoomRation = (float) _newWidth/ (float) mDrawableSrc.width();
                    tmpZoomRation = (float) _newHeight/ (float) mDrawableSrc.height();
                    if (tmpZoomRation >= maxZoomOut) {
                        _newWidth = (int) (maxZoomOut * mDrawableSrc.width());
                        _newHeight = (int) ((float) _newWidth / oriRationWH);
                    } else if (tmpZoomRation <= minZoomIn) {
                        _newWidth = (int) (minZoomIn * mDrawableSrc.width());
                        _newHeight = (int) ((float) _newWidth / oriRationWH);
                    }
                    int left = 0, right = 0, bottom = 0, top = 0;
                    if ((left = centerX - _newWidth / 2) > mDrawableFloat.left) {
                        left = mDrawableFloat.left;
                    }
                    if ((right = centerX + _newWidth / 2) < mDrawableFloat.right) {
                        right = mDrawableFloat.right;
                    }
                    if ((bottom = centerY + _newHeight / 2) < mDrawableFloat.bottom) {
                        bottom = mDrawableFloat.bottom;
                    }
                    if ((top = centerY - _newHeight / 2) > mDrawableFloat.top) {
                        top = mDrawableFloat.top;
                    }
                    if(old_left==left||old_right==right||old_bottom==bottom||old_top==top){
                        break;
                    }
                    old_left=left;
                    old_right=right;
                    old_bottom=bottom;
                    old_top=top;
                    mDrawableDst.set(left, top, right, bottom);
                    invalidate();

                    oldx_0 = newx_0;
                    oldy_0 = newy_0;

                    oldx_1 = newx_1;
                    oldy_1 = newy_1;
                } else if (mStatus == STATUS_Touch_SINGLE) {// 单点拖拽
                    int dx = (int) (event.getX() - oldX);
                    int dy = (int) (event.getY() - oldY);

                    oldX = event.getX();
                    oldY = event.getY();
                    if (mDrawableDst.left + dx > mDrawableFloat.left) {
                        dx = mDrawableFloat.left - mDrawableDst.left;
                    }
                    if (mDrawableDst.right + dx < mDrawableFloat.right) {
                        dx = mDrawableFloat.right - mDrawableDst.right;
                    }
                    if (mDrawableDst.bottom + dy < mDrawableFloat.bottom) {
                        dy = mDrawableFloat.bottom - mDrawableDst.bottom;
                    }
                    if (mDrawableDst.top + dy > mDrawableFloat.top) {
                        dy = mDrawableFloat.top - mDrawableDst.top;
                    }
                    if (!(dx == 0 && dy == 0)) {
                        mDrawableDst.offset(dx, dy);
                        invalidate();
                    }
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0
                || mDrawable.getIntrinsicHeight() == 0) {
            return; // nothing to draw (empty bounds)
        }

        configureBounds();
        mDrawable.draw(canvas);
        canvas.save();
        canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mFloatDrawable.draw(canvas);
    }

    protected void configureBounds() {
        if (isFrist) {
            oriRationWH = ((float) mDrawable.getIntrinsicWidth())/ ((float) mDrawable.getIntrinsicHeight());
            int w=cropWidth;
            int h = (int) (w / oriRationWH);
            if(mDrawable.getIntrinsicWidth()>mDrawable.getIntrinsicHeight()){
                h=cropHeight;
                w=(int) (h*oriRationWH);
            }
            int left = (getWidth() - w) / 2;
            int top = (getHeight() - h) / 2;
            int right = left + w;
            int bottom = top + h;
            mDrawableSrc.set(left, top, right, bottom);
            mDrawableDst.set(mDrawableSrc);


            int floatWidth = cropWidth;
            int floatHeight = cropHeight;

            if (floatWidth > getWidth()) {
                floatWidth = getWidth();
                floatHeight = cropHeight * floatWidth / cropWidth;
            }

            if (floatHeight > getHeight()) {
                floatHeight = getHeight();
                floatWidth = cropWidth * floatHeight / cropHeight;
            }

            int floatLeft = (getWidth() - floatWidth) / 2;
            int floatTop = (getHeight() - floatHeight) / 2;
            mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth,floatTop + floatHeight);

            isFrist = false;
        }

        mDrawable.setBounds(mDrawableDst);
        mFloatDrawable.setBounds(mDrawableFloat);
    }

    protected void checkBounds() {
        int newLeft = mDrawableDst.left;
        int newTop = mDrawableDst.top;

        boolean isChange = false;
        if (mDrawableDst.left < -mDrawableDst.width()) {
            newLeft = -mDrawableDst.width();
            isChange = true;
        }

        if (mDrawableDst.top < -mDrawableDst.height()) {
            newTop = -mDrawableDst.height();
            isChange = true;
        }

        if (mDrawableDst.left > getWidth()) {
            newLeft = getWidth();
            isChange = true;
        }

        if (mDrawableDst.top > getHeight()) {
            newTop = getHeight();
            isChange = true;
        }

        mDrawableDst.offsetTo(newLeft, newTop);
        if (isChange) {
            invalidate();
        }
    }

    public Bitmap getCropImage() {
        Bitmap tmpBitmap = XxtBitmapUtil.createBitmap(getWidth(), getHeight());
        if(null==tmpBitmap){
            return null;
        }
        Canvas canvas = new Canvas(tmpBitmap);
        if(null==canvas||null==mDrawable){
            return null;
        }
        mDrawable.draw(canvas);

        Matrix matrix = new Matrix();
        float scale = (float) (mDrawableSrc.width())/ (float) (mDrawableDst.width());
        matrix.postScale(scale, scale);

        Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left,
                mDrawableFloat.top, mDrawableFloat.width(),
                mDrawableFloat.height(), matrix, true);
        tmpBitmap.recycle();
        tmpBitmap = null;

        Bitmap newRet = Bitmap.createScaledBitmap(ret, cropWidth, cropHeight,
                false);
        ret.recycle();
        ret = newRet;

        return ret;
    }
}
