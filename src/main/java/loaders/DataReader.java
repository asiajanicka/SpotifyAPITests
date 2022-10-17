package loaders;

import lombok.SneakyThrows;

import java.util.Properties;

public abstract class DataReader {
    protected Properties properties;

    @SneakyThrows
    public DataReader(String path) {
        this.properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(path));
        loadData();
    }

    abstract void loadData();
}
