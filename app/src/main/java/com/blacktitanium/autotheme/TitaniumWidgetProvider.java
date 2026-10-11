package com.blacktitanium.autotheme;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TitaniumWidgetProvider extends AppWidgetProvider {
  @Override public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds) {
    Intent battery = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int percent = -1;
    if (battery != null) {
      int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
      if (level >= 0 && scale > 0) percent = Math.round(100f * level / scale);
    }
    for (int id : widgetIds) {
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.titanium_widget);
      String date = new SimpleDateFormat("EEE, d MMM", new Locale("ru", "RU")).format(new Date());
      views.setTextViewText(R.id.widget_date, date.toUpperCase(new Locale("ru", "RU")));
      views.setTextViewText(R.id.widget_battery, percent >= 0 ? "▰ " + percent + "%" : "▰ —");
      manager.updateAppWidget(id, views);
    }
  }
}
