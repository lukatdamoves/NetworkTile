# Network Tile

A tiny Android app with one job: a Quick Settings tile that opens the
phone's Mobile network settings page, where you pick 4G or 5G for your SIM.

On dual-SIM phones it opens the page for your data SIM, not just SIM 1.

It changes nothing itself and needs no special permissions, so banking
apps are unaffected. No background work, no tracking, no ads.

## Add the tile

Open the app and tap "Add tile to quick settings" (Android 13+), or pull
down quick settings twice, tap Edit, and drag the "Network" tile up.

## Build

Pushing to `main` builds a debug APK with GitHub Actions
(see `.github/workflows/build-apk.yml`).

Requires Android 7.0+ (API 24) for the Quick Settings tile API.
