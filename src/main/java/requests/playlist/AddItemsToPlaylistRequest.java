package requests.playlist;

import dtos.playlist.request.AddItemsToPlaylistRequestDto;
import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import static io.restassured.RestAssured.given;

public class AddItemsToPlaylistRequest {
    public static Response addItems(String playlistId, String token, AddItemsToPlaylistRequestDto items){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(items)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    public static Response addItemsWithError(String playlistId, String token, AddItemsToPlaylistRequestDto items){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(items)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .extract()
                .response();
    }
}
