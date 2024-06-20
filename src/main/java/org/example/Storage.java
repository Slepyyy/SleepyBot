package org.example;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Storage class for handling JSON data persistence to a file.
 */
public class Storage {
    private static final String FILE_PATH = "data.json";
    private JSONObject data;

    /**
     * Constructor that initializes the Storage object.
     * If the data file exists, it reads the content into a JSONObject.
     * If the data file does not exist or an error occurs, it initializes an empty JSONObject.
     */
    public Storage() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                data = new JSONObject(content);
            } catch (IOException e) {
                e.printStackTrace();
                data = new JSONObject();
            }
        } else {
            data = new JSONObject();
        }
    }

    /**
     * Saves the current state of the data JSONObject to the file.
     */
    public void save() {
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(data.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the data JSONObject.
     *
     * @return the data JSONObject.
     */
    public JSONObject getData() {
        return data;
    }
}
