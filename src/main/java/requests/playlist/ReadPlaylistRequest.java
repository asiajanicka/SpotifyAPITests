package requests.playlist;

import dtos.playlist.response.ReadPlaylistResponseDto;
import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import static io.restassured.RestAssured.given;

public class ReadPlaylistRequest {
    public static ReadPlaylistResponseDto readPlaylist(String token, String playlistId){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(Endpoint.getPlaylist(playlistId))
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(ReadPlaylistResponseDto.class);
    }

    public static Response readPlaylistWithError(String token, String playlistId){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(Endpoint.getPlaylist(playlistId))
                .then()
                .extract()
                .response();
    }
}
