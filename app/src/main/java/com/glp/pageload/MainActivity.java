package com.glp.pageload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.glp.pageload.fragment.ArticleFragment;

public class MainActivity extends AppCompatActivity {

    private ArticleFragment mAndroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initFragments(savedInstanceState);
        showTargetFragment(CommonDefine.ANDROID);
    }

    /**
     * 初始化 Fragment
     */
    private void initFragments(Bundle savedInstanceState) {
        // 添加Fragment前检查是否有保存的Activity状态。
        // 如果没有状态保存，说明Activity是第1次被创建，我们添加Fragment。
        // 如果有状态保存，说明Activity刚刚出现过异常被销毁过，现在正在恢复，我们不再添加Fragment
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mAndroidFragment = ArticleFragment.newInstance(CommonDefine.ANDROID);
            ft.add(R.id.content_layout, mAndroidFragment, CommonDefine.ANDROID);
            ArticleFragment mWebFragment = ArticleFragment.newInstance(CommonDefine.WEB);
            ft.add(R.id.content_layout, mWebFragment, CommonDefine.WEB);
            ArticleFragment mIOSFragment = ArticleFragment.newInstance(CommonDefine.IOS);
            ft.add(R.id.content_layout, mIOSFragment, CommonDefine.IOS);
            ft.commitNow();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_android:
                    showTargetFragment(CommonDefine.ANDROID);
                    return true;
                case R.id.navigation_web:
                    showTargetFragment(CommonDefine.WEB);
                    return true;
                case R.id.navigation_ios:
                    showTargetFragment(CommonDefine.IOS);
                    return true;
            }
            return false;
        }
    };

    /**
     * 只显示目标 Fragment
     *
     * @param targetFragmentTag fragment对应的 tag
     */
    private void showTargetFragment(String targetFragmentTag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (targetFragmentTag.equals(fragment.getTag())) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

}
