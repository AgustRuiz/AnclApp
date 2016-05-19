package es.agustruiz.anclapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import es.agustruiz.anclapp.service.AnclappService;

public class SplashScreen extends AppCompatActivity {

    public static final String LOG_TAG = SplashScreen.class.getName() + "[A]";

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
