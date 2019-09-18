package com.mramirid.moviecatalogue.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.adapter.ViewPagerAdapter;
import com.mramirid.moviecatalogue.fragment.FavoritesFragment;

import java.util.Objects;

import static com.mramirid.moviecatalogue.activity.MainActivity.MOVIES;
import static com.mramirid.moviecatalogue.activity.MainActivity.TV_SHOWS;

public class FavoritesActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.favorites);

		ViewPager viewPager = findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		TabLayout tabLayout = findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new FavoritesFragment(MOVIES), getString(R.string.movies));
		adapter.addFragment(new FavoritesFragment(TV_SHOWS), getString(R.string.tv_shows));
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
	}
}
