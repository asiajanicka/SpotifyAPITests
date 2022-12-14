package dtos.playlist.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.playlist.response.base.BasePlaylistResponseDto;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReadPlaylistResponseDto extends BasePlaylistResponseDto {
    @JsonProperty("next")
    private String next;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("offset")
    private int offset;
}
