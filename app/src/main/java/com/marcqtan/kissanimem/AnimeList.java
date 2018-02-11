package com.marcqtan.kissanimem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by dell on 25/10/2017.
 */

public class AnimeList {

    private String m_animeName;
    private String m_animeLink;
    private String m_thumbnail;
    private ArrayList<Map.Entry<String,String>> m_episodeList;

    public String getAnimeName() {
        return m_animeName;
    }

    public void setAnimeName(String animename){
        m_animeName = animename;
    }

    public void setThumbNail(String image){
        m_thumbnail = image;
    }

    public String getAnimeLink() {
        return m_animeLink;
    }

    public String getAnimeThumbnail() {
        return m_thumbnail;
    }

    public void setAnimeLink(String animelink){
        m_animeLink = animelink;
    }

    public void addEpisodeInfo(Map.Entry<String,String> episode) {
        m_episodeList.add(episode);
    }

    public ArrayList<Map.Entry<String,String>> retrieveEpisodes () {
        return m_episodeList;
    }

    public void initEpisodeList(){
        m_episodeList = new ArrayList<>();
    }

}
