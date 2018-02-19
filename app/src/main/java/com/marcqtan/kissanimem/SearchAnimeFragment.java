package com.marcqtan.kissanimem;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by dell on 18/02/2018.
 */

public class SearchAnimeFragment extends Fragment {
    EditText search;
    Button btnSearch;

    public SearchAnimeFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_anime, container, false);
        search = rootView.findViewById(R.id.searchET);

        btnSearch = rootView.findViewById(R.id.search);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                String searchUrl = "https://otakustream.tv/?s=" + search.getText();
                search.getText().clear();
                SearchFragment searchFrag = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("searchUrl", searchUrl);

                searchFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, searchFrag).commit();
            }
        });
    }
}

