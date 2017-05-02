package lme.net.multiimageselector.utils;

import android.app.Activity;
import android.content.Intent;


import lme.net.multiimageselector.MultiImageSelectorActivity;


/**
 * 裁剪
 */
public class MultiSelectorUtils {
    public static int SELECTOR_REQUES_CODE=1000;
    /**
     *
     * @param context
     * @param imageSelector
     */
    public static void selectImage(Activity context, ImageSelector imageSelector){
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, imageSelector.isShowCamera());
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, imageSelector.getSelectedMode());
        intent.putExtra(MultiImageSelectorActivity.EDIT_MODE, imageSelector.getEditMode());
        intent.putExtra(MultiImageSelectorActivity.CUT_WIDTH, imageSelector.getWidth());
        intent.putExtra(MultiImageSelectorActivity.CUT_HEIGHT, imageSelector.getHeight());
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, imageSelector.getMaxNum());
        // 默认选择
        if (imageSelector.getSelectedImages() != null && imageSelector.getSelectedImages().getCount() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, imageSelector.getSelectedImages());
        }
        (context).startActivityForResult(intent, SELECTOR_REQUES_CODE);
    }
}
