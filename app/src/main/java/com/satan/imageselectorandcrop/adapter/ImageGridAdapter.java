package com.satan.imageselectorandcrop.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.satan.imageselectorandcrop.R;
import com.satan.imageselectorandcrop.bean.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by satan on 2015/6/19.
 */
public class ImageGridAdapter extends BaseAdapter {


    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private List<Image> mImages = new ArrayList<Image>();
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageGridAdapter(Context context) {
        mContext = context;
        initCache();
    }

    private void initCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void setData(List<Image> images) {
        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mImages.size() + 1;
    }

    @Override
    public Image getItem(int position) {
        return position == 0 ? null : mImages.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == TYPE_CAMERA) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_camera, parent, false);
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_image, parent, false);
            ImageView iv = ViewHolder.get(convertView, R.id.image);
            Picasso.with(mContext).load(new File(getItem(position).getPath())).resize(120, 120).placeholder(R.drawable.default_error).centerCrop().into(iv);

//            Bitmap bitmap = getBitmapFromMemCache(String.valueOf(position));
//            if (bitmap != null) {
//                iv.setImageBitmap(bitmap);
//            } else {
//                Bitmap newBitmap = decodeSampledBitmapFromResource(getItem(position).getPath(), 120, 120);
//                addBitmapToMemoryCache(String.valueOf(position), newBitmap);
//                iv.setImageBitmap(newBitmap);
//            }
        }
        return convertView;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
