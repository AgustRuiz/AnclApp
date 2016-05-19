package es.agustruiz.anclapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.service.AnclappService;

public class SplashScreen extends AppCompatActivity {

    public static final String LOG_TAG = SplashScreen.class.getName() + "[A]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(SplashScreen.this, "Google API key: " + getString(R.string.google_maps_key), Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(this, AnclappService.class);
        startService(serviceIntent);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
