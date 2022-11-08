package tests.playlists;

import dtos.playlist.request.AddItemsToPlaylistRequestDto;
import dtos.playlist.request.CreatePlaylistRequestDto;
import dtos.playlist.response.CreatePlaylistResponseDto;
import dtos.playlist.response.ReadPlaylistResponseDto;
import dtos.playlist.response.base.BasePlaylistResponseDto;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import requests.playlist.AddItemsToPlaylistRequest;
import requests.playlist.CreatePlaylistRequest;
import requests.playlist.ReadPlaylistRequest;
import utils.MyAssertions;
import utils.SampleNames;
import utils.SpotifyProperties;
import utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreatePlaylistTests extends BasePlaylistTests  {

    @BeforeAll
    public void setUp(){
        token = TokenManager.getToken();
        tokenOfOtherUser = TokenManager.getOtherUserToken();
    }

    @Test
    @DisplayName("CP1 Create playlist with default params")
    public void CP1_createDefaultPlaylistTest(){
        String name = SampleNames.playlistName;
        // Step - create playlist with default params
        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, name);
        playlistId = createPlaylistResponse.getId();
        // assert response from post new playlist request
        assertBasicPlaylistParams(createPlaylistResponse, name,SpotifyProperties.getUserId());

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        // assert response from get a newly created playlist
        assertBasicPlaylistParams(readPlaylistResponse, name,SpotifyProperties.getUserId());

        // Step - the other user reads playlist as it's public by default (based on status code 200)
        ReadPlaylistRequest.readPlaylist(tokenOfOtherUser, playlistId);

        // Step - the other user can not add track to playlist as it's not collaborative by default
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, tokenOfOtherUser, List.of(SampleNames.trackUri));
        MyAssertions.assertErrorResponse(response, 403, "You cannot add tracks");
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

    @ParameterizedTest(name = "{displayName} => {0}")
    @CsvFileSource(files = "src/main/resources/SpotifyNameTestData.csv", numLinesToSkip = 1)
    @DisplayName("CP2 Create playlist with valid name")
    public void CP2_createPlaylistWithValidNameTest(String name){
        // Step -  create playlist
        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, name);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.getName())
                .as("playlist name is different the expected")
                .isEqualTo(name);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.getName())
                .as("playlist name is different the expected")
                .isEqualTo(name);
    }

    @Test
    @DisplayName("CP3 Create playlist with long name")
    public void CP3_createPlaylistWithLongNameTest(){
        String name = RandomStringUtils.randomAlphabetic(140);
        CP2_createPlaylistWithValidNameTest(name);
    }

    @Test
    @DisplayName("CP4 Don’t create playlist with empty name")
    public void CP4_dontCreatePlaylistWithEmptyNameTest(){
        final Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, "");
        MyAssertions.assertErrorResponse(response, 400, "Missing required field: name");
    }

    @Test
    @DisplayName("CP5 Don’t create playlist without name param")
    public void CP5_dontCreatePlaylistWithoutNameTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        final Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, playlistRequest);
        MyAssertions.assertErrorResponse(response, 400, "Missing required field: name");
    }

    @Test
    @DisplayName("CP6 Create public playlist")
    public void CP6_createPublicPlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("true");

        // Step - create public playlist
        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should be public")
                .isTrue();

        // Step - read public playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isPublic())
                .as("playlist should be public")
                .isTrue();

        // Step - the other user reads playlist (based on status code 200)
        ReadPlaylistRequest.readPlaylist(tokenOfOtherUser, playlistId);
    }

    @Test
    @DisplayName("CP7 Create private playlist")
    public void CP7_createPrivatePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("false");

        // Step - create private playlist
        final CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();

        // Step - read private playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isPublic())
                .as("playlist should not be public")
                .isFalse();

        // Step - the other user shouldn't be able to read private list
        Response response = ReadPlaylistRequest.readPlaylistWithError(tokenOfOtherUser, playlistId);
        MyAssertions.assertErrorResponse(response, 400, "No access");
    }

    @Test
    @DisplayName("CP8 Don’t create playlist with invalid value for public")
    public void CP8_dontCreatePlaylistWithInvalidValueForPublicTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("invalid");

        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, playlistRequest);
        MyAssertions.assertErrorResponse(response, 400, "Error parsing JSON");
    }

    @Test
    @DisplayName("CP9 Create private playlist if public param set to empty")
    public void CP9_createPublicPlaylistIfPublicParamSetToEmptyTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("");

        // Step - create playlist should be public
        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();

        // Step - read playlist (it should be public)
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();
    }

    @ParameterizedTest(name = "{displayName} => {0}")
    @CsvFileSource(files = "src/main/resources/SpotifyNameTestData.csv", numLinesToSkip = 1)
    @DisplayName("CP10 Create playlist with valid description")
    public void CP10_createPlaylistWithDescriptionTest(String description){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setDescription(description);

        // Step - create list with given description
        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.getDescription())
                .as("Description is different then expected")
                .isEqualTo(description);

        // Step - read list with given description
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.getDescription())
                .as("Description is different then expected")
                .isEqualTo(description);
    }

    @Test
    @DisplayName("CP11 Create playlist with long description")
    public void CP11_createPlaylistWithLongDescriptionTest(){
        String description = RandomStringUtils.randomAlphabetic(140);
        CP10_createPlaylistWithDescriptionTest(description);
    }

    @Test
    @DisplayName("CP12 Create private collaborative playlist")
    public void CP12_createCollaborativePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("false");
        playlistRequest.setIsCollaborative("true");

        // Step - create private and collaborative playlist
        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();
        assertThat(createPlaylistResponse.isCollaborative())
                .as("Playlist should be collaborative")
                .isTrue();

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isCollaborative())
                .as("Playlist should be collaborative")
                .isTrue();

        // Step - the other user adds tack to collaborative playlist
        AddItemsToPlaylistRequest.addItems(playlistId, tokenOfOtherUser, List.of(SampleNames.trackUri));
    }

    @Test
    @DisplayName("CP13 Create private no-collaborative playlist")
    public void CP13_createNoCollaborativePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("false");
        playlistRequest.setIsCollaborative("false");

        // Step - create private and no collaborative playlist
        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isPublic())
                .as("Playlist should not be public")
                .isFalse();
        assertThat(createPlaylistResponse.isCollaborative())
                .as("Playlist should not be collaborative")
                .isFalse();

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isCollaborative())
                .as("Playlist should not be collaborative")
                .isFalse();

        // Step - the other user should not be able to add track to no-collaborative playlist
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, tokenOfOtherUser, List.of("spotify:track:7Lf7oSEVdzZqTA0kEDSlS5"));
        MyAssertions.assertErrorResponse(response, 403, "You cannot add tracks to a playlist you don't own");
    }

    @Test
    @DisplayName("CP14 Create no-collaborative playlist if collaborative param set to empty")
    public void CP14_createNoCollaborativePlaylistIfCollaborativeParamSetToEmptyTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsCollaborative("");

        // Step - create no collaborative playlist when collaborative set to empty string
        CreatePlaylistResponseDto createPlaylistResponse = CreatePlaylistRequest
                .createPlaylist(SpotifyProperties.getUserId(), token, playlistRequest);
        playlistId = createPlaylistResponse.getId();
        assertThat(createPlaylistResponse.isCollaborative())
                .as("Playlist should not be collaborative")
                .isFalse();

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        assertThat(readPlaylistResponse.isCollaborative())
                .as("Playlist should not be collaborative")
                .isFalse();
    }

    @Test
    @DisplayName("CP15 Don't create public collaborative playlist")
    public void CP15_dontCreatePublicCollaborativePlaylistTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("true");
        playlistRequest.setIsCollaborative("true");

        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, playlistRequest);
        MyAssertions.assertErrorResponse(response, 400, "Collaborative playlists can only be private");
    }

    @Test
    @DisplayName("CP16 Don’t create playlist with invalid value for collaborative")
    public void CP16_dontCreatePlaylistWithInvalidValueForCollaborativeTest(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        playlistRequest.setIsPublic("false");
        playlistRequest.setIsCollaborative("invalid");

        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), token, playlistRequest);
        MyAssertions.assertErrorResponse(response, 400, "Error parsing JSON");
    }

    @Test
    @DisplayName("CP17 Don’t create playlist with invalid user id")
    public void CP17_dontCreatePlaylistWithInvalidUserId(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        Response response = CreatePlaylistRequest
                .createPlaylistWithError("invalid", token, playlistRequest);
        MyAssertions.assertErrorResponse(response, 403, "You cannot create a playlist for another user");
    }

    @Test
    @DisplayName("CP18 Don’t create playlist with empty user id")
    public void CP18_dontCreatePlaylistWithEmptyUserId(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        Response response = CreatePlaylistRequest
                .createPlaylistWithError("", token, playlistRequest);
        MyAssertions.assertErrorResponse(response, 404, "Service not found");
    }

    @Test
    @DisplayName("CP19 Don’t create playlist with empty token")
    public void CP19_dontCreatePlaylistWithEmptyToken(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), "", playlistRequest);
        MyAssertions.assertErrorResponse(response, 400, "Only valid bearer authentication supported");
    }

    @Test
    @DisplayName("CP20 Don’t create playlist with invalid token")
    public void CP20_dontCreatePlaylistWithInvalidToken(){
        CreatePlaylistRequestDto playlistRequest = new CreatePlaylistRequestDto();
        playlistRequest.setName(SampleNames.playlistName);
        Response response = CreatePlaylistRequest
                .createPlaylistWithError(SpotifyProperties.getUserId(), "invalid", playlistRequest);
        MyAssertions.assertErrorResponse(response, 401, "Invalid access token");
    }

}
