package com.business.tools.test;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.business.toos.R;
import com.bullet.core.base.BaseSkinActivity;
import com.bullet.ui.customView.FlowLayout;

public class FlowLayoutActivity extends BaseSkinActivity {

    private FlowLayout mFlowLayout;
    private String[] texts = {"王大强", "adsasda", "ADSadsa", "Adsadsa"};



    @Override
    public int layout() {
        return R.layout.activity_flow;
    }

    @Override
    public void bindView() {
        mFlowLayout = findViewById(R.id.flow_Layout);
        findViewById(R.id.btn_add_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });
    }

    private void addTag() {
        TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.view_flow_tv, mFlowLayout,false);
        textView.setText(texts[(int) (Math.random() * 3)]);
        mFlowLayout.addView(textView);
    }

}
