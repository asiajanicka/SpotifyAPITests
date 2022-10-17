package requests.playlist;

import endpoints.Routes;
import io.restassured.response.Response;
import requests.SpecBuilder;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class CreatePlaylist {

    public static Response create(String userId, String token, String name){

        HashMap requestParams = new HashMap<String, String>();
        requestParams.put("name", name);

        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .body(requestParams)
                .when()
                .post(Routes.USERS + "/" +userId + Routes.PLAYLISTS)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();
    }

    public static Response create(String userId, HashMap<String, String> data, String token){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .pathParam("user_id", userId)
                .when()
                .body(data)
                .post(Routes.USERS + "/"+userId + Routes.PLAYLISTS)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();
    }
}
