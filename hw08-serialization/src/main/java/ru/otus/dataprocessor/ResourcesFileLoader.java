package ru.otus.dataprocessor;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import ru.otus.model.Measurement;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final List<Measurement> measurements;

    public ResourcesFileLoader(String fileName) throws FileProcessException {
        try {
            var resource = getClass().getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new FileProcessException(String.format("Не удалось открыть файл: %s", fileName));
            }
            var jsonValue = Files.readString(Path.of(resource.toURI()));
            this.measurements = new Gson().fromJson(jsonValue, new TypeToken<List<Measurement>>() {
            }.getType());
        } catch (URISyntaxException | IOException e) {
            throw new FileProcessException(e);
        }
    }

    @Override
    public List<Measurement> load() {
        return measurements;
    }
}
