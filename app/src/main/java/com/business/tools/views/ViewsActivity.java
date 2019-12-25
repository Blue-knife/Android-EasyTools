package com.business.tools.views;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.business.toos.R;

/**
 * @author 345
 * @date 2019/12/25
 */
public class ViewsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);
        init();

    }

    private void init() {
        CustomTextView textView = findViewById(R.id.tv);
        textView.setText("Android-EasyTools" +
                "一个通用的业务代码解决方案");

        textView.setTvs(new String[]{"Android-EasyTools",
                        "通用", "解决方案"},
                new Integer[]{Color.BLUE,
                        Color.GREEN,
                        Color.RED});
        textView.notifyTv();
    }
}
