package com.vincentramdhanie.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Vincent Ramdhanie on 7/20/15.
 */
public class Movie implements Parcelable{
    long id;
    String title;
    String poster;
    String description;
    double voteAverage;
    int releaseYear;

    public Movie() {
        this(0, null, null, null, 0, null);
    }

    public Movie(long id, String title, String poster, String description, double voteAverage, Calendar releaseDate  ){
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.description = description;
        this.voteAverage = voteAverage;
        if(releaseDate != null){
            releaseYear = releaseDate.get(Calendar.YEAR);
        }else{
            releaseYear = 0;
        }
    }

    protected Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        poster = in.readString();
        description = in.readString();
        voteAverage = in.readDouble();
        releaseYear = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(description);
        dest.writeDouble(voteAverage);
        dest.writeInt(releaseYear);
    }
}
