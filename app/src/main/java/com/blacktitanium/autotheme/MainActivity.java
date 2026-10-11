package com.blacktitanium.autotheme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
  private static final int BG = Color.BLACK;
  private static final int TEXT = Color.rgb(226, 228, 232);
  private static final int SECONDARY = Color.rgb(156, 161, 168);
  private LinearLayout main;
  private boolean busy;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setStatusBarColor(Color.BLACK);
    getWindow().setNavigationBarColor(Color.BLACK);
    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(true);
    scroll.setFitsSystemWindows(true);
    scroll.setBackgroundColor(BG);
    main = new LinearLayout(this);
    main.setOrientation(LinearLayout.VERTICAL);
    main.setPadding(dp(22), dp(26), dp(22), dp(40));
    scroll.addView(main);
    setContentView(scroll);
    text("BLACK TITANIUM", 27, TEXT, true);
    text("CARBON  /  GALAXY S25 ULTRA", 12, SECONDARY, false);
    ImageView preview = new ImageView(this);
    preview.setImageResource(R.drawable.app_icon);
    preview.setAdjustViewBounds(true);
    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dp(98), dp(98));
    imageParams.topMargin = dp(17);
    imageParams.bottomMargin = dp(13);
    main.addView(preview, imageParams);
    text("Чёрные AMOLED-обои, единый набор 24 титановых иконок и часы с батареей. Без root, рекламы и сбора данных.", 15, SECONDARY, false);
    space(17);
    button("1  ·  Применить обои автоматически", this::chooseWallpaper);
    button("2  ·  Применить все иконки через Theme Park", this::openThemePark);
    button("3  ·  Настроить настоящий чёрный AOD", this::openAodGuide);
    button("4  ·  Добавить виджет часов", this::showWidgetGuide);
    space(16);
    text("Что останется вручную", 18, TEXT, true);
    text("Иконки: в Theme Park выбери этот пакет и нажми «Применить». Для AOD и виджета Samsung требует действий в системном интерфейсе.", 13, SECONDARY, false);
  }

  private void chooseWallpaper() {
    if (busy) return;
    new AlertDialog.Builder(this)
      .setTitle("Установить обои")
      .setItems(new String[] { "Карбон: на рабочий стол и экран блокировки",
                               "Абсолютно чёрные AMOLED на оба экрана" },
          (d, which) -> applyWallpaper(which == 1))
      .setNegativeButton("Отмена", null).show();
  }

  private void applyWallpaper(boolean pureBlack) {
    if (busy) return;
    busy = true;
    Toast.makeText(this, "Устанавливаю обои…", Toast.LENGTH_SHORT).show();
    new Thread(() -> {
      Exception error = null;
      try {
        WallpaperManager wm = WallpaperManager.getInstance(this);
        if (pureBlack) {
          set(wm, "wallpaper_amoled.png", WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
        } else {
          set(wm, "wallpaper_home.png", WallpaperManager.FLAG_SYSTEM);
          set(wm, "wallpaper_lock.png", WallpaperManager.FLAG_LOCK);
        }
      } catch (Exception ex) { error = ex; }
      final Exception result = error;
      runOnUiThread(() -> {
        busy = false;
        if (result == null) {
          new AlertDialog.Builder(this).setTitle("Обои установлены")
            .setMessage("Теперь примени пакет иконок в Theme Park.")
            .setPositiveButton("К иконкам", (d,w) -> openThemePark())
            .setNegativeButton("Позже", null).show();
        } else {
          new AlertDialog.Builder(this).setTitle("Не удалось применить обои")
            .setMessage("Android не разрешил применить обои. Повтори действие или проверь настройки обоев. Ошибка: " + result.getClass().getSimpleName())
            .setPositiveButton("Понятно", null).show();
        }
      });
    }).start();
  }

  private void set(WallpaperManager wm, String file, int flags) throws Exception {
    Bitmap bitmap = null;
    try (java.io.InputStream in = getAssets().open(file)) {
      bitmap = BitmapFactory.decodeStream(in);
      if (bitmap == null) throw new IllegalStateException("Invalid wallpaper");
      wm.setBitmap(bitmap, null, true, flags);
    } finally {
      if (bitmap != null) bitmap.recycle();
    }
  }

  private void openThemePark() {
    new AlertDialog.Builder(this)
      .setTitle("Все иконки — одним пакетом")
      .setMessage("Открой Good Lock → Theme Park → Icon → Create New → Iconpack → Third party icon packs → Black Titanium → Save → Apply.\n\nБольшинство популярных приложений сопоставится автоматически. Для некоторых других может потребоваться ручной выбор.")
      .setPositiveButton("Открыть Theme Park", (d,w) -> {
        Intent launch = getPackageManager().getLaunchIntentForPackage("com.samsung.android.themedesigner");
        if (launch != null) startActivity(launch);
        else new AlertDialog.Builder(this).setTitle("Theme Park не найден")
          .setMessage("Установи Good Lock и модуль Theme Park из Galaxy Store.")
          .setPositiveButton("Открыть Galaxy Store", (dd,ww) -> {
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://galaxystore.samsung.com/detail/com.samsung.android.themedesigner"))); }
            catch (Exception ignored) { Toast.makeText(this, "Открой Galaxy Store", Toast.LENGTH_LONG).show(); }
          }).setNegativeButton("Закрыть", null).show();
      }).setNegativeButton("Позже", null).show();
  }

  private void openAodGuide() {
    new AlertDialog.Builder(this).setTitle("Always On Display — True Black")
      .setMessage("Настройки → Экран блокировки и AOD → Always On Display → Вкл.\n\n«Показывать обои экрана блокировки» → Выкл.\n\nВыбери белые тонкие часы в редакторе экрана блокировки.\n\nСтороннее приложение не может автоматически менять защищённые настройки AOD.")
      .setPositiveButton("Открыть настройки", (d,w) -> startActivity(new Intent(Settings.ACTION_SETTINGS)))
      .setNegativeButton("Позже", null).show();
  }

  private void showWidgetGuide() {
    new AlertDialog.Builder(this).setTitle("Часы + батарея")
      .setMessage("На рабочем столе удерживай пустое место → Виджеты → Black Titanium → добавь часы. Часы обновляются динамически, батарея — примерно раз в 30 минут.")
      .setPositiveButton("Понятно", null).show();
  }

  private void button(String label, Runnable action) {
    Button b = new Button(this);
    b.setAllCaps(false);
    b.setText(label);
    b.setTextColor(TEXT);
    b.setTextSize(14);
    b.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
    b.setPadding(dp(16),dp(9),dp(9),dp(9));
    GradientDrawable bg = new GradientDrawable();
    bg.setColor(Color.rgb(20,21,23));
    bg.setCornerRadius(dp(14));
    bg.setStroke(dp(1),Color.rgb(64,67,72));
    b.setBackground(bg);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,dp(67));
    lp.bottomMargin=dp(11);
    main.addView(b,lp);
    b.setOnClickListener(v -> action.run());
  }

  private void text(String value, int sp, int color, boolean bold) {
    TextView t = new TextView(this);
    t.setText(value);
    t.setTextColor(color);
    t.setTextSize(sp);
    if (bold) t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
    lp.bottomMargin=dp(9);
    main.addView(t,lp);
  }
  private void space(int dpValue) { main.addView(new View(this),new LinearLayout.LayoutParams(1,dp(dpValue))); }
  private int dp(float v) {return Math.round(v*getResources().getDisplayMetrics().density);}
}
