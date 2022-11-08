package requests.search;

import io.restassured.response.Response;
import requests.SpecBuilder;
import urls.Endpoint;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class SearchItemsRequest {
    public static Response readItems(String token, Map<String, String> params){
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
