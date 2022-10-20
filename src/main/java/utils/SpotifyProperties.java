package utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotifyProperties {
    private static String getProperties(String key){
        return ResourceBundle.getBundle("config").getString(key);
    }

    public static String getBaseUrl(){
        return getProperties("baseUrl");
    }
    public static String getAccountsUrl(){
        return getProperties("accountsUrl");
    }

    public static String getUserId(){
        return getProperties("userId");
    }
    public static String getClientId(){
        return getProperties("clientId");
    }
    public static String getClientSecret(){
        return getProperties("clientSecret");
    }
    public static String getRefreshToken(){
        return getProperties("refreshToken");
    }
    public static String getOtherUserId(){
        return getProperties("other.userId");
    }
    public static String getOtherClientId(){
        return getProperties("other.clientId");
    }
    public static String getOtherClientSecret(){
        return getProperties("other.clientSecret");
    }
    public static String getOtherRefreshToken(){
        return getProperties("other.refreshToken");
    }
}
