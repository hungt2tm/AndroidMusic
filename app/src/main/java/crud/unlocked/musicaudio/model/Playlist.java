package crud.unlocked.musicaudio.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Irelia on 12/25/2016.
 */

public class Playlist implements Parcelable {
    private String id;
    private String title;
    private String artist;
    private String artworkUrl;
    private String tracksUri;
    private int likesCount;
    private int trackCount;
    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public String getTracksUri() {
        return tracksUri;
    }

    public void setTracksUri(String tracksUri) {
        this.tracksUri = tracksUri;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
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
        parcel.writeString(tracksUri);
        parcel.writeInt(likesCount);
        parcel.writeInt(trackCount);
        parcel.writeLong(duration);
    }

    public Playlist() {
    }

    private Playlist(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        artworkUrl = in.readString();
        tracksUri = in.readString();
        likesCount = in.readInt();
        trackCount = in.readInt();
        duration = in.readLong();
    }

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }
        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
