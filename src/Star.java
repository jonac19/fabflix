public class Star {

    private String id;

    private String name;

    private int birthYear;

    private String movieId;

    public Star(){

    }

    public Star(String id, String name, int birthYear, String movieId) {
        this.id  = id;
        this.name = name;
        this.birthYear = birthYear;
        this.movieId = movieId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Birth Year:" + getBirthYear());
        sb.append(", ");
        sb.append("Movie Id:" + getMovieId());
        sb.append(".");

        return sb.toString();
    }
}