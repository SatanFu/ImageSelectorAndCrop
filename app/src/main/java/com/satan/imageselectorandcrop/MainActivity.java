package com.satan.imageselectorandcrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.satan.imageselectorandcrop.constant.Constant;


public class MainActivity extends Activity {

    private Button mSelect;
    private ImageView mImage;
    private Context mContext;
    private static final int SELECT_IMAGE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initListener();
    }


    private void initView() {
        mSelect = (Button) findViewById(R.id.btn_select);
        mImage = (ImageView) findViewById(R.id.iv_image);

    }

    private void initListener() {
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectActivity.class);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    mImage.setImageBitmap(bitmap);
                }
            } else if (resultCode == Constant.FROM_CLIP) {
                byte[] bis = data.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                mImage.setImageBitmap(bitmap);
            }
        }
    }
}
