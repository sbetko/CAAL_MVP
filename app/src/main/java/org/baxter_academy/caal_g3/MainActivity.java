package org.baxter_academy.caal_g3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    Meta meta = new Meta();

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        System.out.println("Started MainActivity");
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.startbutton);
        Intent readerIntent = new Intent(getApplicationContext(), Reader.class);
        final PendingIntent pintent = PendingIntent.getService(this.getBaseContext(), 0, readerIntent, 0);
        final AlarmManager manager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(buttonView.getContext(), Meta.class));
                    Toast.makeText(buttonView.getContext(), "Started meta", Toast.LENGTH_SHORT).show();
                } else {
                    stopService(new Intent(buttonView.getContext(), Meta.class));
                    Toast.makeText(buttonView.getContext(), "Stopped meta", Toast.LENGTH_SHORT).show();
                    stopService(new Intent(buttonView.getContext(), Reader.class));

                    manager.cancel(pintent);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.licenses:
                Intent myIntent = new Intent(MainActivity.this, LicensesActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}