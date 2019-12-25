package com.business.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.business.tools.basedialog.test.DialogActivity;
import com.business.tools.camera.CameraActivity;
import com.business.tools.flowlayout.test.FlowLayoutActivity;
import com.business.tools.image_card.test.CardaActivity;
import com.business.tools.views.ViewsActivity;
import com.business.toos.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }

    private void findView() {
        findViewById(R.id.btn_base_dialog).setOnClickListener(this);
        findViewById(R.id.btn_flow).setOnClickListener(this);
        findViewById(R.id.btn_img_card).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_custom_views).setOnClickListener(this);
    }


    public void startIntent(Class name) {
        Intent intent = new Intent(this, name);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_base_dialog:
                startIntent(DialogActivity.class);
                break;
            case R.id.btn_flow:
                startIntent(FlowLayoutActivity.class);
                break;
            case R.id.btn_img_card:
                startIntent(CardaActivity.class);
                break;
            case R.id.btn_bar:
                ToastUtils.showText("todo");
                break;
            case R.id.btn_camera:
                startIntent(CameraActivity.class);
                break;
            case R.id.btn_custom_views:
                startIntent(ViewsActivity.class);
                break;
            default:
                break;
        }
    }
}
