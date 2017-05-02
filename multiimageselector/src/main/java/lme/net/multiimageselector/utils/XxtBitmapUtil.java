package lme.net.multiimageselector.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 加载大图片工具类：解决android加载大图片时报OOM异常<br/>
 * 解决原理：先设置缩放选项，再读取缩放的图片数据到内存，规避了内存引起的OOM
 */
public class XxtBitmapUtil {
    /**
     * 保存图片到sd卡中 0 表示保存失败 1 表示保存成功
     */
    public static int saveBitmapToSD(Bitmap bitmap, String filePath) {
        if (null == bitmap) {
            return 0;
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            if (!bitmap.isRecycled()) {
                System.gc();
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static Drawable zoomDrawable(Drawable drawable, int w) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);// drawable转换成bitmap
        Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
        float scaleWidth;
        float scaleHeight;
        if (height > width) {
            scaleWidth = ((float) w / (float) width); // 计算缩放比例
            scaleHeight = (((float) w * (float) height) / (float) width / (float) height);
        } else {
            scaleHeight = ((float) w / (float) height); // 计算缩放比例
            scaleWidth = w * width / (float) height / width;
        }
        matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图、
        if (null != oldbmp && !oldbmp.isRecycled()) {
            oldbmp.recycle();
            oldbmp = null;
        }
        return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas); // 把drawable内容画到画布中
        return bitmap;
    }
    public static Bitmap createBitmap(int width, int height) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        if (null != bitmap) {
            return bitmap;
        }
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        } catch (OutOfMemoryError e) {
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        if (null != bitmap) {
            return bitmap;
        }
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        } catch (OutOfMemoryError e) {
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        if (null != bitmap) {
            return bitmap;
        }
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        } catch (OutOfMemoryError e) {
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        if (null != bitmap) {
            return bitmap;
        }
        return null;
    }
    public static Drawable getDrawable(String path, int width, int height) {
        Bitmap bmp = getBitmapFromSD(path, width, height);
        if (null == bmp) {
            return null;
        }
        return new BitmapDrawable(bmp);
    }
    public static Bitmap getBitmapFromSD(String srcFile, int width, int height) {
        // 读取本地图片
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(srcFile)) {
            // 判断文件是否存在
            File file=new File(srcFile);
            if (!file.exists()) {
                return bitmap;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 缩放的比例,表示缩放的倍数，值越地会导致值越不清晰
            opts.inSampleSize = getInSampleSize(srcFile, opts, width, height);
            opts.inJustDecodeBounds = false;
            bitmap = XxtBitmapUtil.safeDecodeBimtapFile(srcFile, opts);
            return bitmap;
        }
        return bitmap;
    }
    public static int getInSampleSize(String srcFile,
                                      BitmapFactory.Options opts, int width, int height) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(srcFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (null == input) {
            return 1;
        }
        return getInSampleSize(input, opts, width, height);
    }
    public static int getInSampleSize(FileInputStream input,
                                      BitmapFactory.Options opts, int width, int height) {
        opts.inJustDecodeBounds = true;// 只得到图片的高度和宽度
        BitmapFactory.decodeStream(input, null, opts);
        if (null != input) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (width <= 0 || height <= 0) {
            return 1;
        }
        // 压缩比例
        int tempSize = width > height ? height : width;
        int rate = 0;
        for (int i = 0; ; i++) {
            if ((opts.outWidth >> i <= tempSize)
                    && (opts.outHeight >> i <= tempSize)) {
                rate = i;
                break;
            }
        }
        if (rate != 0) {
            rate -= 1;
        }
        return (int) Math.pow(2, rate);
    }
    public static Bitmap safeDecodeBimtapFile(String bmpFile,
                                              BitmapFactory.Options opts) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(bmpFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return safeDecodeBimtapFile(input, opts);
    }
    public static Bitmap safeDecodeBimtapFile(InputStream input,
                                              BitmapFactory.Options opts) {
        if (null == input) {
            return null;
        }
        BitmapFactory.Options optsTmp = opts;
        if (optsTmp == null) {
            optsTmp = new BitmapFactory.Options();
            optsTmp.inSampleSize = 1;
            optsTmp.inJustDecodeBounds = false;
        }
        Bitmap bmp = null;
        for (int i = 0; i < 10; i++) {
            try {
                if (null == input) {
                    break;
                }
                bmp = BitmapFactory.decodeStream(input, null, optsTmp);
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                optsTmp.inSampleSize *= 2;
                try {
                    if (null != bmp && !bmp.isRecycled()) {
                        bmp.recycle();
                        bmp = null;
                    }
                    System.gc();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return bmp;
    }
}
