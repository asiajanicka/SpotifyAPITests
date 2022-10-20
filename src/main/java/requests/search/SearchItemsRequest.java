package requests.search;

import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class SearchItemsRequest {
    public static Response readItems(String token, HashMap<String, String> params){
        return given(SpecBuilder.getRequestSpec())
                .auth().oauth2(token)
                .params(params)
                .when()
                .get(Endpoint.getSearch())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
