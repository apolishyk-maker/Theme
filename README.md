# Black Titanium / Carbon — Samsung Galaxy S25 Ultra

An original AMOLED-black theme companion app, icon pack, and home-screen widget for Samsung Galaxy S25 Ultra (Android / One UI).

## What installing the APK gives you

- One-tap application of dark carbon wallpapers to **home and lock** screens, or true-black `#000000` AMOLED wallpaper.
- **24 matching titanium icons**, installed as a third-party icon pack and selected together through Samsung **Good Lock → Theme Park**.
- Minimal dark **clock and battery widget**.
- An on-device step-by-step guide to enabling **black Always On Display** and setting lock-screen clock styles.

The Android app does **not** circumvent Android security protections. Changing AOD settings, activating the icon pack and placing the widget still requires confirmation on the phone. Some app icons may not match automatically due to app/One UI versions.

## Download APK

Open the [Actions page](https://github.com/apolishyk-maker/Theme/actions/workflows/build-apk.yml), select the latest **successful** run, and download the artifact **BlackTitanium-S25Ultra-debug-APK**. Extract `app-debug.apk` from the artifact ZIP and install it on the device.

This is a debug-signed APK intended for personal installation. Only install a file built from this repository that you trust.

## Build

Push updates to `main`, or start the workflow with **Run workflow**. GitHub Actions installs Java 17 and Android SDK, generates the art assets from code and builds `assembleDebug`.

## Permissions and privacy

The app requests only `SET_WALLPAPER` and uses no internet permission. There is no telemetry or remote service. The assets are generated in CI from `tools/generate_assets.py`.

See also [Russian setup instructions](BUILD_APK_RU.md).
