package urls;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Endpoint {
    public static final String API = "/api";
    public static final String TOKEN = "/token";
    public static final String USERS = "/users";
    public static final String PLAYLISTS = "/playlists";
    public static final String SEARCH = "/search";
    public static final String FOLLOWERS = "/followers";
    public static final String TRACKS = "/tracks";

    public static String getPlaylist(String playlistId){
        return PLAYLISTS + "/" + playlistId;
    }
    public static String getPlaylists(String userId){
        return USERS + "/" + userId + PLAYLISTS;
    }
    public static String getFollowers(String playlistId){
        return PLAYLISTS + "/" + playlistId + FOLLOWERS;
    }
    public static String getSearch(){
        return SEARCH;
    }
    public static String getTracks(String playlistId){
        return PLAYLISTS + "/" + playlistId + TRACKS;
    }
}
