package com.wuch1k1n.tanshu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ConfigActivity extends AppCompatActivity {

    /**
     * 图书种类数目
     */
    private static final int CATALOG = 12;
    private int[] bt_ids = {R.id.bt_1, R.id.bt_2, R.id.bt_3, R.id.bt_4, R.id.bt_5, R.id.bt_6, R.id.bt_7, R.id.bt_8,
            R.id.bt_9, R.id.bt_10, R.id.bt_11, R.id.bt_12, R.id.bt_13, R.id.bt_14, R.id.bt_15, R.id.bt_16};
    private Button[] buttons;
    private Button bt_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        buttons = new Button[CATALOG];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = findViewById(bt_ids[i]);
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    buttons[index].setBackground(getResources().getDrawable(R.drawable.bg_round_button_red));
                }
            });
        }

        bt_commit = findViewById(R.id.bt_commit);
    }

}
