package tests.playlists;

import org.junit.jupiter.api.AfterEach;
import requests.users.UnfollowPlaylistRequest;

public abstract class BasePlaylistTests {

    protected static String token;
    protected static String tokenOfOtherUser;
    protected static String playlistId;



    @AfterEach
    public void afterEach(){
        if(playlistId != null){
            UnfollowPlaylistRequest.unfollowPlaylist(token, playlistId);
            playlistId = null;
        }
    }

}
