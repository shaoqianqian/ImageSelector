package lme.net.multiimageselector.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 文件夹
 */
public class SelectedImages implements Serializable {
    public ArrayList<SelectedImage> listSelectedImage;
    public ArrayList<String> getListPath(){
        if(null==listSelectedImage||listSelectedImage.size()==0){
            return null;
        }
        ArrayList<String> list=new ArrayList<>();
        for (int i=0;i<listSelectedImage.size();i++){
            list.add(listSelectedImage.get(i).path);
        }
        return list;
    }

    /**
     * 返回可以编辑的图片路径
     * 如果已经生成过滤镜图片
     * 则使用滤镜图片
     * 否则使用原图片
     * @return
     */
    public String getEditPath(int position){
        if(null==listSelectedImage||position>=listSelectedImage.size()){
            return "";
        }
        SelectedImage  selectedImage=listSelectedImage.get(position);
        return TextUtils.isEmpty(selectedImage.filter_path)?selectedImage.path:selectedImage.filter_path;
    }
    public String getEditPath(String path){
        if(null==listSelectedImage){
            return path;
        }
        for (int i=0;i<listSelectedImage.size();i++){
            SelectedImage  selectedImage=listSelectedImage.get(i);
            if(path.equals(selectedImage.path)){
                return  TextUtils.isEmpty(selectedImage.filter_path)?selectedImage.path:selectedImage.filter_path;
            }
        }
        return path;
    }

    public int getCount(){
        return null==listSelectedImage?0:listSelectedImage.size();
    }
    public void remove(int position){
        if(null==listSelectedImage||listSelectedImage.size()<=position){
            return ;
        }
        listSelectedImage.remove(position);
    }
    public void remove(String path){
        if(null==listSelectedImage){
            return ;
        }
        int position=-1;
        for (int i=0;i<listSelectedImage.size();i++){
            if(listSelectedImage.get(i).path.equals(path)){
                position=i;
            }
        }
        if(position>-1){
            listSelectedImage.remove(position);
        }
    }
    public void add(String path){
        if(null==listSelectedImage){
            listSelectedImage=new ArrayList<>();
        }
        listSelectedImage.add(new SelectedImage(path, null));
    }
    public boolean contains(String path){
        if(null==listSelectedImage){
            return false;
        }
        for (int i=0;i<listSelectedImage.size();i++){
            if(listSelectedImage.get(i).path.equals(path)){
                return true;
            }
        }
        return false;
    }

    /**
     * 返回可以预览的图片
     * @return
     */
    public ArrayList<String> getGestureList(){
        if(null==listSelectedImage||listSelectedImage.size()==0){
            return null;
        }
        ArrayList<String> list=new ArrayList<>();
        for (int i=0;i<listSelectedImage.size();i++){
            SelectedImage selectedImage=listSelectedImage.get(i);
            list.add(TextUtils.isEmpty(selectedImage.filter_path)?selectedImage.path:selectedImage.filter_path);
        }
        return list;
    }
    public void add(SelectedImage selectedImage){
        if(null==selectedImage){
            return;
        }
        if(null==listSelectedImage){
            listSelectedImage=new ArrayList<>();
        }
        listSelectedImage.add(selectedImage);
    }
}
