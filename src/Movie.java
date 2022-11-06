import java.util.ArrayList;
import java.util.List;

public class Movie {

    private String id;

    private String title;

    private int year;

    private String director;

    private List<String> genres;

    public Movie() {
        genres = new ArrayList<>();
    }

    public Movie(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director  = director;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void addGenre(String genre) {
        if (!genres.contains(genre)) {
            genres.add(genre);
        }
    }

    public void linkGenre(int index, String genre) {
        genres.set(index, genre);
    }

    public List<String> getGenres() {
        return genres;
    }

    public String toString() {
        String string = "";
        string += "Movie Details - ";
        string += "Id:" + getId();
        string += ", ";
        string += "Title:" + getTitle();
        string += ", ";
        string += "Year:" + getYear();
        string += ", ";
        string += "Director:" + getDirector();
        string += ", ";
        string += "Genres:" + getGenres();

        return string;
    }
}