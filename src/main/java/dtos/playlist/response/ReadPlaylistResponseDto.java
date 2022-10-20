package dtos.playlist.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dtos.playlist.response.base.BasePlaylistResponseDto;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReadPlaylistResponseDto extends BasePlaylistResponseDto {

}
