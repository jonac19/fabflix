package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView subtitle_year;
        TextView subtitle_director;
        TextView subtitle_genres;
        TextView subtitle_stars;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.subtitle_year = convertView.findViewById(R.id.subtitle_year);
            viewHolder.subtitle_director = convertView.findViewById(R.id.subtitle_director);
            viewHolder.subtitle_genres = convertView.findViewById(R.id.subtitle_genres);
            viewHolder.subtitle_stars = convertView.findViewById(R.id.subtitle_stars);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getTitle());
        viewHolder.subtitle_year.setText(movie.getYear() + "");
        viewHolder.subtitle_director.setText(movie.getDirector());

        String genres_text = "";
        if (movie.getGenres().size() > 0) {
            for (int i = 0; i < Math.min(3, movie.getGenres().size()); i++) {
                genres_text += movie.getGenres().get(i) + ", ";
            }
            genres_text = genres_text.substring(0, genres_text.lastIndexOf(", "));
        }
        viewHolder.subtitle_genres.setText(genres_text);


        String stars_text = "";
        if (movie.getStars().size() > 0) {
            for (int i = 0; i < Math.min(3, movie.getStars().size()); i++) {
                stars_text += movie.getStars().get(i) + ", ";
            }
            stars_text = stars_text.substring(0, stars_text.lastIndexOf(", "));
        }
        viewHolder.subtitle_stars.setText(stars_text);

        // Return the completed view to render on screen
        return convertView;
    }
}