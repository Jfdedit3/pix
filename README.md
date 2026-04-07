# Pix

Android client scaffold inspired by the official Pixiv app.

## Current state
- Kotlin + Jetpack Compose project scaffold
- Demo feed, search, ranking, bookmarks, profile tabs
- Architecture ready for API integration

## Important
This repository currently contains a **functional Android project scaffold**, not a verified full Pixiv production client.
A true feature-parity client requires:
- live Pixiv API verification
- authentication flow validation
- production testing on device
- a local Android SDK / Gradle build environment for final APK generation

## Open in Android Studio
1. Open the repository folder
2. Let Android Studio sync Gradle
3. Build and run on device or emulator

## Next integration steps
- add real OAuth / token flow
- connect feed/search/ranking/bookmark/profile endpoints
- add image viewer, comments, recommendations, notifications, downloads
- test against current Pixiv behavior
