package requests.token;

import urls.Endpoint;
import io.restassured.response.Response;
import requests.SpecBuilder;

import static io.restassured.RestAssured.given;

public class RefreshTokenRequest {
    public static Response refreshToken(String clientId, String clientSecret, String refreshToken){
        return given(SpecBuilder.getTokenRequestSpec())
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .formParam("refresh_token", refreshToken)
                .formParam("grant_type", "refresh_token")
                .when()
                .post(Endpoint.API + Endpoint.TOKEN)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
