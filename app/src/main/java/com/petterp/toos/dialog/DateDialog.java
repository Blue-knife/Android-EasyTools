package com.petterp.toos.dialog;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.petterp.toos.R;


/**
 * @author 345
 * Created by Administrator on 2019/10/7.
 */

public class DateDialog extends BaseFragDialog implements View.OnClickListener {

    private TextView tv1;
    private TextView tv2;

    public DateDialog(Object view, float alpha, boolean autoDismiss, boolean cancelable, int animation, int gravity) {
        super(view, alpha, autoDismiss, cancelable, animation, gravity);
    }

    @Override
    public void initView(View view) {
        tv1 = view.findViewById(R.id.tv_dialog_cancel);
        tv2 = view.findViewById(R.id.tv_dialog_confirm);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);

        TextView title = view.findViewById(R.id.tv_dialog_title);
        TextView message = view.findViewById(R.id.tv_dialog_message);
        title.setText("我是日期对话框");
        message.setText("我是时间戳："+ System.currentTimeMillis());
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_dialog_cancel) {
            Toast.makeText(getContext(), "取消", Toast.LENGTH_SHORT).show();
            dismiss();
        } else if (i == R.id.tv_dialog_confirm) {
            Toast.makeText(getContext(), "成功", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    /**
     * 继承的类必须写此方法，泛型为当前类
     */
    public static DialogBuilder<DateDialog> DateBuilder() {
        return new DialogBuilder<>(DateDialog.class);
    }

}
