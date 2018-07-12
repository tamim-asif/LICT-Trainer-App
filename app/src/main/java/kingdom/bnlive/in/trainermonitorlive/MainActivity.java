package kingdom.bnlive.in.trainermonitorlive;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView dev1, dev2, copyright;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView2);
        dev1 = findViewById(R.id.dev1);
        dev2 = findViewById(R.id.dev2);
        copyright = findViewById(R.id.copyright);

        dev1.setVisibility(View.GONE);
        dev2.setVisibility(View.GONE);
        copyright.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        animation.setDuration(2500);
        imageView.startAnimation(animation);

        dev1.setVisibility(View.VISIBLE);
        dev2.setVisibility(View.VISIBLE);
        copyright.setVisibility(View.VISIBLE);
        requestCallPermission();
//        imageView.animate().scaleX(20).scaleY(20).rotationX(360).rotationY(360).setDuration(2500);
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep( 3000 );
                    Intent intent=new Intent(MainActivity.this,RegistrationLogin.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        myThread.start();

    }


    private void requestCallPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        111);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
    }
}
