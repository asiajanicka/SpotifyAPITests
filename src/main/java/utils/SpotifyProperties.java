package utils;

import java.util.ResourceBundle;

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
    public static String getClientId(){
        return getProperties("clientId");
    }

    public static String getClientSecret(){
        return getProperties("clientSecret");
    }

    public static String getRefreshToken(){
        return getProperties("refreshToken");
    }

    public static String getUserId(){
        return getProperties("userId");
    }

}
