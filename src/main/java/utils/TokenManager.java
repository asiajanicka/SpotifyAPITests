package utils;

import io.restassured.response.Response;
import requests.token.RefreshTokenRequest;

import java.time.Instant;

public class TokenManager {

    private static String access_token;
    private static Instant expiryTime;

    public static synchronized String getToken(){
        try {
            if (access_token == null || Instant.now().isAfter(expiryTime)) {

                String clientId = SpotifyProperties.getClientId();
                String clientSecret = SpotifyProperties.getClientSecret();
                String refreshToken = SpotifyProperties.getRefreshToken();

                Response response = RefreshTokenRequest.refreshToken(clientId, clientSecret, refreshToken);
                access_token = response.path("access_token");
                int expiryDurationInSeconds = response.path("expires_in");
                expiryTime = Instant.now().plusSeconds(expiryDurationInSeconds - 300);
            }
        } catch(Exception e) {
            throw new RuntimeException("Failed to get the token");
        }
        return access_token;
    }
}
