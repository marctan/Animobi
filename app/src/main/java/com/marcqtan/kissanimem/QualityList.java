package com.marcqtan.kissanimem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QualityList extends AppCompatActivity {
    ListView listView;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_list);
        final List<String> quality = getIntent().getStringArrayListExtra("vidurl");
        ArrayList<String> ep_name = getIntent().getStringArrayListExtra("ep_name");
        listView = (ListView) findViewById(R.id.list);
        frame = findViewById(R.id.progressBarContainer);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ep_name);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index

                // ListView Clicked item value
                String itemValue = quality.get(position);
                new getAnimeVideo().execute(itemValue);
            }
        });
    }

    public class getAnimeVideo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frame.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                return getVideoLink(doc);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String video) {
            super.onPostExecute(video);
            frame.setVisibility(View.GONE);
            if(video == null) {
                Log.v("Error", "Error fetching video quality url");
            } else {
                Intent i = new Intent(QualityList.this, exoactivity.class);
                i.putExtra("vidurl", video);
                i.putExtra("episodeName", getIntent().getStringExtra("episodeName"));
                i.putExtra("animeName", getIntent().getStringExtra("animeName"));
                startActivity(i);
            }
        }
    }

    private String getVideoLink(Document doc) {
        return doc.select("video#videojs").select("source[src]").attr("src");
    }
}
