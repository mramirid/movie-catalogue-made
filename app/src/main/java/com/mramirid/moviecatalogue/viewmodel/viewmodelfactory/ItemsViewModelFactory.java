package com.mramirid.moviecatalogue.viewmodel.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mramirid.moviecatalogue.viewmodel.itemsviewmodel.ItemsViewModel;

public class ItemsViewModelFactory implements ViewModelProvider.Factory {

	private String itemType;

	public ItemsViewModelFactory(String itemType) {
		this.itemType = itemType;
	}

	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new ItemsViewModel(itemType);
	}
}
