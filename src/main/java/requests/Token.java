package requests;

import endpoints.Routes;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Token {
    public static Response refreshToken(String clientId, String clientSecret, String refreshToken){
        return given(SpecBuilder.getTokenRequestSpec())
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .formParam("refresh_token", refreshToken)
                .formParam("grant_type", "refresh_token")
                .when()
                .post(Routes.API + Routes.TOKEN)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .statusCode(200)
                .extract()
                .response();
    }
}
