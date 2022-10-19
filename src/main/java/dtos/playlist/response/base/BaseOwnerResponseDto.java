package dtos.playlist.response.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseOwnerResponseDto {
    @JsonProperty("display_name")
    protected String displayName;
    @JsonProperty("id")
    protected String id;
}
