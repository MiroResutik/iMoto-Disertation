package com.example.imoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.imoto.Fragments.FriendsFragment;
import com.example.imoto.Fragments.GalleryFragment;
import com.example.imoto.Fragments.MyProfileFragment;
import com.example.imoto.Fragments.SettingsFragment;
import com.example.imoto.Fragments.ShareFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

        //Firebase variables
        FirebaseAuth mAuth;
        FirebaseUser currentUser;

    private CardView favourite_places, navigate, parking, weather, fuel, my_folder,first_aid, calendar, messages, round_trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining Cards
        favourite_places = findViewById(R.id.favourite_places);
        first_aid = findViewById(R.id.first_aid);
        weather = findViewById(R.id.weather);

        //Add Click listener to the cards
        favourite_places.setOnClickListener(this);
        first_aid.setOnClickListener(this);
        weather.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Run updateNavHeader function to update nav header with pictrue, email and name from firebase
        updateNavHeader();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Gallery");
            getSupportFragmentManager().beginTransaction().replace(R.id.gallery_container, new GalleryFragment()).commit();

        } else if (id == R.id.nav_my_profile) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("My Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.my_profile_container, new MyProfileFragment()).commit();

        } else if (id == R.id.nav_friends) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("My Friends");
            getSupportFragmentManager().beginTransaction().replace(R.id.friends_container, new FriendsFragment()).commit();

        } else if (id == R.id.nav_share) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Share");
            getSupportFragmentManager().beginTransaction().replace(R.id.share_container, new ShareFragment()).commit();

        } else if (id == R.id.nav_settings) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();


        } else if (id == R.id.nav_logout){
            //Log out of the application - Terminate the application
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Method to
    @Override
    public void onClick(View v) {

        Intent i;
        switch (v.getId()) {
            case R.id.favourite_places:
                i = new Intent(this, FavouritePlaces.class);
                startActivity(i);
                break;
            case R.id.first_aid:
                i = new Intent(getPackageManager().getLaunchIntentForPackage("com.cube.rca"));
                startActivity(i);
                break;
            case R.id.weather:
                i = new Intent(this, WeatherActivity.class);
                startActivity(i);
                break;
            //case R.id.navigate : i = new Intent(this,MapsActivity.class); startActivity(i); break;
            default:
                break;
        }
    }
    //Function to update navigation header
    public void updateNavHeader(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_username);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);




    }
}
