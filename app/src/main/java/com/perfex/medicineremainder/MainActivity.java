package com.perfex.medicineremainder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.perfex.medicineremainder.databinding.ActivityMainBinding;
import com.perfex.medicineremainder.ui.add.AddFragment;
import com.perfex.medicineremainder.ui.event.EventFragment;
import com.perfex.medicineremainder.ui.home.HomeFragment;
import com.perfex.medicineremainder.ui.note.NoteFragment;

import com.perfex.medicineremainder.ui.notification.NotificationsActivity;
import com.perfex.medicineremainder.ui.settings.SettingsFragment;

import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;


public class MainActivity extends AppCompatActivity {

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new EventFragment();
    final Fragment fragment3 = new AddFragment();
    final Fragment fragment4 = new NoteFragment();
    final Fragment fragment5 = new SettingsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    CbnMenuItem cbnMenuItem[] =new CbnMenuItem[5];
    private boolean isFirstTimeCreatedActivity = true;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ActivityMainBinding binding;
    private Fragment active;
    AlarmReceiver alarmReceiver;
    private InterstitialAd mInterstitialAd;

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,binding.drawerLayout,R.string.nav_open,R.string.nav_close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        active = fragment1;
        binding.menu.setOnClickListener(v -> {
            if(!binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        fm.beginTransaction().add(R.id.container, fragment5, "5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.container, fragment1, "1").commit();


        cbnMenuItem[0]=new CbnMenuItem(R.drawable.ic_baseline_home_24,R.drawable.avd_home,R.id.home);
        cbnMenuItem[1]=new CbnMenuItem(R.drawable.ic_baseline_event_24,R.drawable.avd_event,R.id.event);
        cbnMenuItem[2]=new CbnMenuItem(R.drawable.ic_baseline_add_24,R.drawable.avd_add,R.id.add);
        cbnMenuItem[3]=new CbnMenuItem(R.drawable.ic_baseline_event_note_24,R.drawable.avd_note,R.id.note);
        cbnMenuItem[4]=new CbnMenuItem(R.drawable.ic_baseline_settings_24,R.drawable.avd_settings,R.id.settings);

        binding.bottomNavigation1.setMenuItems(cbnMenuItem,0);
        binding.bottomNavigation1.setOnMenuItemClickListener((cbnMenuItem, integer) -> {

            switch (cbnMenuItem.getDestinationId()) {
                case R.id.home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    break;
                case R.id.event:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    break;

                case R.id.add:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    break;
                case R.id.note:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    break;

                case R.id.settings:
                    fm.beginTransaction().hide(active).show(fragment5).commit();
                    active = fragment5;
                    break;
            }
            return null;
        });

        MobileAds.initialize(this, initializationStatus -> {});


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        if(item.getItemId()==R.id.notify){
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() { }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu_bar, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}