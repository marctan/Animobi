package com.marcqtan.kissanimem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

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
                                String tag = currentFragment.getTag();
                                if (currentFragment instanceof MainAnimeFragment) {
                                    ((MainAnimeFragment) currentFragment).scrolltoTop();
                                }
                                else {
                                    if((!(currentFragment instanceof EpisodeListFragment) && !(currentFragment instanceof About)) || (tag != null && tag.equals("episodeList2"))) {
                                        fragbeforeHomeClick = getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
                                    }
                                    if(fragbeforeSearchClick != null) {
                                        switchFragment(fragbeforeSearchClick, fragbeforeSearchClick.getTag());
                                        fragbeforeSearchClick = null;
                                    } else {
                                        getSupportFragmentManager().popBackStackImmediate("episodeList", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        switchFragment(mainAnimeFragment, mainAnimeFragment.getTag());
                                    }
                                }
                                return true;
                            case R.id.menu_search:
                                if(getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder) instanceof EpisodeListFragment) {
                                    fragbeforeSearchClick = getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
                                }

                                if (fragbeforeHomeClick != null) {
                                    switchFragment(fragbeforeHomeClick, fragbeforeHomeClick.getTag());
                                    fragbeforeHomeClick = null;
                                } else if (!(getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder) instanceof About)) {
                                    getSupportFragmentManager().popBackStackImmediate("searchResult", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    switchFragment(searchAnimeFragment, searchAnimeFragment.getTag());
                                } else {
                                    switchFragment(searchAnimeFragment, searchAnimeFragment.getTag());
                                }
                                return true;
                            case R.id.menu_about:
                                Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
                                String tag1 = currFrag.getTag();
                                if((!(currFrag instanceof EpisodeListFragment) && !(currFrag instanceof MainAnimeFragment)) || (tag1 != null && tag1.equals("episodeList2"))) {
                                    fragbeforeHomeClick = currFrag;
                                } else {
                                    fragbeforeSearchClick = currFrag;
                                }
                                switchFragment(new About(), "about");
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

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String name = getSupportFragmentManager().getBackStackEntryAt
                    (getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

            if(name.equals("episodeList") && (bottomNavigationView.getSelectedItemId() == R.id.menu_search || bottomNavigationView.getSelectedItemId() == R.id.menu_about) ) {
                View view = bottomNavigationView.findViewById(R.id.menu_home);
                view.performClick();
            } else if ((name.equals("searchResult") || name.equals("episodeList2") || name.equals("movie")) && (bottomNavigationView.getSelectedItemId() == R.id.menu_home || bottomNavigationView.getSelectedItemId() == R.id.menu_about)) {
                View view = bottomNavigationView.findViewById(R.id.menu_search);
                view.performClick();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}