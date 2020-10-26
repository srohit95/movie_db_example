package com.example.movie_db_example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
    import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

Internet_status internet_status;
List<Movie> movie_list=new ArrayList<>();
int page=1,max=0;
    MovieAdapter movieAdapter;
    ListView list;
    Button previous,next;
    database db;
    Cursor c1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=findViewById(R.id.list);
        previous=findViewById(R.id.previous);
        next=findViewById(R.id.next);
        db=new database(this);

        setTitle("Home");

        internet_status=new Internet_status(this);
        if(internet_status.internet_status()){
            new Async_call().execute();
        }else{
            offline();
            Toast.makeText(this, "Check Internet connectivity!", Toast.LENGTH_SHORT).show();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i1=new Intent(MainActivity.this,Detailaview.class);
                i1.putExtra("jdata",movie_list.get(position).getJdata());
                startActivity(i1);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet_status.internet_status()){
                    if(page>1){
                        page--;
                        new Async_call().execute();
                    }else{
                        Toast.makeText(MainActivity.this, "No page", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Check Internet connectivity!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet_status.internet_status()){
                    if(page<max){
                        page++;
                        new Async_call().execute();
                    }else{
                        Toast.makeText(MainActivity.this, "No page", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Check Internet connectivity!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    ProgressDialog progressDialog;
    URL url;
    HttpURLConnection httpURLConnection;
    BufferedReader br;
    StringBuilder sb;
    String s1 = "",key="";
    class Async_call extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                if (internet_status.internet_status()) {

                    String out_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key="+key+"&language=en-US&page="+page;
                    url = new URL(out_URL);//send the data for the url
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    sb = new StringBuilder();
                    String line;
                    try {
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    s1 = sb.toString().replaceAll("\\\\", "");
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return s1.trim();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                max=jsonObject.getInt("total_pages");
                movie_list.clear();
                JSONArray jsonArray=jsonObject.getJSONArray("results");
                for(int i=0;i<jsonArray.length();i++){
                    movie_list.add(new Movie(jsonArray.getJSONObject(i).toString()));
                }
                movieAdapter = new MovieAdapter(MainActivity.this, R.layout.brief_movie, movie_list);
                list.setAdapter(movieAdapter);

            }catch(JSONException e){
                Toast.makeText(MainActivity.this, "Server Json Issue", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    List<Movie> datalist=new ArrayList<>();
    public class MovieAdapter extends ArrayAdapter<Movie> {

        //the list values in the List of type hero
        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public MovieAdapter(Context context, int resource, List<Movie> dataList1) {
            super(context, resource, dataList1);
            this.context = context;
            this.resource = resource;
            datalist = dataList1;
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            //we need to get the view of the xml for our list item
            //And for this we need a layoutinflater
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            //getting the view
            View view = layoutInflater.inflate(resource, null, false);

            //getting the view elements of the list from the view
            final TextView title = view.findViewById(R.id.title);
            final TextView popular = view.findViewById(R.id.popular);
            final TextView date = view.findViewById(R.id.date);
            final TextView vote = view.findViewById(R.id.vote);
            final TextView voteavg = view.findViewById(R.id.voteavg);
            final ImageView imageView=view.findViewById(R.id.image);


            try {
                Movie data = datalist.get(position);
                JSONObject jsonObject = new JSONObject(data.getJdata());
                title.setText(jsonObject.getString("original_title"));
                popular.setText(jsonObject.getString("popularity"));
                date.setText(jsonObject.getString("release_date"));
                vote.setText(jsonObject.getString("vote_count"));
                voteavg.setText(jsonObject.getString("vote_average"));

                Glide.with(context)
                        .load("https://image.tmdb.org/t/p/w500"+jsonObject.getString("poster_path"))
                        .into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }



            return view;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.voteasc:
                try {
                    Collections.sort(movie_list, new DateComparatorAsc());
                    movieAdapter.notifyDataSetChanged();
                     } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.votedsc:
                Collections.sort(movie_list, new DateComparatorDes());
                movieAdapter.notifyDataSetChanged();
                return true;

            case R.id.popularasc:
                try {
                    Collections.sort(movie_list, new PopularityAsc());
                    movieAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.populardes:
                Collections.sort(movie_list, new PopularityDes());
                movieAdapter.notifyDataSetChanged();
                return true;

            case R.id.voteavgasc:
                try {
                    Collections.sort(movie_list, new AvgVoteAsc());
                    movieAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.voteavgdes:
                Collections.sort(movie_list, new AvgVoteDes());
                movieAdapter.notifyDataSetChanged();
                return true;

            case R.id.dateasc:
                try {
                    Collections.sort(movie_list, new DateAsc());
                    movieAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.datedes:
                Collections.sort(movie_list, new DateDes());
                movieAdapter.notifyDataSetChanged();
                return true;
            case R.id.titleasc:
                try {
                    Collections.sort(movie_list, new TitleAsc());
                    movieAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.titledes:
                Collections.sort(movie_list, new TitleDes());
                movieAdapter.notifyDataSetChanged();
                return true;
            case R.id.offline:
                offline();
                return true;
            case R.id.refresh:
                if(internet_status.internet_status()){
                    new Async_call().execute();
                }else{
                    offline();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void offline() {
        try {
            movie_list.clear();
            c1 = db.tbl_show();
            if (c1.getCount() != 0){
                c1.moveToFirst();
                while (c1.isAfterLast()==false) {
                    movie_list.add(new Movie(c1.getString(0)));
                    c1.moveToNext();
                }
            }
            c1.close();
            movieAdapter = new MovieAdapter(MainActivity.this, R.layout.brief_movie, movie_list);
            list.setAdapter(movieAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DateComparatorAsc implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                Double distance = Double.valueOf(lhs.getVote_count());
                Double distance1 = Double.valueOf(rhs.getVote_count());
                if (distance.compareTo(distance1) < 0) {
                    return -1;
                } else if (distance.compareTo(distance1) > 0) {
                    return 1;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }



    public class DateComparatorDes implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                Double distance = Double.valueOf(lhs.getVote_count());
                Double distance1 = Double.valueOf(rhs.getVote_count());
                if (distance.compareTo(distance1) > 0) {
                    return -1;
                } else if (distance.compareTo(distance1) < 0) {
                    return 1;
                } /*else {
                    return 0;
                }*/
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class PopularityAsc implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                Double distance = Double.valueOf(lhs.getPopularity());
                Double distance1 = Double.valueOf(rhs.getPopularity());
                if (distance.compareTo(distance1) < 0) {
                    return -1;
                } else if (distance.compareTo(distance1) > 0) {
                    return 1;
                } /*else {
                    return 0;
                }*/
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class PopularityDes implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                Double distance = Double.valueOf(lhs.getPopularity());
                Double distance1 = Double.valueOf(rhs.getPopularity());
                if (distance.compareTo(distance1) > 0) {
                    return -1;
                } else if (distance.compareTo(distance1) < 0) {
                    return 1;
                } /*else {
                    return 0;
                }*/
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class AvgVoteAsc implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                Double distance = Double.valueOf(lhs.getVote_average());
                Double distance1 = Double.valueOf(rhs.getVote_average());
                if (distance.compareTo(distance1) < 0) {
                    return -1;
                } else if (distance.compareTo(distance1) > 0) {
                    return 1;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class AvgVoteDes implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                Double distance = Double.valueOf(lhs.getVote_average());
                Double distance1 = Double.valueOf(rhs.getVote_average());
                if (distance.compareTo(distance1) > 0) {
                    return -1;
                } else if (distance.compareTo(distance1) < 0) {
                    return 1;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class DateAsc implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = sdf.parse(lhs.getRelease_date());
                Date d2 = sdf.parse(rhs.getRelease_date());

                return (d1.getTime() > d2.getTime() ? 1 : -1);
            } catch (NumberFormatException | ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class DateDes implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = sdf.parse(lhs.getRelease_date());
                Date d2 = sdf.parse(rhs.getRelease_date());

                return (d1.getTime() > d2.getTime() ? -1 : 1);
            } catch (NumberFormatException | ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class TitleAsc implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {

                return lhs.getOriginal_title().compareToIgnoreCase(rhs.getOriginal_title());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class TitleDes implements Comparator<Movie> {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            try {

                return rhs.getOriginal_title().compareToIgnoreCase(lhs.getOriginal_title());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

}
