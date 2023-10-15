# Alexa Presentation Language (APL) Sample Application for Android

### Description

This is a sample application that uses [apl-viewhost-android](https://github.com/alexa/apl-viewhost-android)
to render APL documents.

This application includes the apl-release.aar, common-release.aar and discover-release.aar
archive files inside `app/libs` folder. 

The aar files are currently built at the [APL 2023.2 commit](https://github.com/alexa/apl-viewhost-android/commit/179863eb980a6e37e4786814cea641dc239dd781) 

### Prerequisites

Make sure you have installed:

- [Android SDK](https://developer.android.com/studio/intro/update) version 28 or higher

### Features

Currently the sample application contains a simple APL document named `testavg.json` under `app/src/main/assets` folder.

* The `APLExampleApp` contains the code to initialize the APL library.
* The `MainActivity` contains the code to render the document.
* The `AssetContentRetriever` contains the code to read APL document JSON files from assets folder
