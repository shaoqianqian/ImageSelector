package lme.net.multiimageselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

import lme.net.multiimageselector.R;
import lme.net.multiimageselector.utils.Constants;
import lme.net.multiimageselector.utils.DrawableAsyncTask;
import lme.net.multiimageselector.utils.XxtBitmapUtil;
import lme.net.multiimageselector.view.CropImageView;
public class CropImageWidget extends Activity {

    private CropImageView mCropImage;
    private int height=640;
    private int width=640;
    private String destRect;
    private String srcRect;
    private MyHandler handler;
    private String TAG = "CropImageWidget";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_widget);
        handler=new MyHandler(this);
        initIntent();
        initView();
        initRMethod();
    }

    public void initIntent() {
        Intent intent = getIntent();
        if (null == intent) {
            finish();
        }
        height = intent.getIntExtra("height", 640);
        width = intent.getIntExtra("width", 640);
        destRect = intent.getStringExtra("destRect");
        srcRect = intent.getStringExtra("srcRect");
    }

    public void initView() {
        mCropImage = (CropImageView) findViewById(R.id.cropImg);
        findViewById(R.id.crop_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (XxtBitmapUtil.saveBitmapToSD(mCropImage.getCropImage(),
                                destRect) == 1) {
                            Intent mIntent = new Intent();
                            mIntent.putExtra("destRect",destRect);
                            setResult(RESULT_OK, mIntent);
                            finish();
                        }
                    }
                }).start();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initRMethod() {
        new DrawableAsyncTask(handler).execute(srcRect, width, height);
    }

    static class MyHandler extends Handler {
        WeakReference<CropImageWidget> mActivity;

        MyHandler(CropImageWidget activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CropImageWidget theActivity = mActivity.get();
            if (theActivity==null){
                return;
            }
            switch (msg.what) {
                case Constants.GET_DRAWABLE_SUCCESS:
                    Drawable drawable = (Drawable) msg.obj;
                    if (null == drawable) {
                        theActivity.finish();
                        break;
                    }
                    theActivity.mCropImage.setDrawable(drawable, theActivity.width, theActivity.height);
                    break;
                default:
                    break;

            }
        }
    }
}
