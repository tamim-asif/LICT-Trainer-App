package kingdom.bnlive.in.trainermonitorlive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
}
