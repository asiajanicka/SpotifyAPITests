package requests.playlist;

import dtos.playlist.request.CreatePlaylistRequestDto;
import io.restassured.response.Response;
import urls.Endpoint;
import dtos.playlist.response.CreatePlaylistResponseDto;
import requests.SpecBuilder;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class CreatePlaylistRequest {

    public static CreatePlaylistResponseDto createPlaylist(String userId, String token, String name){
        HashMap bodyParams = new HashMap<String, String>();
        bodyParams.put("name", name);

        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(bodyParams)
                .when()
                .post(Endpoint.getPlaylists(userId))
                .then()
                .statusCode(201)
                .extract()
                .response()
                .as(CreatePlaylistResponseDto.class);
    }

    public static CreatePlaylistResponseDto createPlaylist(String userId, String token, HashMap<String, String> payload){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getPlaylists(userId))
                .then()
                .statusCode(201)
                .extract()
                .response()
                .as(CreatePlaylistResponseDto.class);
    }

    public static CreatePlaylistResponseDto createPlaylist(String userId, String token, CreatePlaylistRequestDto playlistDto){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .body(playlistDto)
                .post(Endpoint.getPlaylists(userId))
                .then()
                .statusCode(201)
                .extract()
                .response()
                .as(CreatePlaylistResponseDto.class);
    }

    public static Response createPlaylistWithError(String userId, String token, String name){
        HashMap bodyParams = new HashMap<String, String>();
        bodyParams.put("name", name);

        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(bodyParams)
                .when()
                .post(Endpoint.getPlaylists(userId))
                .then()
                .extract()
                .response();
    }

    public static Response createPlaylistWithError(String userId, String token, CreatePlaylistRequestDto playlistDto){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .body(playlistDto)
                .post(Endpoint.getPlaylists(userId))
                .then()
                .extract()
                .response();
    }

    public static Response createPlaylistWithError(String userId, String token, HashMap<String, String> payload){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(payload)
                .when()
                .post(Endpoint.getPlaylists(userId))
                .then()
                .extract()
                .response();
    }
}
