package com.business.tools.basedialog.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.business.tools.basedialog.utils.CustomSpinDialog;
import com.business.tools.basedialog.utils.OnListener;
import com.business.toos.R;
import com.business.tools.basedialog.utils.BaseFragDialog;
import com.business.tools.basedialog.utils.DateDialog;
import com.business.tools.basedialog.utils.ToastDialog;


/**
 * @author 345
 */

public class DialogActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_test);

        init();
    }

    private void init() {
        findViewById(R.id.dialog_test_one).setOnClickListener(this);
        findViewById(R.id.dialog_test_two).setOnClickListener(this);
        findViewById(R.id.dialog_test_three).setOnClickListener(this);
        findViewById(R.id.dialog_test_spin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_test_one:
                BaseFragDialog.Builder()
                        .setAlpha(1)
                        .setAnimation(R.style.BottomAnimStyle)
                        .setGravity(Gravity.CENTER)
                        .setContentView(R.layout.dialog_hint)
                        .build()
                        .setWidth(this, 0.6f)
                        .setText(R.id.tv_dialog_title, "我是名字")
                        .setText(R.id.tv_dialog_message, "我是内容")
                        .setListener(R.id.tv_dialog_confirm, new OnListener() {
                            @Override
                            public void onClick(BaseFragDialog dialog, View view) {
                                Toast.makeText(DialogActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .show(getSupportFragmentManager(), "345");
                break;
            case R.id.dialog_test_two:
                DateDialog.DateBuilder()
                        .setContentView(R.layout.dialog_hint)
                        .setCancelable(false)
                        .build()
                        .setListener(R.id.tv_dialog_cancel, new OnListener() {
                            @Override
                            public void onClick(BaseFragDialog dialog, View view) {
                                Toast.makeText(DialogActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setListener(R.id.tv_dialog_confirm, new OnListener() {
                            @Override
                            public void onClick(BaseFragDialog dialog, View view) {
                                Toast.makeText(DialogActivity.this, "确定", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .show(getSupportFragmentManager(), "345");
                break;
            case R.id.dialog_test_three:
                ToastDialog.ToastBuilder()
                        .setContentView(R.layout.dialog_toast)
                        .setCancelable(false)
                        .build()
                        .postAtTime(3000)
                        .setMessage("加载中")
                        //多种 type ，加载，加载完成，警告，失败等
                        .setType(ToastDialog.Type.LOADING)
                        .show(getSupportFragmentManager(), "345");
                break;
            case R.id.dialog_test_spin:
                CustomSpinDialog.CustomSpinBuilder()
                        .setAlpha(1)
                        .setContentView(R.layout.dialog_custom_spin)
                        .build()
                        .setHeight(200)
                        .setWidth(200)
                        .setResouce(R.drawable.ic_dialog_spin)
                        .show(getSupportFragmentManager(), "spin");
                break;
            default:
                break;
        }
    }

}
