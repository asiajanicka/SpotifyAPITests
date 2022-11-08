package requests.playlist;

import dtos.playlist.response.ReadPlaylistItemsResponseDto;
import requests.SpecBuilder;
import urls.Endpoint;

import static io.restassured.RestAssured.given;

public class ReadPlaylistItemsRequest {
    public static ReadPlaylistItemsResponseDto readItems(String token, String playlistId){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(Endpoint.getTracks(playlistId))
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(ReadPlaylistItemsResponseDto.class);
    }

    public static ReadPlaylistItemsResponseDto readItems(String token, String playlistId, int offset){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .param("offset", offset)
                .when()
                .get(Endpoint.getTracks(playlistId))
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(ReadPlaylistItemsResponseDto.class);
    }
}
