package com.satan.imageselectorandcrop;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


public class ClipPictureActivity extends Activity implements OnTouchListener,
        OnClickListener {
    ImageView srcPic;
    Button sure;
    ClipView clipview;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private static final String TAG = "11";
    int mode = NONE;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    private String path;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;

        path = getIntent().getStringExtra("path");

        srcPic = (ImageView) this.findViewById(R.id.src_pic);

        srcPic.setImageBitmap(BitmapFactory.decodeFile(path));
        srcPic.setOnTouchListener(this);
        srcPic.setScaleType(ImageView.ScaleType.MATRIX);

        sure = (Button) this.findViewById(R.id.sure);
        sure.setOnClickListener(this);

    }

    /*这里实现了多点触摸放大缩小，和单点移动图片的功能，参考了论坛的代码*/
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 設置初始點位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void onClick(View v) {
        Bitmap fianBitmap = getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fianBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();

        Intent intent = new Intent();
        intent.putExtra("bitmap", bitmapByte);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /*获取矩形区域内的截图*/
    private Bitmap getBitmap() {
        getBarHeight();
        Bitmap screenShoot = takeScreenShot();

        clipview = (ClipView) this.findViewById(R.id.clipview);
        int width = clipview.getWidth();
        int height = clipview.getHeight();
        Bitmap finalBitmap = Bitmap.createBitmap(screenShoot,
                (width - height / 3) / 2, height / 3 + titleBarHeight + statusBarHeight, height / 3, height / 3);
        return finalBitmap;
    }

    int statusBarHeight = 0;
    int titleBarHeight = 0;

    private void getBarHeight() {
        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;

        int contenttop = this.getWindow()
                .findViewById(Window.ID_ANDROID_CONTENT).getTop();
        // statusBarHeight是上面所求的状态栏的高度
        titleBarHeight = contenttop - statusBarHeight;

        Log.v(TAG, "statusBarHeight = " + statusBarHeight
                + ", titleBarHeight = " + titleBarHeight);
    }

    // 获取Activity的截屏
    private Bitmap takeScreenShot() {
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

}