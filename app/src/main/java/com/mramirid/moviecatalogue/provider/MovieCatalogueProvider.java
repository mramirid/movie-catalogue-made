package com.mramirid.moviecatalogue.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mramirid.moviecatalogue.activity.MainActivity;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.database.GenresDbContract;
import com.mramirid.moviecatalogue.database.MovieCatalogueDbHelper;

import java.util.Objects;

import static com.mramirid.moviecatalogue.database.FavoritesDbContract.AUTHORITY;

public class MovieCatalogueProvider extends ContentProvider {

	private static final int FAVORITES = 1;
	private static final int FAVORITES_ID = 2;
	private static final int GENRES = 3;

	private MovieCatalogueDbHelper movieCatalogueDbHelper;
	private static final UriMatcher stringUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		// content://com.mramirid.moviecatalogue/favorites
		stringUriMatcher.addURI(AUTHORITY, FavoritesDbContract.TABLE_NAME, FAVORITES);

		// content://com.mramirid.moviecatalogue/favorites/id
		stringUriMatcher.addURI(AUTHORITY, FavoritesDbContract.TABLE_NAME + "/#", FAVORITES_ID);

		// content://com.mramirid.moviecatalogue/genres
		stringUriMatcher.addURI(AUTHORITY, GenresDbContract.TABLE_NAME, GENRES);
	}

	@Override
	public boolean onCreate() {
		movieCatalogueDbHelper = MovieCatalogueDbHelper.getInstance(getContext());
		return true;
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
		movieCatalogueDbHelper.open();
		Cursor cursor;
		switch (stringUriMatcher.match(uri)) {
			case FAVORITES:
				cursor = movieCatalogueDbHelper.getFavoritesByItemTypeProvider(strings1);
				break;
			case FAVORITES_ID:
				cursor = movieCatalogueDbHelper.getFavoriteByIdProvider(uri.getLastPathSegment());
				break;
			case GENRES:
				cursor = movieCatalogueDbHelper.getGenresProvider(strings1);
				break;
			default:
				cursor = null;
				break;
		}
		return cursor;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
		movieCatalogueDbHelper.open();
		long added;
		switch (stringUriMatcher.match(uri)) {
			case FAVORITES:
				added = movieCatalogueDbHelper.insertFavoriteProvider(contentValues);
				Objects.requireNonNull(getContext()).getContentResolver().notifyChange(
								FavoritesDbContract.CONTENT_URI,
								new MainActivity.DataObserver(new Handler(), getContext())
				);
				break;
			case GENRES:
				added = movieCatalogueDbHelper.insertGenreProvider(contentValues);
				break;
			default:
				added = 0;
				break;
		}
		return Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + added);
	}

	@Override
	public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
		movieCatalogueDbHelper.open();
		int deleted;

		if (stringUriMatcher.match(uri) == FAVORITES_ID) {
			deleted = (int) movieCatalogueDbHelper.deleteFavoriteProvider(uri.getLastPathSegment());
			Objects.requireNonNull(getContext()).getContentResolver().notifyChange(
					FavoritesDbContract.CONTENT_URI,
					new MainActivity.DataObserver(new Handler(), getContext())
			);
		}
		else {
			deleted = 0;
		}

		return deleted;
	}

	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
		return 0;
	}
}
