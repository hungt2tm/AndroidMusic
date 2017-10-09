package crud.unlocked.musicaudio.util;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import crud.unlocked.musicaudio.model.Playlist;
import crud.unlocked.musicaudio.model.Track;

/**
 * Created by Irelia on 12/10/2016.
 */

public class SoundCloud {

    //buildUrl
    public static String buildUrl(String kind, String genre, String limit) {
        //URL Request
        return Uri.parse(MyConfig.APIV2_URL)
                .buildUpon()
                .appendQueryParameter("client_id", MyConfig.CLIENT_ID)
                .appendPath(MyConfig.PATH_CHARTS)
                .appendQueryParameter("kind", kind)
                .appendQueryParameter("genre", genre)
                .appendQueryParameter("limit", limit).build().toString();
    }

    //onSuccess(List<Track>)
    public interface TrackListInterface {
        void onSuccess(List<Track> trackList);
    }

    //onSuccess(Track)
    public interface TrackInterface {
        void onSuccess(Track track);
    }

    //onSuccess(List<Playlist>)
    public interface PlaylistsInterface {
        void onSuccess(List<Playlist> playlists);
    }

    //onSuccess(Playlist)
    public interface PlaylistInterface {
        void onSuccess(Playlist playlist);
    }

    private static String TAG = "SoundCloud";


    //List genre when click Top, NewHot
    public static JsonObjectRequest requestChartsGenres(String url, final TrackListInterface callback) {
        return new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Add 3 image to a Genre
                    callback.onSuccess(parseJsonObjectToTrackList(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
    }

    //List track when click chart by genre
    public static JsonObjectRequest requestTrackListByGenre(String url, final TrackListInterface callback) {
        return new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Parse
                            callback.onSuccess(parseJsonObjectToTrackList(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: Get Charts All Genre - " + error.getMessage());
            }
        });
    }

    //Parse JSONObject-Collection to trackList
    private static List<Track> parseJsonObjectToTrackList(JSONObject jsonObjectCollection) throws JSONException {
        List<Track> trackList = new ArrayList<>();
        JSONArray collection = jsonObjectCollection.getJSONArray("collection");
        int size = collection.length();
        for (int i = 0; i < size; i++) {
            JSONObject collectionJsonObject = collection.getJSONObject(i);
            JSONObject trackJObj = collectionJsonObject.getJSONObject("track");
            Track track = new Track();
            track.setId(trackJObj.getString("id"));
            track.setTitle(trackJObj.getString("title"));
            track.setStreamUrl(trackJObj.getString("uri") + "/stream");
            track.setDuration(trackJObj.getLong("duration"));
            if (!trackJObj.getString("playback_count").equals("null")) {
                track.setPlaybackCount(trackJObj.getInt("playback_count"));
            }
            if (!trackJObj.getString("likes_count").equals("null")) {
                track.setFavoritingsCount(trackJObj.getInt("likes_count"));
            }
            JSONObject user = trackJObj.getJSONObject("user");
            track.setArtist(user.getString("username"));
            String artworkUrl = trackJObj.getString("artwork_url");
            if (artworkUrl.equals("null") && user.getString("avatar_url").
                    toLowerCase().indexOf("default_avatar_large") == -1) {
                track.setArtworkUrl(user.getString("avatar_url"));
            } else {
                track.setArtworkUrl(artworkUrl);
            }
            trackList.add(track);
        }
        return trackList;
    }


    //Search Track, Playlist
    //Request JsonArray-Track by q=query
    public static JsonArrayRequest requestTrackListBySearch(String searchTrackUrl, final TrackListInterface callback) {
        return new JsonArrayRequest(searchTrackUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.onSuccess(parseJsonArrayToTrackList(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error: JsonArray-Track Search Request - " + error.getMessage());
            }
        });
    }

    //Parse JSONArray-Search to trackList
    private static List<Track> parseJsonArrayToTrackList(JSONArray jsonArray) throws JSONException {
        List<Track> trackList = new ArrayList<>();
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject trackJObj = jsonArray.getJSONObject(i);
            Track track = new Track();
            track.setId(trackJObj.getString("id"));
            track.setTitle(trackJObj.getString("title"));
            track.setStreamUrl(trackJObj.getString("uri") + "/stream");
            track.setDuration(trackJObj.getLong("duration"));
            if (trackJObj.has("playback_count")) {
                if (!trackJObj.getString("playback_count").equals("null")) {
                    track.setPlaybackCount(trackJObj.getInt("playback_count"));
                }
            }
            if (trackJObj.has("likes_count")) {
                if (!trackJObj.getString("likes_count").equals("null")) {
                    track.setFavoritingsCount(trackJObj.getInt("likes_count"));
                }
            } else {
                if (trackJObj.has("favoritings_count")) {
                    if (!trackJObj.getString("favoritings_count").equals("null")) {
                        track.setFavoritingsCount(trackJObj.getInt("favoritings_count"));
                    }
                }
            }
            JSONObject user = trackJObj.getJSONObject("user");
            track.setArtist(user.getString("username"));
            String artworkUrl = trackJObj.getString("artwork_url");
            if (artworkUrl.equals("null") && user.getString("avatar_url").
                    toLowerCase().indexOf("default_avatar_large") == -1) {
                track.setArtworkUrl(user.getString("avatar_url"));
            } else {
                track.setArtworkUrl(artworkUrl);
            }
            trackList.add(track);
        }
        return trackList;
    }

    //Request JsonArray-Playlist by q=query
    public static JsonArrayRequest requestPlaylistBySearch(String searchPlaylistUrl, final PlaylistsInterface callback) {
        return new JsonArrayRequest(searchPlaylistUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.onSuccess(parseJsonArrayToPlayList(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error: JsonArray-Playlist by q=query - " + error.getMessage());
            }
        });
    }

    //Parse JSONArray to List<Playlist>
    private static List<Playlist> parseJsonArrayToPlayList(JSONArray jsonArray) throws JSONException {
        List<Playlist> playlists = new ArrayList<>();
        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
            JSONObject playlistJObj = jsonArray.getJSONObject(i);
            Playlist playlist = new Playlist();
            playlist.setId(playlistJObj.getString("id"));
            playlist.setDuration(playlistJObj.getLong("duration"));
            playlist.setTitle(playlistJObj.getString("title"));
            playlist.setTracksUri(playlistJObj.getString("tracks_uri"));
            if (!playlistJObj.getString("track_count").equals("null")) {
                playlist.setTrackCount(playlistJObj.getInt("track_count"));
            }
            if (!playlistJObj.getString("likes_count").equals("null")) {
                playlist.setLikesCount(playlistJObj.getInt("likes_count"));
            }
            JSONObject user = playlistJObj.getJSONObject("user");
            playlist.setArtist(user.getString("username"));
            String artworkUrl = playlistJObj.getString("artwork_url");
            if (artworkUrl.equals("null") && user.getString("avatar_url").
                    toLowerCase().indexOf("default_avatar_large") == -1) {
                playlist.setArtworkUrl(user.getString("avatar_url"));
            } else {
                playlist.setArtworkUrl(artworkUrl);
            }
            playlists.add(playlist);
        }
        return playlists;
    }

}
