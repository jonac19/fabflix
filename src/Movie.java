import java.util.ArrayList;
import java.util.List;

public class Movie {

    private String movieId;

    private String movieTitle;

    private int movieYear;

    private String movieDirector;

    private List<String> genreNames;

    public Movie() {
        genreNames = new ArrayList<>();
    }

    public Movie(String movieId, String movieTitle, int movieYear, String movieDirector) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieYear = movieYear;
        this.movieDirector  = movieDirector;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return movieTitle;
    }

    public void setTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getYear() {
        return movieYear;
    }

    public void setYear(int movieYear) {
        this.movieYear = movieYear;
    }

    public String getDirector() {
        return movieDirector;
    }

    public void setDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public void addGenreName(String genreName) {
        if (!genreNames.contains(genreName)) {
            genreNames.add(genreName);
        }
    }

    public List<String> getGenreNames() {
        return genreNames;
    }

    public String toString() {
        String string = "";
        string += "Movie Details - ";
        string += "ID:" + getMovieId();
        string += ", ";
        string += "Title:" + getTitle();
        string += ", ";
        string += "Year:" + getYear();
        string += ", ";
        string += "Director:" + getDirector();
        string += ", ";
        string += "Genres:" + getGenreNames();

        return string;
    }
}