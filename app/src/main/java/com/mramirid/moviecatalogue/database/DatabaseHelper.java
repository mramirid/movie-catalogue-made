package com.mramirid.moviecatalogue.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "dbfavorites";
	private static final int DATABASE_VERSION = 1;

	private static final String SQL_CREATE_TABLE_FAVORITES = String.format(
			"CREATE TABLE %s (" +
			"%s INTEGER PRIMARY KEY," +
			"%s TEXT NOT NULL," +
			"%s TEXT NOT NULL," +
			"%s TEXT NOT NULL," +
			"%s TEXT NOT NULL," +
			"%s TEXT NOT NULL," +
			"%s TEXT NOT NULL," +
			"%s TEXT NOT NULL," +
			"%s REAL NOT NULL);",
			FavoritesDbContract.TABLE_NAME,
			FavoritesDbContract.FavoritesColumns._ID,
			FavoritesDbContract.FavoritesColumns.ITEM_TYPE,
			FavoritesDbContract.FavoritesColumns.POSTER,
			FavoritesDbContract.FavoritesColumns.NAME,
			FavoritesDbContract.FavoritesColumns.GENRES,
			FavoritesDbContract.FavoritesColumns.DESCRIPTION,
			FavoritesDbContract.FavoritesColumns.YEAR,
			FavoritesDbContract.FavoritesColumns.LANGUAGE,
			FavoritesDbContract.FavoritesColumns.RATING
	);

	private static final String SQL_CREATE_TABLE_GENRES = String.format(
			"CREATE TABLE %s (" +
			"%s INTEGER PRIMARY KEY," +
			"%s TEXT NOT NULL);",
			GenresDbContract.TABLE_NAME,
			GenresDbContract.GenresColumns._ID,
			GenresDbContract.GenresColumns.NAME
	);

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(SQL_CREATE_TABLE_FAVORITES);
		sqLiteDatabase.execSQL(SQL_CREATE_TABLE_GENRES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesDbContract.TABLE_NAME);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GenresDbContract.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
