package org.shaolin.uimaster.app.aty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.impl.AdPresenterImpl;

/**
 * Launch the app
 * 
 * @author 
 * @created 2014年12月22日 上午11:51:56
 * 
 */
public class AppStartActivity extends Activity implements AnimationListener {

    private LinearLayout linearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        linearLayout = (LinearLayout) findViewById(R.id.app_start_view);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        String adPath = PreferencesUtils.getString(this, ConfigData.AD_PATH);
        if (!TextUtils.isEmpty(adPath)){
            Bitmap bm = BitmapFactory.decodeFile(adPath, null);
            linearLayout.setBackgroundDrawable(new BitmapDrawable(bm));
            aa.setDuration(3000);
        }else{
            linearLayout.setBackgroundResource(R.drawable.welcome);
            aa.setDuration(800);
        }

        view.startAnimation(aa);
        aa.setAnimationListener(this);

        AdPresenterImpl adPresenter = new AdPresenterImpl(this);
    }

    @Override
    public void onAnimationEnd(Animation arg0) {
        redirectTo();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
