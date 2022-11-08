package tests.playlists;

import dtos.playlist.response.ReadPlaylistItemsResponseDto;
import dtos.playlist.response.ReadPlaylistResponseDto;
import dtos.playlist.response.base.BaseItemsResponseDto;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import requests.playlist.AddItemsToPlaylistRequest;
import requests.playlist.CreatePlaylistRequest;
import requests.playlist.ReadPlaylistItemsRequest;
import requests.playlist.ReadPlaylistRequest;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AddItemsToPlaylistTests extends BasePlaylistTests {

    private static BaseTrackTestDataFromCSVReader testDataTracks;

    @BeforeAll
    public static void setUp() {
        token = TokenManager.getToken();
        testDataTracks = new BaseTrackTestDataFromCSVReader();
    }

    private List<Track> getTracksFromReadPlaylistItemsResponse(ReadPlaylistItemsResponseDto response) {
        return response
                .getItems()
                .stream()
                .map(BaseItemsResponseDto::getTrack)
                .map(p -> (Track) p)
                .collect(Collectors.toList());
    }

    private List<Track> getTracksFromReadPlaylistResponse(ReadPlaylistResponseDto response) {
        return response
                .getTracks()
                .getItems()
                .stream()
                .map(BaseItemsResponseDto::getTrack)
                .map(p -> (Track) p)
                .collect(Collectors.toList());
    }

    private List<String> getTestDataTracksUris(int beginning, int end) {
        return testDataTracks.getTracks()
                .stream()
                .map(p -> p.getUri())
                .collect(Collectors.toList())
                .subList(beginning, end);
    }

    @Test
    @DisplayName("ATTP1 Add single track to empty playlist")
    public void ATTP1_addSingleTrackToEmptyPlaylistTest() {
        int expectedNumberOfTracks = 1;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - add track to playlist
        Track track = testDataTracks.getTracks().get(0);
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, List.of(track.getUri()));

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Track list doesn't contain the track %s", track.toString())
                .containsExactlyElementsOf(Arrays.asList(track));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP2 Add single track to empty playlist at position 0")
    public void ATTP2_addSingleTrackToEmptyPlaylistWithPosition0Test() {
        int expectedNumberOfTracks = 1;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - add track to playlist
        Track track = testDataTracks.getTracks().get(0);
        int position = 0;
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, List.of(track.getUri()), position);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Playlist doesn't contain the track %s", track.toString())
                .containsExactlyElementsOf(Arrays.asList(track));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP3 Don't add track to empty playlist at position other than 0")
    public void ATTP3_dontAddTrackToEmptyPlaylistWithPositionOtherThen0Test() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add track to playlist
        Track track = testDataTracks.getTracks().get(0);
        int position = 5;
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, List.of(track.getUri()), position);

        MyAssertions.assertErrorResponse(response, 403, "Index out of bounds");
    }

    @Test
    @DisplayName("ATTP4 Add single track to populated playlist")
    public void ATTP4_addSingleTrackToPlaylistTest() {
        int expectedNumberOfTracks = 6;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add 5 tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, expectedNumberOfTracks - 1));

        // Step - add track to playlist
        Track track = testDataTracks.getTracks().get(expectedNumberOfTracks - 1);
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, List.of(track.getUri()));

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(0, expectedNumberOfTracks));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP5 Add single track to the beginning of populated playlist")
    public void ATTP5_addTrackToBeginningOfPopulatedPlaylistTest() {
        int expectedNumberOfTracks = 6;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add 5 tracks to playlist to make it possible to add additional track to the beginning of playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, expectedNumberOfTracks - 1));

        // Step - add track to the beginning of playlist
        Track track = testDataTracks.getTracks().get(expectedNumberOfTracks - 1);
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, List.of(track.getUri()), 0);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.size())
                .as("Size of tracks list is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.get(0))
                .as("Playlist doesn't start with given track", track.getName(), track.getUri())
                .isEqualTo(track);
        soft.assertThat(trackListFromResponse.subList(1, expectedNumberOfTracks))
                .as("Tracks weren't shifted right by one in order")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(0, expectedNumberOfTracks - 1));
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => position = {0}")
    @ValueSource(ints = {5, 35, 99})
    @DisplayName("ATTP6 Add single track to populated playlist at position lower than limit")
    public void ATTP6_addSingleTrackToPlaylistAtPositionLoweThanLimitTest(int position) {
        int expectedNumberOfTracks = 101;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, expectedNumberOfTracks - 1));

        // Step - add track to playlist at given position
        Track track = testDataTracks.getTracks().get(expectedNumberOfTracks - 1);
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, List.of(track.getUri()), position);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.size())
                .as("Size of tracks list is different than 100")
                .isEqualTo(100);
        soft.assertThat(trackListFromResponse.get(position))
                .as("Track on position %d is different than expected", position)
                .isEqualTo(track);
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => position = {0}")
    @ValueSource(ints = {100, 102})
    @DisplayName("ATTP7 Add single track to populated playlist at position greater or equal to limit")
    public void ATTP7_addSingleTrackToPlaylistAtPositionHigherThanLimitTest(int position) {
        int expectedNumberOfTracks = 104;
        int limit = 100;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, 100));
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(100, expectedNumberOfTracks - 1));

        // Step - add track to playlist at given position
        Track track = testDataTracks.getTracks().get(expectedNumberOfTracks - 1);
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, List.of(track.getUri()), position);

        // Step - read playlist
        ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest
                .readItems(token, playlistId, limit);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistItemsResponse.getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.size())
                .as("Size of second page of tracks list is different than expected")
                .isEqualTo(expectedNumberOfTracks - limit);
        soft.assertThat(trackListFromResponse.get(position - limit))
                .as("Track on position %d is different than expected", position - limit)
                .isEqualTo(track);
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP8 Don’t add track at position higher than playlist size")
    public void ATTP8_dontAddTrackAtPositionHigherThanPlaylistSizeTest() {
        int numberOfPrerequisiteTracks = 5;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add 5 tracks to playlist to make it possible to add additional tracks in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, numberOfPrerequisiteTracks));

        // Step - try to add a track at position higher than playlist size
        Track track = testDataTracks.getTracks().get(7);
        int position = 8;
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token,
                        List.of(track.getUri()),
                        position);

        MyAssertions.assertErrorResponse(response, 403, "Index out of bounds");
    }

    @Test
    @DisplayName("ATTP9 Add tracks separately to empty playlist")
    public void ATTP9_addTracksSeparatelyToEmptyPlaylistTest() {
        int expectedNumberOfTracks = 3;
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - add three track to playlist separately
        for (int i = 0; i < 3; i++) {
            AddItemsToPlaylistRequest
                    .addItems(playlistId, token, Arrays.asList(testDataTracks.getTracks().get(i).getUri()));
        }

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(0, expectedNumberOfTracks));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP10 Add the same track twice in separated request to empty playlist")
    public void ATTP10_addSameTrackTwiceToEmptyPlaylistTest() {
        int expectedNumberOfTracks = 2;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - add the same track twice to playlist
        Track track = testDataTracks.getTracks().get(0);
        for (int i = 0; i < expectedNumberOfTracks; i++) {
            AddItemsToPlaylistRequest
                    .addItems(playlistId, token, Arrays.asList(track.getUri()));
        }

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.size())
                .as("Size of tracks list is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Track list doesn't contain the track %s twice", track.toString())
                .allSatisfy(a -> assertThat(a).isEqualTo(track));
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => number of tracks: {0}")
    @ValueSource(ints = {3, 100})
    @DisplayName("ATTP11 Add tracks in one request to empty playlist")
    public void ATTP11_addTracksInRequestToEmptyPlaylistTest(int expectedNumberOfTracks) {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - add given number of tracks to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, expectedNumberOfTracks));

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Playlist doesn't contain tracks in order")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(0, expectedNumberOfTracks));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP12_Add the same track twice in one request to empty playlist")
    public void ATTP12_addSameTrackTwiceInOneRequestToEmptyPlaylistTest() {
        int expectedNumberOfTracks = 2;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - add the same track twice to playlist
        Track track = testDataTracks.getTracks().get(0);
        List<String> uris = new ArrayList<>();
        for (int i = 0; i < expectedNumberOfTracks; i++) {
            uris.add(track.getUri());
        }
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, uris);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.size())
                .as("Size of tracks list is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse)
                .as("Track list doesn't contain the track %s twice", track.toString())
                .allSatisfy(a -> assertThat(a).isEqualTo(track));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP13 Add the same track twice in one request to populated playlist at given position")
    public void ATTP13_addSameTrackTwiceToPopulatedPlaylistAtGivenPosition() {
        int numberOfPrerequisiteTracks = 30;
        int expectedNumberOfTracks = numberOfPrerequisiteTracks + 2;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, numberOfPrerequisiteTracks));

        // Step - add track to playlist at given position
        Track track = testDataTracks.getTracks().get(numberOfPrerequisiteTracks);
        int position = 25;
        List<String> uris = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            uris.add(track.getUri());
        }
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, uris, position);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.subList(position, position + 2))
                .as("Track list doesn't contain the track %s twice", track.toString())
                .allSatisfy(a -> assertThat(a).isEqualTo(track));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP14 Don't add more than 100 tracks in one request to playlist")
    public void ATTP14_dontAddMoreThen100TracksToEmptyPlaylistTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add more than 100 tracks in one request to playlist
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, getTestDataTracksUris(0, 101));

        MyAssertions.assertErrorResponse(response, 400,
                "You can add a maximum of 100 tracks per request");
    }

    @ParameterizedTest(name = "{displayName} => tracks to add: {0}, position: {1}")
    @CsvSource({
            "6, 10",
            "3, 97"
    })
    @DisplayName("ATTP15 Add tracks to populated playlist at current page")
    public void ATTP15_addTracksToPopulatedPlaylistAtCurrentPageTest(int numberOfTrackToAdd, int position) {
        int numberOfPrerequisiteTracks = 100;
        int expectedNumberOfTracks = numberOfPrerequisiteTracks + numberOfTrackToAdd;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional tracks to the end
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, numberOfPrerequisiteTracks));

        // Step - add tracks to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(numberOfPrerequisiteTracks, expectedNumberOfTracks), position);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse.subList(position, position + numberOfTrackToAdd))
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(numberOfPrerequisiteTracks, expectedNumberOfTracks));
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => tracks to add: {0}, position: {1}")
    @CsvSource({
            "6, 97",
            "12, 98"
    })
    @DisplayName("ATTP16 Add tracks to populated playlist partially shifted to next page")
    public void ATTP16_addTracksToPopulatedPlaylistPartiallyShiftedTest(int numberOfTrackToAdd, int position) {
        int numberOfPrerequisiteTracks = 100;
        int expectedNumberOfTracks = numberOfPrerequisiteTracks + numberOfTrackToAdd;
        int limit = 100;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional tracks to the end
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, numberOfPrerequisiteTracks));

        // Step - add tracks to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(numberOfPrerequisiteTracks, expectedNumberOfTracks), position);

        // Step - read playlist - first page of tracks
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse_page1 = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        // Step -  read playlist items to get tracks over number 100 (next page of tracks)
        ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest.readItems(token, playlistId, limit);
        List<Track> trackListFromResponse_page2 = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);

        SoftAssertions soft = new SoftAssertions();
        int amountOfTracksAddedToPage1 = 100 - position;
        int amountOfTracksAddedToPage2 = numberOfTrackToAdd - amountOfTracksAddedToPage1;
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse_page1.subList(position, 100))
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks()
                        .subList(numberOfPrerequisiteTracks, numberOfPrerequisiteTracks + amountOfTracksAddedToPage1));
        soft.assertThat(readPlaylistItemsResponse.getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse_page2.subList(0, amountOfTracksAddedToPage2))
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks()
                        .subList(numberOfPrerequisiteTracks + amountOfTracksAddedToPage1, numberOfPrerequisiteTracks + numberOfTrackToAdd));
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => tracks to add: {0}, position: {1}")
    @CsvSource({
            "6, 100",
            "12, 101"
    })
    @DisplayName("ATTP17 Add tracks to populated playlist at next page")
    public void ATTP17_addTracksToPopulatedPlaylistAtNextPageTest(int numberOfTrackToAdd, int position) {
        int numberOfPrerequisiteTracks = 103;
        int expectedNumberOfTracks = numberOfPrerequisiteTracks + numberOfTrackToAdd;
        int limit = 100;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional tracks to the end
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, limit));
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(limit, numberOfPrerequisiteTracks));

        // Step - add tracks to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(numberOfPrerequisiteTracks, expectedNumberOfTracks), position);

        // Step -  read playlist items to get tracks over number 100
        ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest.readItems(token, playlistId, limit);
        List<Track> trackListFromResponse_page2 = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(readPlaylistItemsResponse.getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse_page2.subList(position - limit, position - limit + numberOfTrackToAdd))
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks()
                        .subList(numberOfPrerequisiteTracks, numberOfPrerequisiteTracks + numberOfTrackToAdd));
        soft.assertAll();
    }

    @Test
    @DisplayName("ATTP18 Add max number of tracks per request to populated playlist at given position")
    public void ATTP18_addMaxTracksPerRequestToPopulatedPlaylistAtGivenPositionTest() {
        int numberOfPrerequisiteTracks = 4;
        int numberOfTrackToAdd = 100;
        int position = 2;
        int expectedNumberOfTracks = numberOfPrerequisiteTracks + numberOfTrackToAdd;
        int limit = 100;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional tracks to the end
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, numberOfPrerequisiteTracks));

        // Step - add tracks to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(numberOfPrerequisiteTracks, expectedNumberOfTracks), position);

        // Step - read playlist
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse_page1 = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        // Step -  read playlist items to get tracks over number 100
        ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest.readItems(token, playlistId, limit);
        List<Track> trackListFromResponse_page2 = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);

        SoftAssertions soft = new SoftAssertions();
        int amountOfTracksAddedToPage1 = 100 - position;
        int amountOfTracksAddedToPage2 = numberOfTrackToAdd - amountOfTracksAddedToPage1;
        soft.assertThat(readPlaylistResponse.getTracks().getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse_page1.subList(position, 100))
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks()
                        .subList(numberOfPrerequisiteTracks, numberOfPrerequisiteTracks + amountOfTracksAddedToPage1));
        soft.assertThat(readPlaylistItemsResponse.getTotal())
                .as("Total field is different than expected number of tracks")
                .isEqualTo(expectedNumberOfTracks);
        soft.assertThat(trackListFromResponse_page2.subList(0, amountOfTracksAddedToPage2))
                .as("Playlist is missing tracks or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks()
                        .subList(numberOfPrerequisiteTracks + amountOfTracksAddedToPage1, numberOfPrerequisiteTracks + numberOfTrackToAdd));
        soft.assertAll();
    }

    @ParameterizedTest(name = "{displayName} => number of tracks added: {0}")
    @ValueSource(ints = {51, 100})
    @DisplayName("ATTP19 Add tracks to the beginning of playlist")
    public void ATTP19_addTracksToBeginningOfPlaylistTest(int numberOfTrackToAdd) {
        int numberOfPrerequisiteTracks = 4;
        int expectedNumberOfTracks = numberOfPrerequisiteTracks + numberOfTrackToAdd;

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, numberOfPrerequisiteTracks));

        // Step - add tracks to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(numberOfPrerequisiteTracks, expectedNumberOfTracks), 0);

        // Step - read playlist - first 100 tracks
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);
        assertThat(trackListFromResponse.subList(0, numberOfTrackToAdd))
                .as("Tracks weren't added to playlist or they are misplaced")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(numberOfPrerequisiteTracks, expectedNumberOfTracks));
    }

    @ParameterizedTest(name = "{displayName} => prerequisite tracks/added tracks/ position: {0}/{1}/{2}")
    @CsvSource({
            "50, 3, 40",
            "93, 6 ,12"
    })
    @DisplayName("ATTP20 Shift tracks in current page when tracks added to playlist")
    public void ATTP20_shiftTracksInCurrentPageWhenTracksAddedToPlaylistTest(int prerequisite, int added, int position) {
        int limit = 100;

        // test data should fulfill following condition (position < limit - added && prerequisite + added <= limit)

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);
        // Prerequisite - add  tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, prerequisite));

        // Step - add track to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(prerequisite, prerequisite + added), position);
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);

        assertThat(trackListFromResponse.subList(position + added, trackListFromResponse.size()))
                .as("Tracks after given position weren't shifted correctly")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(position, prerequisite));
    }

    @ParameterizedTest(name = "{displayName} => prerequisite tracks/added tracks/ position: {0}/{1}/{2}")
    @CsvSource({
            "108, 8, 90",
            "105, 3, 40"
    })
    @DisplayName("ATTP21 Shift tracks partially from current to next page when tracks added to playlist")
    public void ATTP21_shiftTracksPartiallyWhenTracksAddedToPlaylistTest(int prerequisite, int added, int position) {
        int limit = 100;

        // test data must fulfill following condition (position < limit - added && prerequisite + added > limit)

        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);
        // Prerequisite - add  tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, limit));
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(limit, prerequisite));

        // Step - add track to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(prerequisite, prerequisite + added), position);
        ReadPlaylistResponseDto readPlaylistResponse = ReadPlaylistRequest
                .readPlaylist(token, playlistId);
        List<Track> trackListFromResponse = getTracksFromReadPlaylistResponse(readPlaylistResponse);
        assertThat(trackListFromResponse.subList(position + added, trackListFromResponse.size()))
                .as("Tracks on first page of playlist (<limit) weren't shifted correctly")
                .containsExactlyElementsOf(testDataTracks.getTracks().subList(position, limit - added));

        ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest.readItems(token, playlistId, 100);
        List<Track> trackListFromItemsResponse = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);
        assertThat(trackListFromItemsResponse)
                .as("Tracks on second page of playlist weren't shifted correctly")
                .containsExactlyElementsOf(testDataTracks
                        .getTracks()
                        .subList(limit - added, prerequisite));
    }

    @ParameterizedTest
    @CsvSource({
            "105, 3, 97",
            "105, 5, 98"
    })
    @DisplayName("ATTP22 Shift tracks only in next page when tracks added to playlist")
    public void ATTP22_shiftTracksOnlyInNextPageWhenTracksAddedToPlaylistTest(int prerequisite, int added, int position) {
        int limit = 100;
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Prerequisite - add  tracks to playlist to make it possible to add additional track in the middle of list
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(0, limit));
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(limit, prerequisite));

        // Step - add track to playlist
        AddItemsToPlaylistRequest
                .addItems(playlistId, token, getTestDataTracksUris(prerequisite, prerequisite + added), position);

        if (position == limit - added && prerequisite + added > limit) {
            ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest.readItems(token, playlistId, 100);
            List<Track> trackListFromItemsResponse = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);
            assertThat(trackListFromItemsResponse)
                    .as("Tracks on second page of playlist weren't shifted correctly")
                    .containsExactlyElementsOf(testDataTracks
                            .getTracks()
                            .subList(position, prerequisite));

        } else if (position > limit - added && prerequisite + added > limit) {
            ReadPlaylistItemsResponseDto readPlaylistItemsResponse = ReadPlaylistItemsRequest.readItems(token, playlistId, 100);
            List<Track> trackListFromItemsResponse = getTracksFromReadPlaylistItemsResponse(readPlaylistItemsResponse);
            assertThat(trackListFromItemsResponse.subList(added - (limit - position), trackListFromItemsResponse.size()))
                    .as("Tracks on second page of playlist weren't shifted correctly")
                    .containsExactlyElementsOf(testDataTracks
                            .getTracks()
                            .subList(position, prerequisite));

        } else {
            throw new IllegalArgumentException("Wrong amount of prerequisite tracks or position < limit - number of added tracks");
        }
    }

    @Test
    @DisplayName("ATTP23 Don’t add tracks at invalid position")
    public void ATTP23_dontAddTracksAtInvalidPositionTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add track to playlist at invalid position
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, getTestDataTracksUris(0, 3), "invalid");

        MyAssertions.assertErrorResponse(response, 400, "Error parsing JSON.");
    }

    @Test
    @DisplayName("ATTP24 Don’t add tracks at negative position")
    public void ATTP24_dontAddTracksAtNegativePositionTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add track to playlist at negative position
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, getTestDataTracksUris(0, 3), -5);

        MyAssertions.assertErrorResponse(response, 400, "Invalid position, must be positive");
    }

    @Test
    @DisplayName("ATTP25 Don't add track if no uri param provided")
    public void ATTP25_dontAddTrackIfNoUriParamProvidedTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to send request without uri field in body
        Response response = AddItemsToPlaylistRequest.addItemsWithError(playlistId, token);

        MyAssertions.assertErrorResponse(response, 400, "Error parsing JSON.");
    }

    @ParameterizedTest(name = "{displayName} => \"'{0}'\"")
    @ValueSource(strings = {"", "invalid"})
    @DisplayName("ATTP26 Don't add track if invalid uri provided")
    public void ATTP26_dontAddTrackIfEmptyUriProvidedTest(String value) {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to send request with invalid/empty uri field in body
        Response response = AddItemsToPlaylistRequest.addItemsWithError(playlistId, token, List.of(value));

        MyAssertions.assertErrorResponse(response, 400, "Invalid track uri");
    }

    @Test
    @DisplayName("ATTP27 Don't add track if non existing uri provided")
    public void ATTP27_dontAddTrackIfNonExistingUriProvidedTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add track with non-existing uri to playlist
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, List.of(SampleNames.invalidTrackUri));

        MyAssertions.assertErrorResponse(response, 400, "Payload contains a non-existing ID");
    }

    @Test
    @DisplayName("ATTP28 Don't add track if empty playlist id param provided")
    public void ATTP28_dontAddTrackIfEmptyPlaylistIdParamProvidedTest() {
        Track track = testDataTracks.getTracks().get(0);

        // Step - try to send request with empty playlist id
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError("", token, List.of(track.getUri()));

        MyAssertions.assertErrorResponse(response, 404, "Service not found");
    }


    @ParameterizedTest(name = "{displayName} => \"'{1}'\"")
    @MethodSource("invalidPlaylistUriProvider")
    @DisplayName("ATTP29 Don't add track if invalid playlist id provided")
    public void ATTP29_dontAddTrackIfInvalidPlaylistIdProvidedTest(String playlistId, String desc) {
        Track track = testDataTracks.getTracks().get(0);

        // Step - try to add track to playlist with invalid id
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, List.of(track.getUri()));

        MyAssertions.assertErrorResponse(response, 404, "Invalid playlist Id");
    }

    static Stream<Arguments> invalidPlaylistUriProvider() {
        return Stream.of(
                Arguments.arguments("invalid", "invalid"),
                Arguments.arguments(SampleNames.invalidPlaylistUri, "non-existing")
        );
    }

    @Test
    @DisplayName("ATTP30 Don't add track if empty token provided")
    public void ATTP30_dontAddTrackIfEmptyTokenProvidedTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add track to playlist if empty token provided
        Track track = testDataTracks.getTracks().get(0);
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, "", List.of(track.getUri()));

        MyAssertions.assertErrorResponse(response, 400, "Only valid bearer authentication supported");
    }

    @Test
    @DisplayName("ATTP31 Don't add track if invalid token provided")
    public void ATTP31_dontAddTrackIfInvalidTokenProvidedTest() {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add track to playlist if invalid token provided
        Track track = testDataTracks.getTracks().get(0);
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, SampleNames.invalidToken, List.of(track.getUri()));

        MyAssertions.assertErrorResponse(response, 401, "Invalid access token");
    }

    @ParameterizedTest(name = "{displayName} => uri = '{1}'")
    @MethodSource("uriProvider")
    @DisplayName("ATTP32 Don't add other items different than tracks to playlist")
    public void ATTP32_dontAddArtistToPlaylistTest(String invalidUri, String desc) {
        //  Prerequisite - create a playlist
        playlistId = CreatePlaylistRequest
                .getPlaylistId(SpotifyProperties.getUserId(), token, SampleNames.playlistName);

        // Step - try to add other items than tracks to playlist
        Response response = AddItemsToPlaylistRequest
                .addItemsWithError(playlistId, token, List.of(invalidUri));

        MyAssertions.assertErrorResponse(response, 400, "Invalid track uri");
    }

    static Stream<Arguments> uriProvider() {
        return Stream.of(
                Arguments.arguments(SampleNames.albumUri, "album uri"),
                Arguments.arguments(SampleNames.artistUri, "artist uri"),
                Arguments.arguments(SampleNames.playlistUri, "playlist uri"),
                Arguments.arguments(SampleNames.showUri, "show uri")
        );
    }
}
