package dtos.playlist.response.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class BasePlaylistResponseDto {
    @JsonProperty("id")
    protected String id;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("public")
    protected boolean isPublic;
    @JsonProperty("collaborative")
    protected boolean isCollaborative;
    @JsonProperty("description")
    protected String description;
    @JsonProperty("type")
    protected String type;
    @JsonProperty("owner")
    protected BaseOwnerResponseDto owner;
}
