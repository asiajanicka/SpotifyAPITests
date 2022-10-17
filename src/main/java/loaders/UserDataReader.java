package loaders;

import lombok.Getter;

@Getter
public class UserDataReader extends DataReader{

    private String clientId ;
    private String clientSecret;
    private String refreshToken;
    private String userId;

    public UserDataReader() {
        super("config.properties");
    }

    @Override
    void loadData() {
        clientId = properties.getProperty("clientId");
        clientSecret = properties.getProperty("clientSecret");
        refreshToken = properties.getProperty("refreshToken");
        userId = properties.getProperty("userId");
    }
}
