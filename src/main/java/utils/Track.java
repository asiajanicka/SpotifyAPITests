package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Track {
    @CsvBindByName(column = "name")
    @JsonProperty("name")
    protected String name;
    @JsonProperty("uri")
    @CsvBindByName(column = "uri")
    protected String uri;

    @Override
    public String toString() {
        return "{" +
                "name='" + name +
                ", uri='" + uri +
                '}';
    }
}
