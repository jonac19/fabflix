import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;

import static java.lang.Integer.parseInt;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movieList"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    private ServletContext servletContext;

    private long elapsedTS;
    private long elapsedTJ;

    public void init(ServletConfig config) {
        try {
            super.init(config);
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            servletContext = config.getServletContext();
        } catch (NamingException | ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTS = System.nanoTime();
        processRequest(request, response);
        elapsedTS = System.nanoTime() - startTS;

        logPerformance();
    }

    void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameters from url request.
        String limit = request.getParameter("limit");
        String criteria = request.getParameter("criteria");
        String orderFirst = request.getParameter("orderFirst");
        String orderSecond = request.getParameter("orderSecond");
        String page = request.getParameter("page");
        String searchTitle = request.getParameter("searchTitle");
        searchTitle = formatSearchTitle(searchTitle);
        String searchYear = request.getParameter("searchYear");
        String searchDirector = request.getParameter("searchDirector");
        String searchStar = request.getParameter("searchStar");
        String browseGenre = request.getParameter("browseGenre");
        String browseTitle = request.getParameter("browseTitle");

        // The log messages can be found in localhost log
        request.getServletContext().log("getting limit: " + limit);
        request.getServletContext().log("getting criteria: " + criteria);
        request.getServletContext().log("getting orderFirst: " + orderFirst);
        request.getServletContext().log("getting orderSecond: " + orderSecond);
        request.getServletContext().log("getting page: " + page);
        request.getServletContext().log("getting searchTitle:" + searchTitle);
        request.getServletContext().log("getting searchYear:" + searchYear);
        request.getServletContext().log("getting searchDirector:" + searchDirector);
        request.getServletContext().log("getting searchStar:" + searchStar);
        request.getServletContext().log("getting browseGenre:" + browseGenre);
        request.getServletContext().log("getting browseTitle:" + browseTitle);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            long startTJ = System.nanoTime();

            String query = constructQuery(criteria, orderFirst, orderSecond, searchTitle, searchYear, searchDirector, searchStar, browseGenre, browseTitle);

            // Set the additional parameters
            PreparedStatement statement = prepareStatement(conn, query, limit, page, searchTitle, searchYear, searchDirector, searchStar, browseGenre, browseTitle);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                JsonArray movie_genres = getGenres(conn, movie_id);
                JsonArray movie_stars = getStars(conn, movie_id);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("movie_genres", movie_genres);
                jsonObject.add("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            elapsedTJ = System.nanoTime() - startTJ;

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }

    synchronized void logPerformance() {
//        String contextPath = servletContext.getRealPath("/");
//        String logFilePath = contextPath + "../logs/current_case.txt";
//        String logFilePath = contextPath + "logs/current_case.txt";

        String contextPath = "~/logs/current_case.txt";
        String logFilePath = "~/logs/current_case.txt";
        servletContext.log("\n\nFrom ServletContext, Context Path: " + contextPath + "\n\n");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)))){
            out.println(elapsedTS + "," + elapsedTJ);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reformats searchTitle to surround every word in string with "+word*" operators
     * @param inputTitle user-entered raw input of searchTitle
     * @return searchTitle reformatted with "+word*" around every word in String
     */
    String formatSearchTitle(String inputTitle){
        if (inputTitle.equals("")) return "";   // Do not act on blank strings
        String reformattedString = "";
        for (String word : inputTitle.split("\\s+")){
            reformattedString += "+" + word + "* ";
        }
        reformattedString.trim();
        return reformattedString;
    }

    /**
     * Constructs the base query with criteria and order options
     * @param criteria Column name to sort movies by
     * @param orderFirst Sorting order for first column either ascending or descending
     * @param orderSecond Sorting order for second column either ascending or descending
     * @return Query to be processed by MySQL
     * @throws Exception Invalid parameters in URL
     */
    private String constructQuery(String criteria, String orderFirst, String orderSecond, String searchTitle, String searchYear,
                                  String searchDirector, String searchStar, String browseGenre, String browseTitle) throws Exception {
        String query = "SELECT " +
                            "M.id, " +
                            "M.title, " +
                            "M.year, " +
                            "M.director, " +
                            "R.rating, " +
                            "GROUP_CONCAT(DISTINCT S.id SEPARATOR ',') AS genre_ids, " +
                            "GROUP_CONCAT(DISTINCT G.id SEPARATOR ',') AS star_ids " +
                        "FROM " +
                            "(SELECT " +
                                "R.movieId, " +
                                "R.rating " +
                            "FROM movies AS M, ratings AS R " +
                            "WHERE M.id = R.movieId " +
                            "ORDER BY ";

        if (criteria.equals("") || criteria.equals("rating")) {
            query += "rating ";
        } else if (criteria.equals("title")) {
            query += "title ";
        } else {
            throw new Exception("Invalid criteria for sorting");
        }

        if (orderFirst.equals("") || orderFirst.equals("desc")) {
            query += "DESC, ";
        } else if (orderFirst.equals("asc")) {
            query += "ASC, ";
        } else {
            throw new Exception("Invalid order for sorting");
        }

        if (criteria.equals("") || criteria.equals("rating")) {
            query += "title ";
        } else  {
            query += "rating ";
        }

        if (orderSecond.equals("") || orderSecond.equals("asc")) {
            query += "ASC";
        } else if (orderSecond.equals("desc")) {
            query += "DESC";
        } else {
            throw new Exception("Invalid order for sorting");
        }

        query +=    ") AS R, " +
                    "movies AS M, " +
                    "stars AS S, " +
                    "stars_in_movies AS SM, " +
                    "genres AS G, " +
                    "genres_in_movies AS GM " +
                "WHERE " +
                    "M.id = R.movieId " +
                    "AND M.id = SM.movieId " +
                    "AND S.id = SM.starId " +
                    "AND M.id = GM.movieId " +
                    "AND G.id = GM.genreId ";

        if (!searchTitle.equals("")) {
            query += "AND MATCH(M.title) AGAINST (? IN BOOLEAN MODE) "; //Changed from "LIKE ?" in Proj4: Implement fulltext search
        }

        if (!searchYear.equals("")) {
            query += "AND M.year = ? ";
        }

        if (!searchDirector.equals("")) {
            query += "AND M.director LIKE ? ";
        }

        if (!browseTitle.equals("")) {
            if (browseTitle.equals("*")) {
                query += "AND M.title REGEXP '^[^a-zA-Z0-9]+.?'";
            } else {
                query += "AND M.title LIKE ? ";
            }
        }

        query += "GROUP BY M.id ";

        if (!searchStar.equals("")) {
            query += "HAVING sum(S.name LIKE ?) > 0 ";
        }

        if (!browseGenre.equals("")) {
            query += "HAVING sum(G.id = ?) > 0 ";
        }

        query += "ORDER BY ";

        if (criteria.equals("") || criteria.equals("rating")) {
            query += "rating ";
        }  else  {
            query += "title ";
        }

        if (orderFirst.equals("") || orderFirst.equals("desc")) {
            query += "DESC, ";
        } else {
            query += "ASC, ";
        }

        if (criteria.equals("") || criteria.equals("rating")) {
            query += "title ";
        } else {
            query += "rating ";
        }

        if (orderSecond.equals("") || orderSecond.equals("asc")) {
            query += "ASC ";
        } else {
            query += "DESC ";
        }

        query += "LIMIT ? " +
                 "OFFSET ? ";

        return query;
    }

    private PreparedStatement prepareStatement(Connection conn, String query, String limit, String page,
                                               String searchTitle, String searchYear, String searchDirector,
                                               String searchStar, String browseGenre, String browseTitle) throws Exception{
        // Declare our statement
        PreparedStatement statement = conn.prepareStatement(query);

        int index = 1;

        if (!searchTitle.equals("")) {
            statement.setString(index, "%" + searchTitle + "%");
            index += 1;
        }

        if (!searchYear.equals("")) {
            statement.setInt(index, parseInt(searchYear));
            index += 1;
        }

        if (!searchDirector.equals("")) {
            statement.setString(index, "%" + searchDirector + "%");
            index += 1;
        }

        if (!browseTitle.equals("") && !browseTitle.equals("*")) {
            statement.setString(index, browseTitle + "%");
            index += 1;
        }

        if (!searchStar.equals("")) {
            statement.setString(index, "%" + searchStar + "%");
            index += 1;
        }

        if (!browseGenre.equals("")) {
            statement.setString(index, browseGenre);
            index += 1;
        }

        if (!limit.equals("")) {
            statement.setInt(index, parseInt(limit));
        } else {
            statement.setInt(index, 20);
        }
        index += 1;

        if (!page.equals("")) {
            if (!limit.equals("")) {
                statement.setInt(index, parseInt(limit) * (parseInt(page) - 1));
            } else {
                statement.setInt(index, 20 * (parseInt(page) - 1));
            }
        } else {
            statement.setInt(index, 0);
        }

        return statement;
    }

    /**
     * @param conn Existing connection to MySQL database
     * @param movie_id Movie ID to obtain genres for
     * @return Array containing all genres associated with the given movie
     * @throws SQLException Failed MySQL query
     */
    private JsonArray getGenres(Connection conn, String movie_id) throws SQLException {
        String query = "SELECT * FROM genres G, genres_in_movies GM WHERE G.id = GM.genreId AND GM.movieId = ? ORDER BY G.name";

        // Declare our statement
        PreparedStatement statement = conn.prepareStatement(query);

        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, movie_id);

        // Perform the query
        ResultSet rs = statement.executeQuery();

        JsonArray jsonArray = new JsonArray();

        while (rs.next()) {
            String genre_id = rs.getString("genreId");
            String genre_name = rs.getString("name");

            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("genre_id", genre_id);
            jsonObject.addProperty("genre_name", genre_name);

            jsonArray.add(jsonObject);
        }
        rs.close();
        statement.close();

        return jsonArray;
    }

    /**
     * @param conn Existing connection to MySQL database
     * @param movie_id Movie ID to obtain genres for
     * @return Array containing all stars associated with the given movie
     * @throws SQLException Failed MySQL query
     */
    private JsonArray getStars(Connection conn, String movie_id) throws SQLException {
        String query = "SELECT * FROM stars S, stars_in_movies SM WHERE S.id = SM.starId AND SM.movieId = ? ORDER BY S.name";

        // Declare our statement
        PreparedStatement statement = conn.prepareStatement(query);

        // Set the parameter represented by "?" in the query to the id we get from url,
        // num 1 indicates the first "?" in the query
        statement.setString(1, movie_id);

        // Perform the query
        ResultSet rs = statement.executeQuery();

        JsonArray jsonArray = new JsonArray();

        while (rs.next()) {
            String star_id = rs.getString("starId");
            String star_name = rs.getString("name");
            String star_birthYear = rs.getString("birthYear");

            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("star_id", star_id);
            jsonObject.addProperty("star_name", star_name);
            jsonObject.addProperty("star_birthYear", star_birthYear);

            jsonArray.add(jsonObject);
        }
        rs.close();
        statement.close();

        return jsonArray;
    }
}