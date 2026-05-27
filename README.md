📚 Library Management App
An Android application for managing library book inventory, built with Kotlin and Firebase. Developed as part of an internship at Internz Learn.

Features

Add Books — Add new books to the library with title, author, and category details

Search & Browse — Search the catalogue by title or author name

Borrow & Return Tracking — Mark books as borrowed or returned and track availability in real time

Firebase Integration — Real-time database synchronisation ensures data is always up to date

User Authentication — Secure login and registration via Firebase Authentication

Delete & Update — Edit or remove book records from the catalogue



Tech Stack
LayerTechnologyLanguageKotlinPlatformAndroid (SDK)DatabaseFirebase Realtime DatabaseAuthenticationFirebase AuthenticationUIXML Layouts, Material DesignBuildGradle (Kotlin DSL)

Screenshots

Add screenshots of your app here. In Android Studio: Run the app on an emulator → take a screenshot → drag the image into this folder and reference it below.

![Home Screen](screenshots/home.png)
![Add Book](screenshots/add_book.png)
![Book List](screenshots/book_list.png)

Getting Started
Prerequisites

Android Studio (Hedgehog or later)
Android device or emulator running API 21+
A Firebase project (free tier is sufficient)

Setup

Clone the repository

bash   git clone https://github.com/jenilvekariya123/Library_Mangement.git

Open the project in Android Studio
Connect Firebase:

Go to Firebase Console
Create a new project
Add an Android app using your package name
Download google-services.json and place it in the /app directory


Build and run the project on your device or emulator


Project Structure
app/
├── src/
│   └── main/
│       ├── java/         # Kotlin source files (Activities, Adapters, Models)
│       ├── res/
│       │   ├── layout/   # XML UI layouts
│       │   ├── drawable/ # Icons and images
│       │   └── values/   # Colours, strings, themes
│       └── AndroidManifest.xml
build.gradle.kts           # App-level Gradle config

What I Learned

Building a full CRUD Android application from scratch in Kotlin
Integrating Firebase Realtime Database for live data sync
Implementing Firebase Authentication for user login/registration
Designing multi-screen Android UIs with XML and Material Design components
Signing and packaging a release APK for deployment


Author
Jenil Vekariya

GitHub: @jenilvekariya123
LinkedIn: jenil-vekariya-360478220


License
This project is open source and available under the MIT License.ShareContentpdf
