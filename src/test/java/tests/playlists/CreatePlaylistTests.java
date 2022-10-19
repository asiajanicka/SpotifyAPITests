package tests.playlists;

import dtos.playlist.request.CreatePlaylistRequestDto;
import dtos.playlist.response.CreatePlaylistResponseDto;
import dtos.playlist.response.ReadPlaylistResponseDto;
import dtos.playlist.response.base.BasePlaylistResponseDto;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import requests.playlist.CreatePlaylistRequest;
import requests.playlist.ReadPlaylistRequest;
import requests.users.UnfollowPlaylistRequest;
import utils.SampleNames;
import utils.SpotifyProperties;
import utils.TokenManager;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePlaylistTests {

    private static String token;
    private static String playlistId;


    @BeforeAll
    public static void setUp(){
        token = TokenManager.getToken();
    }

    @AfterEach
    public void beforeEach(){
        if(playlistId != null){
            UnfollowPlaylistRequest.unfollowPlaylist(token, playlistId);
            playlistId = null;
        }
    }


    @Test
    @DisplayName("Create default playlist")
    public void createDefaultPlaylistTest(){
        String name = SampleNames.playlistName;

        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, name);
        playlistId = createPlaylistResponse.getId();
        // assert response from post new playlist request
        assertBasicPlaylistParams(createPlaylistResponse, name,SpotifyProperties.getUserId());

        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        // assert response from get a newly created playlist
        assertBasicPlaylistParams(readPlaylistResponse, name,SpotifyProperties.getUserId());
    }

    private void assertBasicPlaylistParams(BasePlaylistResponseDto playlistResponse,
                                           String expectedName,
                                           String expectedUserId){
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(playlistResponse.getName())
                .as("playlist name is different the expected")
                .isEqualTo(expectedName);
        soft.assertThat(playlistResponse.isCollaborative())
                .as("default playlist should not be collaborative")
                .isFalse();
        soft.assertThat(playlistResponse.isPublic())
                .as("default playlist should be public")
                .isTrue();
        soft.assertThat(playlistResponse.getDescription())
                .as("default playlist description should be blank")
                .isBlank();
        soft.assertThat(playlistResponse.getType())
                .as("default playlist type is 'playlist'")
                .isEqualTo("playlist");
        soft.assertThat(playlistResponse.getOwner().getId())
                .as("playlist's owner id is incorrect")
                .isEqualTo(expectedUserId);
        soft.assertAll();
    }

    private void assertErrorResponse(Response response, int expectedStatusCode, String expectMessage){
        JsonPath json = response.jsonPath();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(response.statusCode())
                .as("Status code is different then expected")
                .isEqualTo(expectedStatusCode);
        soft.assertThat(json.getString("error.status"))
                .as("Status code in json response is different then expected")
                .isEqualTo(String.valueOf(expectedStatusCode));
        soft.assertThat(json.getString("error.message"))
                .as("Response json doesn't contain info about incorrect name")
                .contains(expectMessage);
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => {0}")
    @CsvFileSource(files = "src/main/resources/SpotifyNameTestData.csv", numLinesToSkip = 1)
    @DisplayName("Create playlist with valid name")
    public void createPlaylistWithValidNameTest(String name){
        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, name);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.getName())
                .as("playlist name is different the expected")
                .isEqualTo(name);

        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.getName())
                .as("playlist name is different the expected")
                .isEqualTo(name);
    }

    @Test
    @DisplayName("Create playlist with long name")
    public void createPlaylistWithLongNameTest(){
        String name = RandomStringUtils.randomAlphabetic(140);
        createPlaylistWithValidNameTest(name);
    }

    @Test
    @DisplayName("Don’t create playlist with empty name")
    public void dontCreatePlaylistWithEmptyNameTest(){
        final Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, "");
        assertErrorResponse(response, 400, "name");
    }

    @Test
    @DisplayName("Don’t create playlist without name")
    public void dontCreatePlaylistWithoutNameTest(){
        HashMap<String, String> payload = new HashMap<>();
        final Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, payload);
        assertErrorResponse(response, 400, "name");
    }

    @Test
    @DisplayName("Create public playlist")
    public void createPublicPlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic(true);

        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
       playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should be public")
                .isTrue();

        // zmienic na get z innym uzytkownikiem
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isPublic())
                .as("playlist should be public")
                .isTrue();
    }

    @Test
    @DisplayName("Create private playlist")
    public void createPrivatePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic(false);

        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();

        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isPublic())
                .as("playlist should not be public")
                .isTrue();

        // dodac request z innym użytkownikiem
    }

    @Test
    @DisplayName("Don’t create playlist with invalid value for public")
    public void dontCreatePlaylistWithInvalidValueForPublicTest(){
        HashMap<String, String> payload = new HashMap<>();
        payload.put("name", SampleNames.playlistName);
        payload.put("public", "invalid");

        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, payload);
        assertErrorResponse(response, 400, "Error parsing JSON");
    }

    @ParameterizedTest(name = "{displayName} => {0}")
    @CsvFileSource(files = "src/main/resources/SpotifyNameTestData.csv", numLinesToSkip = 1)
    @DisplayName("Create playlist with valid description")
    public void createPlaylistWithDescriptionTest(String description){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setDescription(description);

        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.getDescription())
                .as("Description is different then expected")
                .isEqualTo(description);

        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.getDescription())
                .as("Description is different then expected")
                .isEqualTo(description);
    }

    @Test
    @DisplayName("Create playlist with long description")
    public void createPlaylistWithLongDescriptionTest(){
        String description = RandomStringUtils.randomAlphabetic(140);
        createPlaylistWithDescriptionTest(description);
    }

    @Test
    @DisplayName("Create private collaborative playlist")
    public void createCollaborativePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic(false);
        playlistRequest.setIsCollaborative(true);

        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();
        assertThat(createPlaylistResponse.isCollaborative())
                .as("Playlist should be collaborative")
                .isTrue();

        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isCollaborative())
                .as("Playlist should be collaborative")
                .isTrue();
    }

    @Test
    @DisplayName("Don't create public collaborative playlist")
    public void dontCreatePublicCollaborativePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic(true);
        playlistRequest.setIsCollaborative(true);

        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, playlistRequest);
        assertErrorResponse(response, 400, "Collaborative playlists can only be private");
    }

    @Test
    @DisplayName("Don’t create playlist with invalid value for collaborative")
    public void dontCreatePlaylistWithInvalidValueForCollaborativeTest(){
        HashMap<String, String> payload = new HashMap<>();
        payload.put("name", SampleNames.playlistName);
        payload.put("public", "false");
        payload.put("collaborative", "invalid");

        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, payload);
        assertErrorResponse(response, 400, "Error parsing JSON");
    }

}
