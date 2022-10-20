package utils;

import io.restassured.response.Response;
import requests.token.RefreshTokenRequest;

import java.time.Instant;

public class TokenManager {

    private static String userAccessToken;
    private static Instant userExpiryTime;
    private static String otherUserAccessToken;
    private static Instant otherUserExpiryTime;

    public static synchronized String getToken(){
        try {
            if (userAccessToken == null || Instant.now().isAfter(userExpiryTime)) {

                String clientId = SpotifyProperties.getClientId();
                String clientSecret = SpotifyProperties.getClientSecret();
                String refreshToken = SpotifyProperties.getRefreshToken();

                Response response = RefreshTokenRequest.refreshToken(clientId, clientSecret, refreshToken);
                userAccessToken = response.path("access_token");
                int expiryDurationInSeconds = response.path("expires_in");
                userExpiryTime = Instant.now().plusSeconds(expiryDurationInSeconds - 300);
            }
        } catch(Exception e) {
            throw new RuntimeException("Failed to get the token");
        }
        return userAccessToken;
    }

    public static synchronized String getOtherUserToken(){
        try {
            if (otherUserAccessToken == null || Instant.now().isAfter(otherUserExpiryTime)) {

                String clientId = SpotifyProperties.getOtherClientId();
                String clientSecret = SpotifyProperties.getOtherClientSecret();
                String refreshToken = SpotifyProperties.getOtherRefreshToken();

                Response response = RefreshTokenRequest.refreshToken(clientId, clientSecret, refreshToken);
                otherUserAccessToken = response.path("access_token");
                int expiryDurationInSeconds = response.path("expires_in");
                otherUserExpiryTime = Instant.now().plusSeconds(expiryDurationInSeconds - 300);
            }
        } catch(Exception e) {
            throw new RuntimeException("Failed to get the token");
        }
        return otherUserAccessToken;
    }
}
