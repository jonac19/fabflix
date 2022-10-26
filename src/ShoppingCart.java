import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashMap;

public class ShoppingCart {

    int size;
    HashMap<String, Integer> movie_ids;

    public ShoppingCart() {
        this.size = 0;
        this.movie_ids = new HashMap<String, Integer>();
    }

    public void incrementMovie(String movie_id) {
        movie_ids.put(movie_id, movie_ids.getOrDefault(movie_id, 0) + 1);
    }

    public void decrementMovie(String movie_id) {
        if (movie_ids.containsKey(movie_id)) {
            movie_ids.put(movie_id, movie_ids.get(movie_id) - 1);
        }

        if (movie_ids.containsKey(movie_id) && movie_ids.get(movie_id) == 0) {
            movie_ids.remove(movie_id);
        }
    }

    public void removeMovie(String movie_id) {
        if (movie_ids.containsKey(movie_id)) {
            movie_ids.remove(movie_id);
        }
    }

    public void flush() {
        movie_ids.clear();
    }

    public int size() {
        int total = 0;

        for (String movie_id : movie_ids.keySet()) {
            total += movie_ids.get(movie_id);
        }

        return total;
    }

    public JsonArray toJsonArray() {
        JsonArray jsonArray = new JsonArray();

        Object[] sortedKeys = movie_ids.keySet().toArray();
        Arrays.sort(sortedKeys);
        for (Object movie_id_object : sortedKeys) {
            String movie_id = (String) movie_id_object;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_id", movie_id);
            jsonObject.addProperty("quantity", String.valueOf(movie_ids.get(movie_id)));
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

}