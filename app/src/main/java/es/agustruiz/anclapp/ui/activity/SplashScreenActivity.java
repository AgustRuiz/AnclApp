package es.agustruiz.anclapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import es.agustruiz.anclapp.service.AnclappService;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String LOG_TAG = SplashScreenActivity.class.getName() + "[A]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(this, AnclappService.class);
        startService(serviceIntent);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}
