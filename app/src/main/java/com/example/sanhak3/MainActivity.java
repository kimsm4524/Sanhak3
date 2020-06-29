package com.example.sanhak3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentBalance fragmentBalance = new FragmentBalance();
    private FragmentCard fragmentCard = new FragmentCard();
    private FragmentCommunity fragmentCommunity = new FragmentCommunity();
    private FragmentInout fragmentInout = new FragmentInout();
    private FragmentPattern fragmentPattern = new FragmentPattern();
    OnBackPressedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<InoutData> inoutdatas = new ArrayList<InoutData>();
        inoutdatas.add(new InoutData("temp","음식","70000"));
        inoutdatas.add(new InoutData("temp","교통","30000"));
        inoutdatas.add(new InoutData("temp","학원","20000"));
        inoutdatas.add(new InoutData("temp","편의점","10000"));
        inoutdatas.add(new InoutData("temp","카페","15000"));
        inoutdatas.add(new InoutData("temp","기타","5000"));
        SaveInoutData(inoutdatas);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentInout).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.inout:
                    transaction.replace(R.id.frameLayout, fragmentInout).commitAllowingStateLoss();
                    break;
                case R.id.balance:
                    transaction.replace(R.id.frameLayout, fragmentBalance).commitAllowingStateLoss();
                    break;
                case R.id.pattern:
                    transaction.replace(R.id.frameLayout, fragmentPattern).commitAllowingStateLoss();
                    break;
                case R.id.card:
                    transaction.replace(R.id.frameLayout, fragmentCard).commitAllowingStateLoss();
                    break;
                case R.id.community:
                    transaction.replace(R.id.frameLayout, fragmentCommunity).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(listener != null){
            listener.onBackPressed();
        }else{
            super.onBackPressed();
        }
    }
    private void SaveInoutData(ArrayList<InoutData> nameservs) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nameservs);
        editor.putString("MyInout", json);
        editor.commit();
    }
}