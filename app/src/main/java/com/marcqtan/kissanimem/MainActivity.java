package com.marcqtan.kissanimem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.cover_trending);

        bottomNavigationView.setItemBackgroundResource(R.color.bottombar);

        /*Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    Log.v("test", Integer.toHexString(vibrant.getRgb()));//ffe83008

                    bottomNavigationView.setItemBackgroundResource(R.color.bottombar);
                }
            }
        });*/


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
