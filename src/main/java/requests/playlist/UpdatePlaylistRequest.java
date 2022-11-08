package requests.playlist;

import requests.SpecBuilder;
import urls.Endpoint;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class UpdatePlaylistRequest {
    public static UpdatePlaylistRequest updatePlaylist(String playlistId, String token, String name){
        HashMap bodyParams = new HashMap<String, String>();
        bodyParams.put("name", name);

        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(bodyParams)
                .when()
                .put(Endpoint.getPlaylist(playlistId))
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(UpdatePlaylistRequest.class);
    }
}
