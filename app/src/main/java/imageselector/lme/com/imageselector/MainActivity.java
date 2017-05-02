package imageselector.lme.com.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import lme.net.multiimageselector.MultiImageSelectorActivity;
import lme.net.multiimageselector.bean.SelectedImages;
import lme.net.multiimageselector.utils.ImageSelector;
import lme.net.multiimageselector.utils.MultiSelectorUtils;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.select_one_btn).setOnClickListener(this);
        findViewById(R.id.select_one_not_camera_btn).setOnClickListener(this);
        findViewById(R.id.select_more_btn).setOnClickListener(this);
        findViewById(R.id.select_more_not_camera_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_one_btn:
                  MultiSelectorUtils.selectImage(this,new ImageSelector.Builder().editMode(ImageSelector.EDIT_NOTHING_MODE).build());
                break;
            case R.id.select_one_not_camera_btn:
                MultiSelectorUtils.selectImage(this,new ImageSelector.Builder().showCamera(false).build());
                break;
            case R.id.select_more_btn:
                MultiSelectorUtils.selectImage(this,new ImageSelector.Builder().selectedMode(ImageSelector.MODE_MULTI).build());
                break;
            case R.id.select_more_not_camera_btn:
                MultiSelectorUtils.selectImage(this,new ImageSelector.Builder().selectedMode(ImageSelector.MODE_MULTI).showCamera(false).build());
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MultiSelectorUtils.SELECTOR_REQUES_CODE && data != null) {
                SelectedImages selectedImages = (SelectedImages) data.getSerializableExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                ArrayList<String> listPath = selectedImages.getListPath();
                //处理图片
                StringBuilder sb=new StringBuilder();
                if (null==listPath||listPath.size()==0){
                    sb.append("没有选择图片");
                }else {
                    int size=listPath.size();
                    for (int i=0;i<size;i++){
                        sb.append(listPath.get(i));
                    }
                }
                TextView textView= (TextView) findViewById(R.id.show_select_image);
                textView.setText(sb.toString());
            }
        }
    }
}
