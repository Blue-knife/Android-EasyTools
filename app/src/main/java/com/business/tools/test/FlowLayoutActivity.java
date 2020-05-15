package com.business.tools.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.business.tools.flowlayout.FlowLayout;
import com.business.toos.R;

public class FlowLayoutActivity extends AppCompatActivity {

    private FlowLayout mFlowLayout;
    private String[] texts = {"王大强", "adsasda", "ADSadsa", "Adsadsa"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
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
