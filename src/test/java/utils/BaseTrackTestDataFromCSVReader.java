package utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BaseTrackTestDataFromCSVReader {
    @Getter
    private List<Track> tracks;
    private String filePath = "src/test/resources/tracksTestData.csv";
    public BaseTrackTestDataFromCSVReader() {
        tracks = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        Reader reader;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(String.format("There is no file %s", filePath));
        }

        CsvToBean<Track> csvToBean = new CsvToBeanBuilder(reader)
                .withType(Track.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        tracks = csvToBean.parse();
    }
}
