package org.baxter_academy.caal_g3;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
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

/*        final Button button = (Button) findViewById(R.id.startbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked");
                Intent mServiceIntent = new Intent(v.getContext(), Meta.class);
                startService(mServiceIntent);
            }
        });*/


    }
}