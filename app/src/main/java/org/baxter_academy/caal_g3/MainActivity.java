package org.baxter_academy.caal_g3;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.startbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mServiceIntent = new Intent(v.getContext(), Background.class);
                startService(mServiceIntent);
            }
        });
    }
}