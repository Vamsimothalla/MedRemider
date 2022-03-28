package com.perfex.medicineremainder.ui.note;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
//
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.database.user.AppDatabase;
import com.perfex.medicineremainder.databinding.ActivityNoteDetailsBinding;
import com.perfex.medicineremainder.model.Note;
import com.perfex.medicineremainder.utils.Constants;

import java.util.ArrayList;

public class NoteDetailsActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    private boolean isEditMode = false;
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://test-3281b-default-rtdb.asia-southeast1.firebasedatabase.app");
    private ActivityNoteDetailsBinding binding;
    DatabaseReference ref = database.getReference();
    private AppDatabase appDatabase;
    private FirebaseAuth mAuth;
    ArrayList<EditText> oldEditText,newEditText;;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        mAuth=FirebaseAuth.getInstance();
        appDatabase=AppDatabase.getDatabase(this);
        note= (Note) getIntent().getSerializableExtra("refill");
        //binding.setRefill(refill);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'> Refill Details </font>"));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        oldEditText =new ArrayList<>();
        newEditText = new ArrayList<>();
        findAllEditTexts();
    }
    private void findAllEditTexts() {
        oldEditText.add(binding.titleEt);
        oldEditText.add(binding.addressEt);
        oldEditText.add(binding.prescriptionEt);
        for (EditText e : oldEditText) {
            EditText newEt = new EditText(this);
            newEditText.add(newEt);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, Constants.AD_INT, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        selectMenu(menu);
        return true;
    }
    private void selectMenu(Menu menu){
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if(isEditMode){
            inflater.inflate(R.menu.save_menu, menu);
        }
        else {
            inflater.inflate(R.menu.edit_menu, menu);
        }
    }
}