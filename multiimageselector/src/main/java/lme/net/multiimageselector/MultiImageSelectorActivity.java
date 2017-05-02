package lme.net.multiimageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;

import java.io.File;

import lme.net.multiimageselector.R;
import lme.net.multiimageselector.bean.SelectedImage;
import lme.net.multiimageselector.bean.SelectedImages;
import lme.net.multiimageselector.utils.Constants;
import lme.net.multiimageselector.utils.ImageSelector;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment.Callback {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 裁剪图片的宽高
     */
    public static final String CUT_WIDTH = "cut_width";
    public static final String CUT_HEIGHT = "cut_height";

    public static final String EDIT_MODE = "edit_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    private SelectedImages selectedImages=new SelectedImages();
    private Button mSubmitButton;
    private int mDefaultCount;
    private int mode;
    private SelectedImage selectedImage;
    private int cut_width=640;
    private int cut_height=640;
    private int editMode;
    private  String cameraFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, ImageSelector.MAX_SELECT_COUNT);
        mode = intent.getIntExtra(EXTRA_SELECT_MODE, ImageSelector.MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == ImageSelector.MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            selectedImages=(SelectedImages)intent.getExtras().get(EXTRA_DEFAULT_SELECTED_LIST);
        }
        cut_width=intent.getIntExtra(CUT_WIDTH, 640);
        cut_height=intent.getIntExtra(CUT_HEIGHT, 640);
        editMode=intent.getIntExtra(EDIT_MODE,0);
        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putSerializable(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, selectedImages);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // 完成按钮
        mSubmitButton = (Button) findViewById(R.id.commit);
        if(mode==ImageSelector.MODE_SINGLE){//单选的时候，隐藏预览按钮
            mSubmitButton.setVisibility(View.GONE);
        }
        if (selectedImages == null || selectedImages.getCount()<= 0) {
            mSubmitButton.setText(R.string.ui_done);
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setText(getString(R.string.ui_done_percent,selectedImages.getCount()));
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImages == null || selectedImages.getCount() == 0) {
                    return;
                }
                // 返回已选择的图片数据
                Intent data = new Intent();
                data.putExtra(EXTRA_RESULT, selectedImages);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
    @Override
    public void onSingleImageSelected(SelectedImage sl) {
        if (sl == null) {
            return;
        }
        selectedImage=sl;
        if(editMode==ImageSelector.EDIT_NOTHING_MODE){
            setResult(selectedImage.path);
            return;
        }
        if(editMode== ImageSelector.HIGH_EDIT_MODE){
           // MultiSelectorUtils.openEditAdvanced(result,this,this);
            updateBg(selectedImage.path);
        }else if(editMode== ImageSelector.LOW_EDIT_MODE){
            //MultiSelectorUtils.openTuEditTurnAndCut(result, this, this, new TuSdkSize(cut_width, cut_height));
            updateBg(selectedImage.path);
        }else{
            setResult(selectedImage.path);
        }
    }
    private void updateBg(String srcRect) {
        File cache=Glide.getPhotoCacheDir(this);
        cameraFileName = cache.getAbsoluteFile()+ "/"+ System.currentTimeMillis() + ".png";
        Intent intent=new Intent(this,CropImageWidget.class);
        intent.putExtra("height",cut_height);
        intent.putExtra("width",cut_width);
        intent.putExtra("destRect",cameraFileName);
        intent.putExtra("srcRect",srcRect);
        startActivityForResult(intent,Constants.UPDATE_MYINFO_AVATAR);
    }
    @Override
    public void onImageSelected(SelectedImage selectedImage) {
        if (!selectedImages.contains(selectedImage.path)) {
            selectedImages.add(selectedImage);
        }
        // 有图片之后，改变按钮状态
        if (selectedImages.getCount() > 0) {
            mSubmitButton.setText(getString(R.string.ui_done_percent,selectedImages.getCount()));
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageReSelected(SelectedImage selectedImage) {
        if (!selectedImages.contains(selectedImage.path)) {
            selectedImages.add(selectedImage);
        }else{
            selectedImages.remove(selectedImage.path);
            selectedImages.add(selectedImage);
        }
        // 有图片之后，改变按钮状态
        if (selectedImages.getCount() > 0) {
            mSubmitButton.setText(getString(R.string.ui_done_percent,selectedImages.getCount()));
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(SelectedImage selectedImage) {
        if (selectedImages.contains(selectedImage.path)) {
            selectedImages.remove(selectedImage.path);
        }
        mSubmitButton.setText(getString(R.string.ui_done_percent,selectedImages.getCount()));
        // 当为选择图片时候的状态
        if (selectedImages.getCount() == 0) {
            mSubmitButton.setText(R.string.ui_done);
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile == null) {
            return;
        }
        selectedImage=new SelectedImage(imageFile.getAbsolutePath(),null);
        if(editMode== ImageSelector.HIGH_EDIT_MODE){
            //MultiSelectorUtils.openEditAdvanced(result,this,this);
            updateBg(selectedImage.path);
        }else if(editMode== ImageSelector.LOW_EDIT_MODE){
            //MultiSelectorUtils.openTuEditTurnAndCut(result, this, this, new TuSdkSize(cut_width, cut_height));
            updateBg(selectedImage.path);
        }else{
            setResult(selectedImage.path);
        }
    }
    private void setResult(String filter_path){
        selectedImage.filter_path = filter_path;
        selectedImages.add(selectedImage);
        Intent data = new Intent();
        data.putExtra(EXTRA_RESULT, selectedImages);
        setResult(RESULT_OK, data);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.UPDATE_MYINFO_AVATAR:
            selectedImage.filter_path = cameraFileName;
            selectedImages.add(selectedImage);
            Intent data1 = new Intent();
            data1.putExtra(EXTRA_RESULT, selectedImages);
            setResult(RESULT_OK, data1);
            finish();
        }
    }
}
