package com.mramirid.moviecatalogue.database;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.mramirid.moviecatalogue.database.FavoritesDbContract.AUTHORITY;
import static com.mramirid.moviecatalogue.database.FavoritesDbContract.SCHEME;

public class GenresDbContract {

	public static String TABLE_NAME = "genres";

	public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
			.authority(AUTHORITY)
			.appendPath(TABLE_NAME)
			.build();

	public static final class GenresColumns implements BaseColumns {
		public static final String NAME = "name";
	}
}
