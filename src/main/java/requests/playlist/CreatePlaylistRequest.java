package requests.playlist;

import dtos.playlist.request.CreatePlaylistRequestDto;
import dtos.playlist.response.CreatePlaylistResponseDto;
import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import static io.restassured.RestAssured.given;

public class CreatePlaylistRequest {
    public static CreatePlaylistResponseDto createPlaylist(String userId, String token, String name){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(name);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(playlistRequest)
                .when()
                .post(Endpoint.getPlaylists(userId))
                .then()
                .statusCode(201)
                .extract()
                .response()
                .as(CreatePlaylistResponseDto.class);
    }

    public static String getPlaylistId(String userId, String token, String name){
        return createPlaylist(userId, token, name).getId();
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

    public static String getPlaylistId(String userId, String token, CreatePlaylistRequestDto playlistDto){
        return createPlaylist(userId, token, playlistDto).getId();
    }

    public static Response createPlaylistWithError(String userId, String token, String name){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(name);
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(playlistRequest)
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
}
