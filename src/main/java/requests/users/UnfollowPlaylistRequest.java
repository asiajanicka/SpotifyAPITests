package requests.users;

import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import static io.restassured.RestAssured.given;

public class UnfollowPlaylistRequest {

    public static Response unfollowPlaylist(String token, String playlistId){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .delete(Endpoint.getFollowers(playlistId))
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
