package com.mramirid.moviecatalogue.viewmodel.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mramirid.moviecatalogue.viewmodel.favoritesviewmodel.FavoritesViewModel;

public class FavoritesViewModelFactory implements ViewModelProvider.Factory {

	private String itemType;

	public FavoritesViewModelFactory(String itemType) {
		this.itemType = itemType;
	}

	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new FavoritesViewModel(itemType);
	}
}
