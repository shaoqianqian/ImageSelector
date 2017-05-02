package lme.net.multiimageselector.utils;

import java.io.Serializable;

import lme.net.multiimageselector.bean.SelectedImages;


public class ImageSelector implements Serializable {
    private boolean showCamera = true;//是否显示相机
    private int selectedMode = 0;//单选还是多选
    //单选
    public static final int MODE_SINGLE = 0;
    //多选
    public static final int MODE_MULTI = 1;

    private int editMode = 0;//编辑模式
    //高级编辑模式
    public static final int HIGH_EDIT_MODE = 0;
    //低级编辑模式
    public static final int LOW_EDIT_MODE = 1;
    //无编辑
    public static final int EDIT_NOTHING_MODE = 2;
    private int width = 480;
    private int height = 480;
    public static final int MAX_SELECT_COUNT = 9;
    private int maxNum = MAX_SELECT_COUNT;
    private SelectedImages selectedImages;//默认选中的图片

    private ImageSelector(Builder builder) {
        this.showCamera = builder.showCamera;
        this.selectedMode = builder.selectedMode;
        this.editMode = builder.editMode;
        this.width = builder.width;
        this.height = builder.height;
        this.maxNum = builder.maxNum;
        this.selectedImages = builder.selectedImages;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public int getSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(int selectedMode) {
        this.selectedMode = selectedMode;
    }

    public int getEditMode() {
        return editMode;
    }

    public void setEditMode(int editMode) {
        this.editMode = editMode;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public SelectedImages getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(SelectedImages selectedImages) {
        this.selectedImages = selectedImages;
    }

    public static class Builder {
        private boolean showCamera = true;//是否显示相机
        private int selectedMode = 0;//单选还是多选
        private int editMode = 0;//编辑模式
        private int width = 480;
        private int height = 480;
        private int maxNum = MAX_SELECT_COUNT;
        private SelectedImages selectedImages;

        public Builder() {
            this.showCamera = true;
            this.selectedMode = 0;
            this.editMode = 0;
            this.width = 480;
            this.height = 480;
        }


        public Builder showCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public Builder selectedMode(int selectedMode) {
            this.selectedMode = selectedMode;
            return this;
        }

        public Builder editMode(int editMode) {
            this.editMode = editMode;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder maxNum(int maxNum) {
            this.maxNum = maxNum;
            return this;
        }

        public Builder selectedImages(SelectedImages selectedImages) {
            this.selectedImages = selectedImages;
            return this;
        }

        public ImageSelector build() {
            return new ImageSelector(this);
        }
    }
}
