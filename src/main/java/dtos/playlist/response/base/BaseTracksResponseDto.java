package dtos.playlist.response.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseTracksResponseDto {
    @JsonProperty("items")
    private List<BaseItemsResponseDto> items;
    @JsonProperty("total")
    private int total;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("offset")
    private int offset;
}
