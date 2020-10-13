package com.example.movie_db_example;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {

    String popularity="",vote_count="",adult="",backdrop_path="",original_language="",original_title="",title="",vote_average="",overview="",release_date="";
    String jdata="";

    public Movie(String data){
        jdata=data;
        try {
            JSONObject jsonObject=new JSONObject(jdata);
            vote_count=""+jsonObject.getInt("vote_count");
            popularity=""+jsonObject.getDouble("popularity");
            vote_average=""+jsonObject.getDouble("vote_average");
            release_date=jsonObject.getString("release_date");
            original_title=jsonObject.getString("original_title");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getPopularity() {
        return popularity;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getVote_count() {
        return vote_count.trim();
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getJdata() {
        return jdata;
    }
}
