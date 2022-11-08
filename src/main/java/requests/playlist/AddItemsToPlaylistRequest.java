package requests.playlist;

import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class AddItemsToPlaylistRequest {

    public static Response addItems(String playlistId, String token, List<String> uris){
        HashMap<String, List<String >> payload = new HashMap<>();
        payload.put("uris", uris);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    public static Response addItems(String playlistId, String token, List<String> uris, int position){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("uris", uris);
        payload.put("position", position);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    public static Response addItemsWithError(String playlistId, String token, List<String> uris){
        HashMap<String, List<String >> payload = new HashMap<>();
        payload.put("uris", uris);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .extract()
                .response();
    }

    public static Response addItemsWithError(String playlistId, String token){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .extract()
                .response();
    }

    public static Response addItemsWithError(String playlistId, String token, List<String> uris, int position){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("uris", uris);
        payload.put("position", position);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .extract()
                .response();
    }

    public static Response addItemsWithError(String playlistId, String token, List<String> uris, String position){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("uris", uris);
        payload.put("position", position);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getTracks(playlistId))
                .then()
                .extract()
                .response();
    }
}
