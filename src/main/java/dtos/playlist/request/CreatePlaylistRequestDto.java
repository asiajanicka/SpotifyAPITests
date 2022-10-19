package dtos.playlist.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePlaylistRequestDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("public")
    private Boolean isPublic;
    @JsonProperty("collaborative")
    private Boolean isCollaborative;
    @JsonProperty("description")
    private String description;
}
