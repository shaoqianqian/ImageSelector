package lme.net.multiimageselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import lme.net.multiimageselector.R;
import lme.net.multiimageselector.utils.ImageLoadUtil;
import lme.net.multiimageselector.view.CirclePageIndicator;
import lme.net.multiimageselector.view.HackyViewPager;


/**
 * 预览图片
 */
public class GestureImageActivity extends Activity {
    private ArrayList<String> resultList = new ArrayList<>();
    private HackyViewPager myViewPager;
    private MyPagerAdapter myAdapter;
    private CirclePageIndicator circleLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_image_layout);
        Intent intent = getIntent();
        if (intent.hasExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST);
        }
        initView();
    }

    public void initView() {
        myViewPager = (HackyViewPager) findViewById(R.id.viewpagerLayout);
        myAdapter = new MyPagerAdapter();
        myAdapter.setData(resultList);
        myViewPager.setAdapter(myAdapter);
        circleLay = (CirclePageIndicator) findViewById(R.id.indicator);
        circleLay.setViewPager(myViewPager);
    }

    private class MyPagerAdapter extends PagerAdapter {
        private List<String> listPath;
        private LayoutInflater inflater;

        MyPagerAdapter() {
            inflater = getLayoutInflater();
        }

        public void setData(ArrayList<String> listPath) {
            this.listPath = listPath;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return null == listPath ? 0 : listPath.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.sns_viewatt_image);
            ImageLoadUtil.loadPhoto(GestureImageActivity.this,listPath.get(position),imageView);
            container.addView(imageLayout, 0);
            return imageLayout;
        }
    }

    private void onSure() {
        Intent intent = new Intent();
        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, resultList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
