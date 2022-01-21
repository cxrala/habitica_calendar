package habiticasupport;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class HabiticaCredentials {
    String userId;
    String apiId;

    public static HabiticaCredentials getFromJSON(String pathname) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(pathname));
        Gson gson = new Gson();
        return gson.fromJson(reader, HabiticaCredentials.class);
    }

    public String getUserId() {
        return userId;
    }

    public String getApiId() {
        return apiId;
    }
}
