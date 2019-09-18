package com.mramirid.moviecatalogue.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesDbContract {

	public static final String AUTHORITY = "com.mramirid.moviecatalogue";
	static final String SCHEME = "content";

	public static String TABLE_NAME = "favorites";

	public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
			.authority(AUTHORITY)
			.appendPath(TABLE_NAME)
			.build();

	public static final class FavoritesColumns implements BaseColumns {
		public static final String ITEM_TYPE = "item_type";
		public static final String POSTER = "poster";
		public static final String NAME = "name";
		public static final String GENRES = "genres";
		public static final String DESCRIPTION = "description";
		public static final String YEAR = "year";
		public static final String LANGUAGE = "language";
		public static final String RATING = "rating";
	}
}
