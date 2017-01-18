package org.baxter_academy.caal_g3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
/**
 *
 * Old button code
        final Button button = (Button) findViewById(R.id.startbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mServiceIntent = new Intent(v.getContext(), Reader.class);
                startService(mServiceIntent);
            }
        });
**/
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.startbutton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent mServiceIntent = new Intent(buttonView.getContext(), Reader.class);
                    startService(mServiceIntent);
                } else {
                    Intent mServiceIntent = new Intent(buttonView.getContext(), Reader.class);
                    stopService(mServiceIntent);
                }
            }
        });
    }
}