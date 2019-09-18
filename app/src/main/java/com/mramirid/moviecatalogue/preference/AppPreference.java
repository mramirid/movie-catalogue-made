package com.mramirid.moviecatalogue.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {

	private static final String PREFS_NAME = "MovieCataloguePref";

	private static final String APP_FIRST_RUN = "app_first_run";
	private static final String APP_DAILY_REMINDER = "app_daily_reminder";
	private static final String APP_RELEASE_TODAY_REMINDER = "app_release_today_reminder";

	private SharedPreferences preferences;

	public AppPreference(Context context) {
		preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	public void setAppFirstRun(Boolean input) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(APP_FIRST_RUN, input);
		editor.apply();
	}

	public void setAppDailyReminder(Boolean input) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(APP_DAILY_REMINDER, input);
		editor.apply();
	}

	public void setAppReleaseTodayReminder(boolean input) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(APP_RELEASE_TODAY_REMINDER, input);
		editor.apply();
	}

	public Boolean getAppFirstRun() {
		return preferences.getBoolean(APP_FIRST_RUN, true);
	}

	public Boolean getAppDailyReminder() {
		return preferences.getBoolean(APP_DAILY_REMINDER, false);
	}

	public boolean getAppReleaseTodayReminder() {
		return preferences.getBoolean(APP_RELEASE_TODAY_REMINDER, false);
	}
}
