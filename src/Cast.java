public class Cast {
    private String movieId;

    private String starName;

    private String starId;

    public Cast(){

    }

    public Cast(String movieId, String starName) {
        this.movieId = movieId;
        this.starName = starName;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getStarName() {
        return starName;
    }

    public void setStarName(String starName) {
        this.starName = starName;
    }

    public String getStarId() {
        return starId;
    }

    public void setStarId(String starId) {
        this.starId = starId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Cast Details - ");
        sb.append("Movie ID:" + getMovieId());
        sb.append(", ");
        sb.append("Star Name:" + getStarName());

        return sb.toString();
    }
}