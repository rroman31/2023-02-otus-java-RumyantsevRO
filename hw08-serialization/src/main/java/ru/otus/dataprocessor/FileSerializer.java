package ru.otus.dataprocessor;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class FileSerializer implements Serializer {

    private final Gson gson;

    private final Path pathToFile;

    public FileSerializer(String fileName) {
        this.gson = new Gson();
        this.pathToFile = Paths.get(fileName);
    }

    @Override
    public void serialize(Map<String, Double> data) throws FileProcessException {
        var orderedMap = new TreeMap<>(data);
        var jsonValue = gson.toJson(orderedMap);
        try {
            Files.writeString(pathToFile, jsonValue);
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
