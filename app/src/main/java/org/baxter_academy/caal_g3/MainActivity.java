package org.baxter_academy.caal_g3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        System.out.println("Started MainActivity");
        Intent mServiceIntent = new Intent(this.getApplicationContext(), Meta.class); // DELETE
        //startService(mServiceIntent); // DELETE
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.startbutton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent mServiceIntent = new Intent(buttonView.getContext(), Meta.class);
                    startService(mServiceIntent);
                    Toast.makeText(buttonView.getContext(), "Started meta", Toast.LENGTH_SHORT);
                } else {
                    Intent mServiceIntent = new Intent(buttonView.getContext(), Meta.class);
                    stopService(mServiceIntent);
                    Toast.makeText(buttonView.getContext(), "Stopped meta", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}