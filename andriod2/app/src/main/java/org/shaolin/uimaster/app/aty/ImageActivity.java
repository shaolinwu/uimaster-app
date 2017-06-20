package org.shaolin.uimaster.app.aty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.shaolin.uimaster.app.R;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/1/18.
 */

public class ImageActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView imageview;
    public final static String BUNDLE_KEY_ARGS = "BUNDLE_KEY_ARGS";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageview=(ImageView)findViewById(R.id.imageView1);
        final URL url;
        try {
            url = new URL(getIntent().getStringExtra("url"));
            new Thread(){
                @Override
                public void run() {
                    try {
                        InputStream is= url.openStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        //  imageview.setImageBitmap(bitmap);
                        //发送消息，通知UI组件显示图片
                        handler.sendEmptyMessage(0x9527);
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0x9527) {
                //显示从网上下载的图片
                imageview.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

}
