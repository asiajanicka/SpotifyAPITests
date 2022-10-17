package tests.playlists;

import loaders.UserDataReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.playlist.CreatePlaylist;
import utils.TokenManager;

public class CreatePlaylistTests {

    private static String token;
    private static String userId;

    @BeforeAll
    public static void setUp(){
        UserDataReader userdata = new UserDataReader();
        token = TokenManager.getToken(userdata);
        userId = userdata.getUserId();
    }



    @Test
    public void test(){

        CreatePlaylist.create(userId, token, "bla1");

    }
}
