package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movie.MovieActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b-fall22-team-46";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        intent = getIntent();

        configureSearchView();
        configurePrevButton();
        configureNextButton();
        loadMovieList();
    }

    private void configureSearchView() {
        SearchView searchView = findViewById(R.id.search);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                finish();
                // initialize the activity(page)/destination
                Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                MovieListPage.putExtra("page", 1);
                MovieListPage.putExtra("search_title", query);
                // activate the list page.
                startActivity(MovieListPage);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private void configurePrevButton() {
        Button prevButton = findViewById(R.id.previous);
        int page = intent.getIntExtra("page", 1);

        if (page > 1) {
            prevButton.setOnClickListener(view -> previous());
        } else {
            prevButton.setEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n")
    private void previous() {
        finish();

        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("page", intent.getIntExtra("page", 2) - 1);
        MovieListPage.putExtra("search_title", intent.getStringExtra("search_title"));

        startActivity(MovieListPage);
    }

    private void configureNextButton() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        int page = intent.getIntExtra("page", 1);
        String search_title = intent.getStringExtra("search_title");
        String search_url = baseURL + "/api/movie-list?limit=" + "20"
                + "&criteria=" + "rating"
                + "&orderFirst=" + "desc"
                + "&orderSecond=" + "asc"
                + "&page=" + (page + 1)
                + "&searchTitle=" + search_title
                + "&searchYear="
                + "&searchDirector="
                + "&searchStar="
                + "&browseGenre="
                + "&browseTitle=";
        final StringRequest movieListRequest = new StringRequest(
                Request.Method.GET,
                search_url,
                response -> {
                    Button nextButton = findViewById(R.id.next);
                    try {
                        JSONArray jsonMoviesArray = new JSONArray(response);

                        if (jsonMoviesArray.length() > 0) {
                            nextButton.setOnClickListener(view -> next());
                        } else {
                            nextButton.setEnabled(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("next.error", error.toString());
                });
        queue.add(movieListRequest);
    }

    private void next() {
        finish();

        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("page", intent.getIntExtra("page", 1) + 1);
        MovieListPage.putExtra("search_title", intent.getStringExtra("search_title"));

        startActivity(MovieListPage);
    }

    private void loadMovieList() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        int page = intent.getIntExtra("page", 1);
        String search_title = intent.getStringExtra("search_title");
        String search_url = baseURL + "/api/movie-list?limit=" + "20"
                + "&criteria=" + "rating"
                + "&orderFirst=" + "desc"
                + "&orderSecond=" + "asc"
                + "&page=" + page
                + "&searchTitle=" + search_title
                + "&searchYear="
                + "&searchDirector="
                + "&searchStar="
                + "&browseGenre="
                + "&browseTitle=";
        final StringRequest movieListRequest = new StringRequest(
                Request.Method.GET,
                search_url,
                response -> {
                    final ArrayList<Movie> movies = new ArrayList<>();

                    try {
                        JSONArray jsonMoviesArray = new JSONArray(response);
                        for (int i = 0; i < jsonMoviesArray.length(); i++) {
                            JSONObject jsonMovieObject = jsonMoviesArray.getJSONObject(i);
                            String movieTitle = jsonMovieObject.getString("movie_title");
                            int movieYear = jsonMovieObject.getInt("movie_year");
                            String movieDirector = jsonMovieObject.getString("movie_director");

                            ArrayList<String> movieGenres = new ArrayList<>();
                            JSONArray jsonMovieGenresArray = jsonMovieObject.getJSONArray("movie_genres");
                            for (int j = 0; j < jsonMovieGenresArray.length(); j++) {
                                movieGenres.add(jsonMovieGenresArray.getJSONObject(j).getString("genre_name"));
                            }

                            ArrayList<String> movieStars = new ArrayList<>();
                            JSONArray jsonMovieStarsArray = jsonMovieObject.getJSONArray("movie_stars");
                            for (int j = 0; j < jsonMovieStarsArray.length(); j++) {
                                movieStars.add(jsonMovieStarsArray.getJSONObject(j).getString("star_name"));
                            }

                            movies.add(new Movie(movieTitle, movieYear, movieDirector, movieGenres, movieStars));
                        }

                        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movies.get(position);

                            finish();
                            // initialize the activity(page)/destination
                            Intent MoviePage = new Intent(MovieListActivity.this, MovieActivity.class);
                            MoviePage.putExtra("movie_title", movie.getTitle());
                            MoviePage.putExtra("movie_year", movie.getYear());
                            MoviePage.putExtra("movie_director", movie.getDirector());
                            MoviePage.putExtra("movie_genres", movie.getGenres());
                            MoviePage.putExtra("movie_stars", movie.getStars());
                            // activate the list page.
                            startActivity(MoviePage);
                        });
                    } catch (JSONException e) {
                        System.out.println("ERROR");
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("movie_list.error", error.toString());
                });
        queue.add(movieListRequest);
    }
}