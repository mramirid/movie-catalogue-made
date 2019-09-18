package com.mramirid.moviecatalogue.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mramirid.moviecatalogue.activity.MainActivity.MOVIES;

public class Item implements Parcelable {

    private int id;
    private String itemType, poster, name, genres, description, year, language;
    private float rating;

    public Item() {
    }

    public Item(int id, String itemType, String poster, String name, String genres, String description, String year, String language, float rating) {
        this.id = id;
        this.itemType = itemType;
        this.poster = poster;
        this.name = name;
        this.genres = genres;
        this.description = description;
        this.year = year;
        this.language = language;
        this.rating = rating;
    }

    public Item(JSONObject item, String itemType) {
        try {
            this.id = item.getInt("id");

            this.itemType = itemType;

            poster = "https://image.tmdb.org/t/p/w342" + item.getString("poster_path");

            name = itemType.equals(MOVIES) ? item.getString("title") : item.getString("name");

            rating = (float) item.getDouble("vote_average") / 2;

            language = item.getString("original_language").toUpperCase();

            year = itemType.equals(MOVIES) ? item.getString("release_date") : item.getString("first_air_date");
            if (year.length() > 4)
                year = year.substring(0, 4);

            description = item.getString("overview");

            genres = "";
            JSONArray listIdGenres = item.getJSONArray("genre_ids");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < listIdGenres.length(); ++i)
                stringBuilder.append(listIdGenres.getInt(i)).append("_");
            genres = stringBuilder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public String getItemType() {
        return itemType;
    }

    public String getPoster() {
        return poster;
    }

    public String getName() {
        return name;
    }

    public String getGenres() {
        return genres;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }

    public String getLanguage() {
        return language;
    }

    private Item(Parcel in) {
        id = in.readInt();
        itemType = in.readString();
        poster = in.readString();
        name = in.readString();
        genres = in.readString();
        description = in.readString();
        rating = in.readFloat();
        year = in.readString();
        language = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(itemType);
        parcel.writeString(poster);
        parcel.writeString(name);
        parcel.writeString(genres);
        parcel.writeString(description);
        parcel.writeFloat(rating);
        parcel.writeString(year);
        parcel.writeString(language);
    }
}
