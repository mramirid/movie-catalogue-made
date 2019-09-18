package com.mramirid.moviecatalogue.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mramirid.moviecatalogue.R;
import com.mramirid.moviecatalogue.repository.ReleaseTodayRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String DAILY_REMINDER = "daily_reminder";
	public static final String RELEASE_REMINDER = "release_reminder";

	public static final int ID_DAILY_REMINDER = 101;
	public static int ID_RELEASE_REMINDER = 201;
	public static int ID_RELEASE_REMINDER_DIFF = 0;

	private static final String EXTRA_TYPE = "extra_type";

	public void setRepeatingAlarm(Context context, String type, String time) {
		if (isTimeInvalid(time))
			return;

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(EXTRA_TYPE, type);

		String[] timeArray = time.split(":");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
		calendar.set(Calendar.SECOND, 0);

		int requestCode = type.equals(DAILY_REMINDER) ? ID_DAILY_REMINDER : ID_RELEASE_REMINDER;

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

		if (alarmManager != null)
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

		String localeType = type.equals(DAILY_REMINDER) ?
				context.getString(R.string.daily_reminder) :
				context.getString(R.string.release_today_reminder);

		Toast.makeText(context, String.format(context.getString(R.string.alarm_turned_on), localeType), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String type = intent.getStringExtra(EXTRA_TYPE);
		if (type != null) {
			if (type.equalsIgnoreCase(DAILY_REMINDER))
				AlarmNotification.showDailyReminderNotification(context);
			else
				new ReleaseTodayRepository(context);
		}
	}

	public void cancelAlarm(Context context, String type) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		int requestCode = type.equals(DAILY_REMINDER) ? ID_DAILY_REMINDER : (ID_RELEASE_REMINDER - ID_RELEASE_REMINDER_DIFF);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
		pendingIntent.cancel();

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmManager != null)
			alarmManager.cancel(pendingIntent);

		String localeType = type.equals(DAILY_REMINDER) ?
				context.getString(R.string.daily_reminder) :
				context.getString(R.string.release_today_reminder);

		Toast.makeText(context, String.format(context.getString(R.string.alarm_turned_off), localeType), Toast.LENGTH_SHORT).show();
	}

	private boolean isTimeInvalid(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
			dateFormat.setLenient(false);
			dateFormat.parse(time);
			return false;
		} catch (ParseException e) {
			return true;
		}
	}
}
