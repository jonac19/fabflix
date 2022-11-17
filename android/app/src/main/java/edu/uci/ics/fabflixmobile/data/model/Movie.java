package edu.uci.ics.fabflixmobile.data.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String title;
    private final int year;
    private final String director;
    private final ArrayList<String> genres;
    private final ArrayList<String> stars;

    public Movie(String title, int year, String director, ArrayList<String> genres, ArrayList<String> stars) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getStars() {
        return stars;
    }
}