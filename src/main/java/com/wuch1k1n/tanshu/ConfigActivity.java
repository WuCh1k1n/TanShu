package com.wuch1k1n.tanshu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity {

    /**
     * 图书种类数目
     */
    private static final int CATALOG = 16;
    private int[] bt_ids = {R.id.bt_1, R.id.bt_2, R.id.bt_3, R.id.bt_4, R.id.bt_5, R.id.bt_6, R.id.bt_7,
            R.id.bt_8, R.id.bt_9, R.id.bt_10, R.id.bt_11, R.id.bt_12, R.id.bt_13, R.id.bt_14, R.id.bt_15, R.id.bt_16};
    private int[] catalog_ids = {242, 243, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 244};
    private Boolean[] flags;
    private Button[] buttons;
    private Button bt_commit;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private StringBuilder prefer = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        pref = getSharedPreferences("config", MODE_PRIVATE);
        editor = pref.edit();
        flags = new Boolean[16];

        for (int i = 0; i < flags.length; i++) {
            flags[i] = false;
        }

        buttons = new Button[CATALOG];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = findViewById(bt_ids[i]);
            buttons[i].setTextColor(getResources().getColor(R.color.white));
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    if (!flags[index]) {
                        buttons[index].setSelected(true);
                        buttons[index].setBackground(getResources().getDrawable(R.drawable.bg_round_button_red));
                        flags[index] = true;
                    } else {
                        buttons[index].setSelected(false);
                        buttons[index].setBackground(getResources().getDrawable(R.drawable.bg_round_button_blue));
                        flags[index] = false;
                    }
                }
            });
        }

        bt_commit = findViewById(R.id.bt_commit);
        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < buttons.length; i++) {
                    if (buttons[i].isSelected()) {
                        prefer.append(catalog_ids[i] + "#");
                    }
                }
                Log.d("Test", prefer.toString());
                editor.putString("prefer", prefer.toString());
                editor.putBoolean("is_set", true);
                editor.commit();
                Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
