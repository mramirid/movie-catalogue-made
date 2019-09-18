package com.mramirid.moviecatalogue.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.adapter.ItemsAdapter;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.fragment.recycleviewhelper.SpacesItemDecoration;
import com.mramirid.moviecatalogue.model.Item;
import com.mramirid.moviecatalogue.viewmodel.searchviewmodel.SearchViewModel;
import com.mramirid.moviecatalogue.viewmodel.viewmodelfactory.SearchViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

	public static final String EXTRA_QUERY = "extra_search";
	public static final String EXTRA_TYPE = "extra_type";

	private ItemsAdapter itemsAdapter;
	private SearchViewModel searchViewModel;
	private TextView tvNotFound;
	private ProgressBar progressBar;
	private String itemType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle(null);

		itemType = getIntent().getStringExtra(EXTRA_TYPE);

		itemsAdapter = new ItemsAdapter(this);

		tvNotFound = findViewById(R.id.tv_no_items_match);
		progressBar = findViewById(R.id.progress_bar);
		RecyclerView recyclerView = findViewById(R.id.rv_items);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(itemsAdapter);
		int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.rv_item_margin);
		recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

		searchViewModel = ViewModelProviders.of(this, new SearchViewModelFactory(itemType)).get(SearchViewModel.class);
		searchViewModel.getItemsLiveData().observe(this, getItems);

		itemsAdapter.setOnItemClickCallback(new ItemsAdapter.OnItemClickCallback() {
			@Override
			public void onItemClicked(Item item, ItemsAdapter.ItemViewHolder holder, int position) {
				// Set transition
				Pair[] pairs = new Pair[3];
				pairs[0] = new Pair<View, String>(holder.imgPoster, "posterTransition");
				pairs[1] = new Pair<View, String>(holder.tvName, "nameTransition");
				pairs[2] = new Pair<View, String>(holder.ratingBar, "ratingBarTransition");
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, pairs);

				Intent intent = new Intent(SearchActivity.this, ItemDetailActivity.class);
				Uri uriItemId = Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + itemsAdapter.getItemsList().get(position).getId());
				intent.setData(uriItemId);
				intent.putExtra(ItemDetailActivity.ITEM_EXTRA, item);
				startActivity(intent, options.toBundle());
			}
		});
	}

	private Observer<ArrayList<Item>> getItems = new Observer<ArrayList<Item>>() {
		@Override
		public void onChanged(ArrayList<Item> items) {
			if (items.size() == 0) {
				showNotFound(true);
			}
			else {
				showNotFound(false);
				itemsAdapter.setData(items);
				itemsAdapter.notifyDataSetChanged();
			}
			showLoading(false);
		}
	};

	private void showNotFound(boolean state) {
		if (state)
			tvNotFound.setVisibility(View.VISIBLE);
		else
			tvNotFound.setVisibility(View.GONE);
	}

	private void showLoading(boolean state) {
		if (state)
			progressBar.setVisibility(View.VISIBLE);
		else
			progressBar.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchItem.getActionView();

		searchItem.expandActionView();
		searchView.setQuery(getIntent().getStringExtra(EXTRA_QUERY), true);
		searchView.setQueryHint("Search " + itemType);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				showNotFound(false);
				itemsAdapter.clearItems();
				query = query.toLowerCase().trim();
				searchViewModel.search(query);
				showLoading(true);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem menuItem) {
				return false;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem menuItem) {
				finish();
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}
}
