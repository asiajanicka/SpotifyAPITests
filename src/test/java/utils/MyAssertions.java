package utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class MyAssertions {
    public static void assertErrorResponse(Response response, int expectedStatusCode, String expectMessage){
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(response.statusCode())
                .as("Status code is different then expected")
                .isEqualTo(expectedStatusCode);

        JsonPath json = response.jsonPath();
        soft.assertThat(json.getString("error.status"))
                .as("Status code in json response is different then expected")
                .isEqualTo(String.valueOf(expectedStatusCode));
        soft.assertThat(json.getString("error.message"))
                .as("Response json contains incorrect message")
                .contains(expectMessage);
        soft.assertAll();
    }
}
