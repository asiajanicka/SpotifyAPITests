package dtos.playlist.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddItemsToPlaylistRequestDto {
    @JsonProperty("position")
    private String position;
    @JsonProperty("uris")
    private ArrayList<String> uris;
}
