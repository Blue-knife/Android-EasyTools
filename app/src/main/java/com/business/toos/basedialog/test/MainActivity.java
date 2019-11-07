package com.business.toos.basedialog.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.business.toos.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportFragmentManager().beginTransaction().replace(R.id.main,new TestCardFragment()).commit();

        init();
    }

    private void init() {
        findViewById(R.id.main_dialog_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_dialog_tv:
                startActivity(new Intent(this, DialogActivity.class));
                break;
            default:
                break;
        }
    }
}
