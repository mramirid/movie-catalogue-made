package com.mramirid.moviecatalogue.helper;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.database.GenresDbContract;
import com.mramirid.moviecatalogue.model.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class MappingHelper {

	public static ArrayList<Item> mapItemsCursorToArrayList(Cursor itemsCursor) {
		if (itemsCursor == null)
			return new ArrayList<>();

		ArrayList<Item> itemsList = new ArrayList<>();

		while (itemsCursor.moveToNext()) {
			int id = itemsCursor.getInt(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns._ID));
			String itemType = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.ITEM_TYPE));
			String poster = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.POSTER));
			String name = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.NAME));
			String genres = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.GENRES));
			String description = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.DESCRIPTION));
			String year = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.YEAR));
			String language = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.LANGUAGE));
			float rating = itemsCursor.getFloat(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.RATING));
			Log.d("MappingHelper", "mapItemsCursorToArrayList: " + itemType);
			itemsList.add(new Item(id, itemType, poster, name, genres, description, year, language, rating));
		}

		return itemsList;
	}

	public static HashMap<Integer, String> mapGenresCursorToHashMap(Cursor genresCursor) {
		@SuppressLint("UseSparseArrays")
		HashMap<Integer, String> genresList = new HashMap<>();

		while (genresCursor.moveToNext()) {
			int id = genresCursor.getInt(genresCursor.getColumnIndexOrThrow(GenresDbContract.GenresColumns._ID));
			String name = genresCursor.getString(genresCursor.getColumnIndexOrThrow(GenresDbContract.GenresColumns.NAME));
			genresList.put(id, name);
		}

		return genresList;
	}
}
