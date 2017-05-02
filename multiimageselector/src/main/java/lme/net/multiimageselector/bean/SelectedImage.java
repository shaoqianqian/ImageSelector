package lme.net.multiimageselector.bean;

import java.io.Serializable;


/**
 * 文件夹
 */
public class SelectedImage implements Serializable {
    public String path;
    public String filter_path;//滤镜处理过的图片
    public SelectedImage(String path,String filter_path) {
        this.path = path;
        this.filter_path=filter_path;
    }
}
