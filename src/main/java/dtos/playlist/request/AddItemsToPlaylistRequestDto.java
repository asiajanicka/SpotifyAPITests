package dtos.playlist.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddItemsToPlaylistRequestDto {
    @JsonProperty("position")
    private int position;
    @JsonProperty("uris")
    private List<String> uris;
}
