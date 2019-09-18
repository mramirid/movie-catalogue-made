package com.mramirid.moviecatalogue.viewmodel.favoritesviewmodel;

import android.database.Cursor;

public interface LoadFavoritesCallback {
	void postExecute(Cursor cursorFavorites);
}
