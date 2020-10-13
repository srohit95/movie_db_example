package com.example.movie_db_example;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Detailaview extends AppCompatActivity {

    TextView    title,popular,  adult,totalvote, voteavg,releasedate, language,overview;
ImageView image;
Button save,delete;
database db;
String movieid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailaview);

        overview=findViewById(R.id.overview);
        title=findViewById(R.id.title);
        popular=findViewById(R.id.popular);
        adult=findViewById(R.id.adult);
        totalvote=findViewById(R.id.totalvote 	);
        voteavg=findViewById(R.id.voteavg);
        releasedate=findViewById(R.id.releasedate);
        language=findViewById(R.id.language);
        image=findViewById(R.id.image);
        save=findViewById(R.id.save);
        delete=findViewById(R.id.delete);
        db=new database(this);

        setTitle("Movie Overview");

        try {
            final JSONObject jsonObject=new JSONObject(getIntent().getStringExtra("jdata"));

            title.setText(jsonObject.getString("original_title"));
            popular.setText(jsonObject.getString("popularity"));
            releasedate.setText(jsonObject.getString("release_date"));
            totalvote.setText(jsonObject.getString("vote_count"));
            voteavg.setText(jsonObject.getString("vote_average"));
            language.setText(jsonObject.getString("original_language"));
            overview.setText(jsonObject.getString("overview"));
            movieid=jsonObject.getString("id");

            if(jsonObject.getBoolean("adult")){
                adult.setText("A");
            }else{
                adult.setText("U");
            }

            Glide.with(Detailaview.this)
                    .load("https://image.tmdb.org/t/p/w500"+jsonObject.getString("backdrop_path"))
                    .into(image);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (db.tbl_insert(jsonObject.toString(),movieid)) {
                        Toast.makeText(Detailaview.this, "Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Detailaview.this, "Not Saved", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (db.tbl_delete(movieid.trim())) {
                        Toast.makeText(Detailaview.this, "Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Detailaview.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}