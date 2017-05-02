package lme.net.multiimageselector.utils;

import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * 时间处理工具
 */
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class TimeUtils {

    public static String timeFormat(long timeMillis, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time){
        return timeFormat(time, "yyyy-MM-dd");
    }

    public static String formatPhotoDate(String path){
        File file = new File(path);
        if(file.exists()){
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    public static String getAudioTime(long timeMillis){
        StringBuffer sb = new StringBuffer("");
        if(!TextUtils.isEmpty(timeMillis + "")){
            Date date = new Date(timeMillis);
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
            sb.append(format.format(date)).append("  ");
            if(date.getHours() < 12){
                sb.append("上午").append(date.getHours()).append(":").append(date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
            }else{
                sb.append("下午").append(date.getHours() == 12 ? 12 : date.getHours() - 12).append(":").append(date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
            }
        }
        return sb.toString();
    }
}
