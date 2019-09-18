package com.mramirid.moviecatalogue.activity;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.adapter.ViewPagerAdapter;
import com.mramirid.moviecatalogue.database.FavoritesDbContract;
import com.mramirid.moviecatalogue.fragment.ItemsFragment;
import com.mramirid.moviecatalogue.widget.FavoritesWidget;

import java.util.Objects;

import static com.mramirid.moviecatalogue.activity.SearchActivity.EXTRA_TYPE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	public static final String MOVIES = "movie";
	public static final String TV_SHOWS = "tv";

	private ViewPager viewPager;
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;
	private TabLayout tabLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = findViewById(R.id.drawer_layout);
		navigationView = findViewById(R.id.nav_view);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
		);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		navigationView.setNavigationItemSelectedListener(this);

		viewPager = findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		tabLayout = findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

		HandlerThread handlerThread = new HandlerThread("DataObserver");
		handlerThread.start();
		Handler handler = new Handler(handlerThread.getLooper());
		DataObserver dataObserver = new DataObserver(handler, this);
		getContentResolver().registerContentObserver(FavoritesDbContract.CONTENT_URI, true, dataObserver);
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPagerAdapter.addFragment(new ItemsFragment(MOVIES), getString(R.string.movies));
		viewPagerAdapter.addFragment(new ItemsFragment(TV_SHOWS), getString(R.string.tv_shows));
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOffscreenPageLimit(2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		final int MOVIES_TAB = 0;
		switch (item.getItemId()) {
			case R.id.action_refresh:
				Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();
				break;
			case R.id.action_search:
				Intent intent = new Intent(MainActivity.this, SearchActivity.class);
				String activeTab = tabLayout.getSelectedTabPosition() == MOVIES_TAB ? "movie" : "tv";
				intent.putExtra(EXTRA_TYPE, activeTab);
				startActivity(intent);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		navigationView.setCheckedItem(R.id.home);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);
		else
			super.onBackPressed();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.favorites:
				Intent moveToFavorites = new Intent(this, FavoritesActivity.class);
				startActivity(moveToFavorites);
				break;
			case R.id.language_settings:
				Intent languageSettingsIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
				startActivity(languageSettingsIntent);
				break;
			case R.id.remider_settings:
				Intent reminderSettingsIntent = new Intent(this, ReminderSettingsActivity.class);
				startActivity(reminderSettingsIntent);
		}
		drawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	public static class DataObserver extends ContentObserver {

		final Context context;

		public DataObserver(Handler handler, Context context) {
			super(handler);
			this.context = context;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			FavoritesWidget.notifyWidgetDataChanged(context);
		}
	}
}
