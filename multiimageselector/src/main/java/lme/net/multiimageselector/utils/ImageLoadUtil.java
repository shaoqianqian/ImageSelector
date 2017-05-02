package lme.net.multiimageselector.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import lme.net.multiimageselector.R;


/**
 * Created by lcp on 2016/7/27.
 */
public class ImageLoadUtil {

    public static void load(Context context, String path, ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }

    public static void load(Activity context, String path, ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }

    public static void load(FragmentActivity context, String path, ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }

    public static void load(Fragment context, String path, ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }

    public static void load(android.app.Fragment context, String path, ImageView imageView){
        Glide.with(context).load(path).into(imageView);
    }

    /*----------------圆角--------------*/
    public static void loadRound(Context context, String path, ImageView imageView){
        Glide.with(context).load(path).transform(new GlideRoundTransform(context))
                .placeholder(R.drawable.load_fail).into(imageView);
    }


    /*------------加载头像  圆形-----------------*/

    public static void loadAvatar(Activity context, String path, ImageView imageView){
        Glide.with(context).load(path).transform(new GlideCircleTransform(context))
                .placeholder(R.drawable.avatar).into(imageView);
    }

    public static void loadAvatar(Context context, String path, ImageView imageView){
        Glide.with(context).load(path).transform(new GlideCircleTransform(context))
                .placeholder(R.drawable.avatar).into(imageView);
    }

    public static void loadAvatar(Fragment context, String path, ImageView imageView){
        Glide.with(context).load(path).transform(new GlideCircleTransform(context.getContext()))
                .placeholder(R.drawable.avatar).into(imageView);
    }

    /*------------居中放大-----------------*/

    public static void loadFitCenter(Activity context, String path, ImageView imageView){
        Glide.with(context).load(path).fitCenter().into(imageView);
    }

    /*-------------查看相册 asBitmap----------------*/

    public static void loadPhoto(Activity context, String path, ImageView imageView){
        Glide.with(context).load(path).asBitmap().placeholder(R.drawable.photo_example).into(imageView);
    }

    public static void loadPhoto(Context context, String path, ImageView imageView){
        Glide.with(context).load(path).asBitmap().placeholder(R.drawable.photo_example).into(imageView);
    }

}
