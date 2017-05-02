# ImageSelector
使用方法很简单

1.moudle的方式把multiimageselector加入到你的项目中（compile project(':multiimageselector')）

2.在你的项目中的AndroidMainfest.xml中加入

        <activity android:name="lme.net.multiimageselector.GestureImageActivity" />
        <activity android:name="lme.net.multiimageselector.MultiImageSelectorActivity"/>
        <activity android:name="lme.net.multiimageselector.CropImageWidget"/>
        
3.开始调用

  MultiSelectorUtils.selectImage(this,new ImageSelector.Builder().build());
  ImageSelector中可以配置是否显示相机，单选还是多选模式，是否是编辑模式，默认多选选择图片的数量
  
4.返回结果

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MultiSelectorUtils.SELECTOR_REQUES_CODE && data != null) {
                SelectedImages selectedImages = (SelectedImages) data.getSerializableExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            }
        }
    }
    
5.项目中有完整的使用例子
