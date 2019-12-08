package com.business.toos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.business.toos.R;
import com.business.toos.basedialog.test.DialogActivity;
import com.business.toos.flowlayout.test.FlowLayoutActivity;
import com.business.toos.image_card.test.CardaActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_base_dialog).setOnClickListener(this);
        findViewById(R.id.btn_flow).setOnClickListener(this);
        findViewById(R.id.btn_img_card).setOnClickListener(this);
    }


    public void startIntent(Class name) {
        Intent intent = new Intent(this, name);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_base_dialog:startIntent(DialogActivity.class);break;
            case R.id.btn_flow:startIntent(FlowLayoutActivity.class);break;
            case R.id.btn_img_card:startIntent(CardaActivity.class);break;
            default:
                break;
        }
    }
}
