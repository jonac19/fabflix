package edu.uci.ics.fabflixmobile.ui.movie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovieBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {
    private Intent intent;

    private TextView title;
    private TextView subtitle_year;
    private TextView subtitle_director;
    private TextView subtitle_genres;
    private TextView subtitle_stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieBinding binding = ActivityMovieBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        intent = getIntent();

        title = binding.title;
        subtitle_year = binding.subtitleYear;
        subtitle_director = binding.subtitleDirector;
        subtitle_genres = binding.subtitleGenres;
        subtitle_stars = binding.subtitleStars;

        title.setText(intent.getStringExtra("movie_title"));
        subtitle_year.setText(intent.getIntExtra("movie_year", 2000) + "");
        subtitle_director.setText(intent.getStringExtra("movie_director"));
        subtitle_genres.setText(intent.getStringExtra("movie_title"));

        String genres_text = "";
        ArrayList<String> movie_genres = intent.getStringArrayListExtra("movie_genres");
        if (movie_genres.size() > 0) {
            for (String genre_name: movie_genres) {
                genres_text += genre_name + ", ";
            }
            genres_text = genres_text.substring(0, genres_text.lastIndexOf(", "));
        }
        subtitle_genres.setText(genres_text);

        String stars_text = "";
        ArrayList<String> movie_stars = intent.getStringArrayListExtra("movie_stars");
        if (movie_stars.size() > 0) {
            for (String star_name: movie_stars) {
                stars_text += star_name + ", ";
            }
            stars_text = stars_text.substring(0, stars_text.lastIndexOf(", "));
        }
        subtitle_stars.setText(stars_text);
    }
}