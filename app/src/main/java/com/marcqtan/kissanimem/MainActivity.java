package com.marcqtan.kissanimem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MainAnimeFragment mainAnimeFragment = new MainAnimeFragment();
    SearchAnimeFragment searchAnimeFragment = new SearchAnimeFragment();
    Fragment fragbeforeSearchClick = null;
    Fragment fragbeforeHomeClick = null;

    static List<Anime> animeList = null;

    public static void cacheAnimeList(List<Anime> list) {
        animeList = list;
    }

    public static List<Anime> getCacheAnimeList(){
        return animeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                //R.drawable.cover_trending);

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
                                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
                                if (currentFragment instanceof MainAnimeFragment) {
                                    ((MainAnimeFragment) currentFragment).scrolltoTop();
                                }
                                else {
                                    if(!(currentFragment instanceof EpisodeListFragment)) {
                                        fragbeforeHomeClick = getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
                                    }
                                    if(fragbeforeSearchClick != null) {
                                        switchFragment(fragbeforeSearchClick, "episodeList");
                                    }
                                }
                                return true;
                            case R.id.menu_search:
                                if(getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder) instanceof MainAnimeFragment ||
                                    getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder) instanceof EpisodeListFragment) {
                                    fragbeforeSearchClick = getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
                                }

                                if (fragbeforeHomeClick != null) {
                                    switchFragment(fragbeforeHomeClick, "test2");
                                    fragbeforeHomeClick = null;
                                } else {
                                    /*if(!(getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder) instanceof SearchAnimeFragment)) {
                                        switchFragment(new SearchAnimeFragment(), "searchFrag");
                                    }*/
                                    switchFragment(searchAnimeFragment, "test2");
                                    //getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, searchAnimeFragment, "test2").addToBackStack(null).commit();
                                }
                                return true;
                            case R.id.menu_notifications:
                                //switchFragment(2, TAG_FRAGMENT_TRIPS);
                                return true;
                        }
                        return false;
                    }
                });


        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        //layoutParams.setBehavior(new bottombarbehavior());

       switchFragment(mainAnimeFragment, "MainFrag");
    }

    private void switchFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, fragment, tag).commit();
    }
}