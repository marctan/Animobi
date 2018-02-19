package com.marcqtan.kissanimem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MainAnimeFragment mainAnimeFragment = new MainAnimeFragment();
    SearchAnimeFragment searchAnimeFragment = new SearchAnimeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_home:
                                switchFragment(mainAnimeFragment);
                                return true;
                            case R.id.menu_search:
                                switchFragment(searchAnimeFragment);
                                return true;
                            case R.id.menu_notifications:
                                //switchFragment(2, TAG_FRAGMENT_TRIPS);
                                return true;
                        }
                        return false;
                    }
                });


        switchFragment(mainAnimeFragment);
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, fragment).commit();
    }
}
