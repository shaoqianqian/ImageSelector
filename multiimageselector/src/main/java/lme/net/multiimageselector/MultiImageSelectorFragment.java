package lme.net.multiimageselector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lme.net.multiimageselector.R;
import lme.net.multiimageselector.adapter.FolderAdapter;
import lme.net.multiimageselector.adapter.ImageGridAdapter;
import lme.net.multiimageselector.bean.Folder;
import lme.net.multiimageselector.bean.Image;
import lme.net.multiimageselector.bean.SelectedImage;
import lme.net.multiimageselector.bean.SelectedImages;
import lme.net.multiimageselector.utils.ImageSelector;
import lme.net.multiimageselector.utils.TimeUtils;

/**
 * 图片选择Fragment
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorFragment extends Fragment implements ImageGridAdapter.ImageSelectCallback{


    /**
     * 最大图片选择次数，int类型
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，int类型
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，boolean类型
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 默认选择的数据集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;
    //预览图片
    private static final int PREVIEW_IMAGE = REQUEST_CAMERA + 1;

    // 结果数据
    private SelectedImages selectedImages = new SelectedImages();
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    // 图片Grid
    private GridView mGridView;
    private Callback mCallback;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;
    private PopupWindow popupWindow;

    // 时间线
    private TextView mTimeLineText;
    // 类别
    private TextView mCategoryText;
    // 预览按钮
    private Button mPreviewBtn;
    // 底部View
    private View mPopupAnchorView;

    private int mDesireImageCount;

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;

    private int mGridWidth, mGridHeight;

    private File mTmpFile;
    private int mode;
    private Image editedImage;
    private ListView listView;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 选择图片数量
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        // 图片选择模式
        mode = getArguments().getInt(EXTRA_SELECT_MODE);

        // 默认选择
        if (mode == MODE_MULTI) {
            SelectedImages tmp = (SelectedImages) getArguments().get(EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.getCount() > 0) {
                selectedImages = tmp;
            }
        }

        // 是否显示照相机
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
        mImageAdapter = new ImageGridAdapter(getActivity(), mIsShowCamera);
        mImageAdapter.setImageSelectCallback(this);
        // 是否显示选择指示器
        mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

        mPopupAnchorView = view.findViewById(R.id.footer);

        mTimeLineText = (TextView) view.findViewById(R.id.timeline_area);
        // 初始化，先隐藏当前timeline
        mTimeLineText.setVisibility(View.GONE);

        mCategoryText = (TextView) view.findViewById(R.id.category_btn);
        // 初始化，加载所有图片
        mCategoryText.setText(R.string.folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (popupWindow == null) {
                    createPopupFolderList(mGridWidth, mGridHeight);
                }

                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAtLocation(mPopupAnchorView, Gravity.BOTTOM, 0, mCategoryText.getHeight());
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    listView.setSelection(index);
                }
            }
        });

        mPreviewBtn = (Button) view.findViewById(R.id.preview);
        if (mode == MODE_SINGLE) {//单选的时候，隐藏预览按钮
            mPreviewBtn.setVisibility(View.GONE);
        } else {
            mPreviewBtn.setVisibility(View.VISIBLE);
        }
        // 初始化，按钮状态初始化
        if (selectedImages == null || selectedImages.getCount() <= 0) {
            mPreviewBtn.setText(R.string.preview);
            mPreviewBtn.setEnabled(false);
        } else {
            mPreviewBtn.setEnabled(true);
            mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + selectedImages.getCount() + ")");
        }
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GestureImageActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, selectedImages.getGestureList());
                startActivity(intent);
            }
        });

        mGridView = (GridView) view.findViewById(R.id.grid);
        mGridView.setOnScrollListener(
                new PauseOnScrollListener(getActivity(), true, true,
                        new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (mTimeLineText.getVisibility() == View.VISIBLE) {
                            int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
                            Image image = (Image) view.getAdapter().getItem(index);
                            if (image != null) {
                                mTimeLineText.setText(TimeUtils.formatPhotoDate(image.path));
                            }
                        }
                    }
                }));
        mGridView.setAdapter(mImageAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = mGridView.getWidth();
                final int height = mGridView.getHeight();

                mGridWidth = width;
                mGridHeight = height;

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (width - columnSpace * (numCount - 1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mImageAdapter.isShowCamera()) {
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                    if (i == 0) {
                        showCameraAction();
                    }
                }
            }
        });
        mFolderAdapter = new FolderAdapter(getActivity());
    }
    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(int width, int height) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.multi_image_select_list_popup, null);
        listView = (ListView) contentView.findViewById(R.id.multi_image_list);
        listView.setAdapter(mFolderAdapter);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height * 5 / 8);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mFolderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.dismiss();

                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                            mCategoryText.setText(R.string.folder_all);
                            if (mIsShowCamera) {
                                mImageAdapter.setShowCamera(true);
                            } else {
                                mImageAdapter.setShowCamera(false);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.setData(folder.images);
                                mCategoryText.setText(folder.name);
                                // 设定默认选择
                                if (selectedImages != null && selectedImages.getCount() > 0) {
                                    mImageAdapter.setDefaultSelected(selectedImages.getListPath());
                                }
                            }
                            mImageAdapter.setShowCamera(false);
                        }

                        // 滑动到最初始位置
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        //new LoadImageTask().execute();
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile);
                    }
                }
            } else {
                if (mTmpFile != null && mTmpFile.exists()) {
                    mTmpFile.delete();
                }
            }
        } else if (requestCode == PREVIEW_IMAGE) {//预览照片回来
            // 设定默认选择
            if (selectedImages != null && selectedImages.getCount() > 0) {
                mImageAdapter.setDefaultSelected(selectedImages.getListPath());
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int height = mGridView.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = mGridView.getWidth() / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (mGridView.getWidth() - columnSpace * (numCount - 1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);

                if (popupWindow != null) {
                    popupWindow.setHeight(height * 5 / 8);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        super.onConfigurationChanged(newConfig);

    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        //多选模式，并且已经选择的最大数量等于最大图片数量限制
        if (mode== ImageSelector.MODE_MULTI&&mDesireImageCount == selectedImages.getCount()) {
            Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            File cacheDir=Glide.getPhotoCacheDir(getContext());
            mTmpFile = new File(cacheDir.getAbsolutePath() + "/"+System.currentTimeMillis()+".png");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 选择图片操作
     *
     * @param image
     */
    private void selectImageFromGrid(Image image) {
        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {
                if (selectedImages.contains(image.path)) {
                    selectedImages.remove(image.path);
                    if (selectedImages.getCount() != 0) {
                        mPreviewBtn.setEnabled(true);
                        mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + selectedImages.getCount() + ")");
                    } else {
                        mPreviewBtn.setEnabled(false);
                        mPreviewBtn.setText(R.string.preview);
                    }
                    if (mCallback != null) {
                        mCallback.onImageUnselected(new SelectedImage(image.path, null));
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount == selectedImages.getCount()) {
                        Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SelectedImage selectedImage = new SelectedImage(image.path, null);
                    selectedImages.add(new SelectedImage(image.path, null));
                    mPreviewBtn.setEnabled(true);
                    mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + selectedImages.getCount() + ")");
                    if (mCallback != null) {
                        mCallback.onImageSelected(selectedImage);
                    }
                }
                mImageAdapter.select(image);
            } else if (mode == MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    mCallback.onSingleImageSelected(new SelectedImage(image.path, null));
                }
            }
        }
    }

    private void componentFinished(Image image, SelectedImage selectedImage) {
        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {

                if (selectedImages.contains(image.path)) {
                    selectedImages.remove(image.path);
                    selectedImages.add(selectedImage);
                    if (mCallback != null) {
                        mCallback.onImageReSelected(selectedImage);
                    }
                } else {
                    // 判断选择数量问题
                    if (mDesireImageCount == selectedImages.getCount()) {
                        Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (null == selectedImage) {
                        selectedImage = new SelectedImage(image.path, null);
                    }
                    selectedImages.add(selectedImage);
                    mImageAdapter.select(image);
                    if (mCallback != null) {
                        mCallback.onImageSelected(selectedImage);
                    }
                }
                mPreviewBtn.setEnabled(true);
                mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + selectedImages.getCount() + ")");
            } else if (mode == MODE_SINGLE) {
                // 单选模式
                if (mCallback != null) {
                    if (null == selectedImage) {
                        selectedImage = new SelectedImage(image.path, null);
                    }
                    mCallback.onSingleImageSelected(selectedImage);
                }
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
            } else if (id == LOADER_CATEGORY) {
                return new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> images = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        if (TextUtils.isEmpty(path)) {
                            continue;
                        }
                        File file = new File(path);
                        if (!file.exists() || file.length() == 0) {
                            continue;
                        }
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);
                        if (!hasFolderGened) {
                            // 获取文件夹名称
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!mResultFolder.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                mResultFolder.add(folder);
                            } else {
                                // 更新
                                Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    } while (data.moveToNext());

                    mImageAdapter.setData(images);

                    // 设定默认选择
                    if (selectedImages != null && selectedImages.getCount() > 0) {
                        mImageAdapter.setDefaultSelected(selectedImages.getListPath());
                    }

                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    public void select(ImageGridAdapter imageGridAdapter, int position) {
        Image image = imageGridAdapter.getItem(position);
        selectImageFromGrid(image);
    }

    @Override
    public void edit(ImageGridAdapter imageGridAdapter, int position) {
        editedImage = imageGridAdapter.getItem(position);
        // 判断选择数量问题
        if (mDesireImageCount == selectedImages.getCount() && !imageGridAdapter.isSelect(editedImage)) {
            Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        String path;
        if (null == selectedImages) {
            path = editedImage.path;
        } else {//编辑已经编辑过的图片
            path = selectedImages.getEditPath(editedImage.path);
        }
        //TuSdkResult result = new TuSdkResult();
        //result.imageFile = new File(path);
        //MultiSelectorUtils.openEditAdvanced(result, this, getActivity());
    }
    /**
     * 回调接口
     */
    public interface Callback {
        void onSingleImageSelected(SelectedImage selectedImage);

        void onImageSelected(SelectedImage selectedImage);

        void onImageReSelected(SelectedImage selectedImage);

        void onImageUnselected(SelectedImage selectedImage);

        void onCameraShot(File imageFile);
    }

}
