package com.satan.imageselectorandcrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.satan.imageselectorandcrop.adapter.ImageGridAdapter;
import com.satan.imageselectorandcrop.bean.Image;
import com.satan.imageselectorandcrop.constant.Constant;
import com.satan.imageselectorandcrop.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择图片Activity
 * Created by satan on 2015/6/19.
 */
public class SelectActivity extends FragmentActivity {

    private GridView mGridView;
    private ImageGridAdapter mImageGridAdapter;
    private Context mContext;
    private File mTmpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        mContext = this;
        setData();
        initView();
        initListener();
    }

    private void setData() {
        getSupportLoaderManager().initLoader(Constant.LOADER_ALL, null, mLoaderCallback);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.grid);
        mImageGridAdapter = new ImageGridAdapter(mContext);
        mGridView.setAdapter(mImageGridAdapter);
    }


    private void initListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showCameraAction();
                } else {
                    startClipPicture(mImageGridAdapter.getItem(position).getPath());
                }
            }
        });

    }


    /**
     * 跳转至系统截图界面进行截图
     *
     * @param data
     * @param size
     */
    private void startPhotoZoom(Uri data, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, Constant.REQUEST_CROP);
    }


    private void startClipPicture(String path) {
        Intent intent = new Intent(mContext, ClipPictureActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, Constant.FROM_CLIP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    startClipPicture(mTmpFile.getPath());
                }
            } else {
                if (mTmpFile != null && mTmpFile.exists()) {
                    mTmpFile.delete();
                }
            }
        } else if (requestCode == Constant.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    setResult(Activity.RESULT_OK, data);
                    this.finish();
                }
            }

        } else if (requestCode == Constant.FROM_CLIP) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Constant.FROM_CLIP, data);
                finish();
            }
        }
    }


    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtils.createTmpFile(this);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, Constant.REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "没找到系统相机", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 加载手机中的相片
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(SelectActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
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
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);
                    } while (data.moveToNext());
                    mImageGridAdapter.setData(images);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
}
