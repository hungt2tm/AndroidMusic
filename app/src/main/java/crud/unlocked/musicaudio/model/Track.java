package crud.unlocked.musicaudio.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Irelia on 12/16/2016.
 */

public class Track implements Comparable<Track>, Parcelable {

    private String id;
    private String title;
    private String artist;
    private String artworkUrl;
    private long duration;
    private String streamUrl;
    private int playbackCount;
    private int favoritingsCount;
    private String genre;

    public Track() {
    }

    private Track(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        artworkUrl = in.readString();
        duration = in.readLong();
        streamUrl = in.readString();
        playbackCount = in.readInt();
        favoritingsCount = in.readInt();
        genre = in.readString();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public int getPlaybackCount() {
        return playbackCount;
    }

    public void setPlaybackCount(int playbackCount) {
        this.playbackCount = playbackCount;
    }

    public int getFavoritingsCount() {
        return favoritingsCount;
    }

    public void setFavoritingsCount(int favoritingsCount) {
        this.favoritingsCount = favoritingsCount;
    }

    @Override
    public int compareTo(Track track) {
        return track.playbackCount - this.playbackCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(artworkUrl);
        parcel.writeLong(duration);
        parcel.writeString(streamUrl);
        parcel.writeInt(playbackCount);
        parcel.writeInt(favoritingsCount);
        parcel.writeString(genre);
    }
}
