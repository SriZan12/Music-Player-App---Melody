package MusicComponent;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SongsDetails implements Parcelable {
    Uri songUri;
    String SongName;
    String SongArtist;
    int duration;
    int size;

    public SongsDetails(Uri songUri, String songName, String songArtist, int duration, int size) {
        this.songUri = songUri;
        SongName = songName;
        SongArtist = songArtist;
        this.duration = duration;
        this.size = size;
    }

    protected SongsDetails(Parcel in) {
        songUri = in.readParcelable(Uri.class.getClassLoader());
        SongName = in.readString();
        SongArtist = in.readString();
        duration = in.readInt();
        size = in.readInt();
    }

    public static final Creator<SongsDetails> CREATOR = new Creator<SongsDetails>() {
        @Override
        public SongsDetails createFromParcel(Parcel in) {
            return new SongsDetails(in);
        }

        @Override
        public SongsDetails[] newArray(int size) {
            return new SongsDetails[size];
        }
    };


    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }

    public Uri getSongUri(){
        return songUri;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getSongArtist() {
        return SongArtist;
    }

    public void setSongArtist(String songArtist) {
        SongArtist = songArtist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(songUri, flags);
        dest.writeString(SongName);
        dest.writeString(SongArtist);
        dest.writeInt(duration);
        dest.writeInt(size);
    }
}
