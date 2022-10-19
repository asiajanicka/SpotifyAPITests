package requests;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import urls.Endpoint;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.SpotifyProperties;

public class SpecBuilder {

    public static RequestSpecification getRequestSpec(){

        return new RequestSpecBuilder()
                .setBaseUri(SpotifyProperties.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public static RequestSpecification getTokenRequestSpec(){
        return new RequestSpecBuilder()
                .setBaseUri(SpotifyProperties.getAccountsUrl())
                .setContentType(ContentType.URLENC)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }


}
