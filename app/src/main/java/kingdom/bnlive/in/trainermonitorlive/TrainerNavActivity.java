package kingdom.bnlive.in.trainermonitorlive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TrainerNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView headerName;
    private TextView headerEmail;
    private TextView headerRole;
    SharedPreferences sharedPreferences;
    String MyPreferences="monitordb";
    private String accessType;
    View headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView=findViewById(R.id.nav_view);

        headerView=navigationView.getHeaderView(0);
        headerName=headerView.findViewById(R.id.headername);
        headerEmail=headerView.findViewById(R.id.headeremail);
        headerRole=headerView.findViewById(R.id.headerrole);

        sharedPreferences=getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        headerName.setText(""+sharedPreferences.getString("name",null));
        headerEmail.setText(""+sharedPreferences.getString("email",null));
        headerRole.setText(""+sharedPreferences.getString("role",null));
        accessType=""+sharedPreferences.getString("access",null);
        navigationView.setNavigationItemSelectedListener(this);
        //select first item from nav items
        navigationView.getMenu().getItem(0).setChecked(true);
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.changeLayout,new FragmentDashboard()).commit();
    }
long currenttime;
    @Override
    public void onBackPressed() {
        if(currenttime>System.currentTimeMillis())
        {
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

        }
        currenttime=System.currentTimeMillis()+2000;
        Toast.makeText(getBaseContext(),"Press again to exit",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            FragmentManager manager=getSupportFragmentManager();
            FragmentDashboard fd=new FragmentDashboard();
            manager.beginTransaction().replace(R.id.changeLayout,fd).commit();
            // Handle the camera action
        } else if (id == R.id.nav_activity) {
            Toast.makeText(getBaseContext(),"Feture not functional yet.",Toast.LENGTH_LONG).show();

        }  else if (id == R.id.log_out) {
SharedPreferences.Editor editor=sharedPreferences.edit();
editor.putString("role",null);
editor.commit();
            this.finish();
            Intent intent=new Intent(TrainerNavActivity.this,RegistrationLogin.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
