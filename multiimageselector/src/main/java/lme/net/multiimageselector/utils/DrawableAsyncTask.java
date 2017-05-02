package lme.net.multiimageselector.utils;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class DrawableAsyncTask extends AsyncTask {
	private Handler handler;
	private String filePath;
	private String TAG="DrawableAsyncTask";
	public DrawableAsyncTask(Handler handler) {
		super();
		this.handler = handler;
	}

	@Override
	protected Object doInBackground(Object... params) {
		this.filePath = (String) params[0];
		int widht=(Integer) params[1];
		int height=(Integer) params[2];
		Drawable drawable=XxtBitmapUtil.getDrawable(filePath,widht,height);
		Message msg=handler.obtainMessage();
		msg.what=Constants.GET_DRAWABLE_SUCCESS;
		msg.obj=drawable;
		handler.sendMessage(msg);
		return null;
	}

}
