package com.spud.sgsclasscountdownapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Stephen Ogden on 4/23/18.
 * FTC 6128 | 7935
 * FRC 1595
 */
public class Settings extends AppCompatActivity {

    RadioGroup override;

    RadioButton automatic, builtin, manual, override8, overrideA, overrideE;

    private Database database = new Database();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        override = findViewById(R.id.overrideGroup);

        automatic = findViewById(R.id.AutomaticUpdate);
        builtin = findViewById(R.id.Builtin);
        manual = findViewById(R.id.Manual);
        override8 = findViewById(R.id.override8);
        overrideA = findViewById(R.id.overrideA);
        overrideE = findViewById(R.id.overrideE);

        automatic.setChecked(database.getUpdateType().equals(Database.updateType.Automatic));
        builtin.setChecked(database.getUpdateType().equals(Database.updateType.BuiltIn));
        manual.setChecked(database.getUpdateType().equals(Database.updateType.ManualADay) ||
                database.getUpdateType().equals(Database.updateType.ManualEDay) ||
                database.getUpdateType().equals(Database.updateType.ManualFullDay) ||
                database.getUpdateType().equals(Database.updateType.ManualCustomDay));

        if (manual.isChecked()) {
            override8.setChecked(database.getUpdateType().equals(Database.updateType.ManualFullDay));
            overrideA.setChecked(database.getUpdateType().equals(Database.updateType.ManualADay));
            overrideE.setChecked(database.getUpdateType().equals(Database.updateType.ManualEDay));
        } else {
            for (int i = 0; i < override.getChildCount(); i++) {
                ((RadioButton) override.getChildAt(i)).setChecked(false);
            }
        }

        // If the database does not exist, disable set the override buttons
        manual.setEnabled(database.databaseExists());
        override.setEnabled(database.databaseExists());

        // Setup back button
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Timer.class));
                finish();
            }
        });

        manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (int i = 0; i < override.getChildCount(); i++) {
                    override.getChildAt(i).setEnabled(b);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if there is a network connection available for the automatic portion
        automatic.setEnabled(isNetworkAvailable());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Write options to database

    }

    // https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}