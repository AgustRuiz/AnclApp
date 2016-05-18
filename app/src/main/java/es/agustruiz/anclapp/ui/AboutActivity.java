package es.agustruiz.anclapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;

public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.tvDeveloperEmail)
    TextView developerEmail;

    @Bind(R.id.tvVersionLabel)
    TextView versionLabel;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mContext = getApplicationContext();
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        developerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.about_developer_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, String.format("[%s] ", getString(R.string.app_name)));
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        try {
            versionLabel.setText(
                    getString(
                            R.string.about_version,
                            getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException ignored) {}
    }
}
