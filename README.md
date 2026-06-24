# LifeSaver — Blood Donation App

**A native Android app that connects blood donors, patients, and NGOs — find nearby camps, post urgent requests, and track your own donation eligibility.**

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white) ![Java](https://img.shields.io/badge/Java%2017-007396?style=flat&logo=openjdk&logoColor=white) ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=black) ![Cloud Firestore](https://img.shields.io/badge/Cloud%20Firestore-FFA000?style=flat&logo=firebase&logoColor=white) ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white) ![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=flat&logo=googlemaps&logoColor=white) ![Gemini](https://img.shields.io/badge/Gemini%202.5%20Flash-8E75B2?style=flat&logo=googlegemini&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white)

## Overview

LifeSaver is a native Android application built in Java that puts blood donors, patients, and NGOs/hospitals on one platform. The idea is simple: in an emergency, finding a matching donor fast is hard, NGOs don't have a shared place to advertise camps, and donors often don't even know when they're next allowed to give. LifeSaver tries to close that gap with a role-based app where patients post requests, organisations post camps, and donors discover both — sorted by how close they actually are.

The app runs on Firebase for auth and real-time data, keeps a local SQLite copy so lists still render offline, plots camps on Google Maps with distance sorting done client-side, and ships a Gemini-powered assistant that only answers blood-donation questions. It's a college project, complete and feature-wide rather than production-deployed, and the Java code is a port of an earlier JavaScript prototype (you can still see references to `helpers.js` and `gemini.js` in the source comments).

## Key Features

- **Three account roles** — Donor, Patient, and NGO/Hospital, each with its own flow, chosen at registration.
- **Email/password authentication** via Firebase Auth, with the user profile persisted to Firestore at `/users/{uid}`.
- **Blood camp discovery** in both a list and a Google Maps view, sorted by Haversine distance from the user. Donors can register for any active camp.
- **Blood request workflow** — patients create requests with a priority level (Normal / Urgent / Emergency), units needed, hospital, and required date. Other users browse requests filtered by blood group and respond, which increments the request's response counter.
- **Donation tracker** (donors only) — every donation is logged to both Firestore and the local SQLite cache, and the app computes the next eligible donation date from medical interval rules.
- **Blood-group compatibility logic** — given a donor or recipient group, the app resolves who can give to whom (the standard ABO/Rh compatibility matrix).
- **Push notifications** through Firebase Cloud Messaging, handled by a dedicated messaging service for matching requests and nearby camps.
- **Awareness centre** — educational facts and myth-vs-fact cards, plus a chatbot.
- **Gemini AI assistant** — a domain-restricted chatbot that only responds to blood-donation, transfusion, blood-type, eligibility, and hospital topics, and politely declines anything off-topic.
- **Offline-friendly** — the SQLite layer mirrors the camps, requests, and donations data so screens still populate without a connection.

## How It Works

The app follows a three-tier client-server design: a presentation layer of Activities/Fragments, a service layer of manager classes that hold the business logic, and a data layer split across Firebase, SQLite, and the Google Maps/Gemini APIs. UI talks to a manager, the manager talks to Firebase or SQLite, and updates flow back to the UI.

### Presentation layer

A single-Activity-per-screen model with a bottom-navigation host. `SplashActivity` leads into `AuthActivity` (login/register), then `MainActivity` hosts five fragments through a `BottomNavigationView`: Home, Find Blood, Become Donor, Tracker, and Awareness. Detail screens (`BloodCampDetailActivity`, `BloodRequestDetailActivity`, `ProfileActivity`, `MapViewActivity`, `ChatBotActivity`) sit above it. Lists are `RecyclerView`s with one adapter each (camps, requests, chat messages, donation history, facts, myths). The UI uses Material Design 3 and ViewBinding.

### Service layer (managers)

The logic is isolated in plain Java manager classes so the Activities stay thin:
- `AuthManager` — Firebase Auth sign-in/registration and session.
- `FirestoreManager` — all Cloud Firestore reads/writes for users, camps, requests, and donations.
- `LocationHelper` — device location via Play Services, feeding the distance sorting.
- `NotificationHelper` + `LifeSaverMessagingService` — local notification channels and FCM message handling.
- `GeminiClient` — a singleton wrapper around the Gemini model.

### Data layer

- **Cloud Firestore** is the source of truth, with four collections: `users`, `blood_camps`, `blood_requests`, and `donations`.
- **SQLite** (`DatabaseHelper` + `DatabaseContract`, DB version 1, `lifesaver.db`) mirrors camps, requests, and donations into three local tables for offline display.
- **Firebase Cloud Messaging** delivers pushes to a default notification channel declared in the manifest.
- **Google Maps SDK** renders the camp map; **Play Services Location** provides the current position.

### Distance and direction (`DistanceUtils`)

Distances are computed client-side with the Haversine formula over an Earth radius of 6371 km, rounded to one decimal place, and formatted adaptively (metres under 1 km, one-decimal km under 10 km, whole km above). The same util also computes a 0–360° bearing between two points and maps it to a compass direction (North, Northeast, …), so camps can be described by direction, not just distance.

### Donation eligibility (`DonationEligibility`)

When a donor logs a donation, the tracker computes whether they can donate again and, if not, the exact next-eligible date. Intervals follow medical safety guidance per donation type:

```
Whole Blood   →  56 days  (8 weeks)
Platelets     →   7 days  (1 week)
Plasma        →  28 days  (4 weeks)
Red Cells     → 112 days  (16 weeks)
```

The result carries `eligible`, `nextEligibleDate`, `daysSinceLastDonation`, and `daysUntilEligible`, so the Tracker screen can show a live countdown.

### Gemini assistant (`GeminiClient`)

The chatbot is a singleton wrapping `gemini-2.5-flash` through the Google Generative AI Java SDK (`GenerativeModelFutures`). Generation config is fixed at temperature 0.7, topK 40, topP 0.95, and a 1024-token cap. A hard-coded system prompt scopes it strictly to blood donation, transfusion, blood types/compatibility, eligibility, app features, and related health topics — anything else (sports, weather, politics, general knowledge) gets declined and redirected. The API key is injected at build time via `BuildConfig.GEMINI_API_KEY` and never committed.

## Tech Stack

- **Language:** Java 17 (100% of the codebase, ~127 KB)
- **Platform:** Android (compileSdk 34, targetSdk 34, minSdk 24), AndroidX + Material Design 3, ViewBinding
- **Build:** Gradle (Android Gradle Plugin), `com.google.gms.google-services` plugin
- **Backend / services:** Firebase Auth, Cloud Firestore, Firebase Cloud Messaging (via Firebase BoM 33.3.0)
- **Maps / location:** Google Maps SDK 19.0.0, Play Services Location 21.3.0
- **Local storage:** SQLite via `SQLiteOpenHelper`
- **AI:** Google Generative AI SDK 0.9.0 (`gemini-2.5-flash`) with kotlinx-coroutines
- **Networking:** Retrofit 2.11 + OkHttp 4.12 + Gson; Glide 4.16 for images

## Getting Started

### Prerequisites

- Android Studio (Hedgehog or newer) with JDK 17
- An Android device or emulator on API 24+
- A Firebase project, a Google Maps API key, and a Gemini API key

### Installation

```bash
git clone https://github.com/DCode-v05/Blood-Donation-App.git
cd Blood-Donation-App
```

Open the folder in Android Studio (`File → Open`) and let Gradle sync — it pulls every dependency declared in `app/build.gradle`.

### Configure API keys

```bash
cp local.properties.template local.properties
```

Then edit `local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

`MAPS_API_KEY` is injected into the manifest via `manifestPlaceholders`; `GEMINI_API_KEY` becomes a `BuildConfig` field. Neither key is committed.

### Set up Firebase

1. Create a project at the Firebase Console.
2. Add an Android app with package `com.lifesaver.blooddonation`.
3. Download `google-services.json` and drop it into `app/` (a `.template` is checked in as a placeholder).
4. Enable **Authentication (Email/Password)**, **Firestore Database**, and **Cloud Messaging**.

### Build and run

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Or press **Run** in Android Studio with a connected device/emulator.

## Usage

- Register as a Donor, Patient, or NGO/Hospital.
- **Donors:** browse nearby camps on Home or the Map, register for one, and log each donation in Tracker — the app then tells you exactly when you're next eligible.
- **Patients:** post a blood request with a priority level; compatible donors see it under Find Blood and can respond.
- **NGOs/Hospitals:** post and manage active blood camps for donors to discover.
- **Awareness tab:** read facts and myth-vs-fact cards, or ask the LifeSaver assistant any blood-donation question.

The app requests location permission (for distance sorting) and notification permission (for FCM pushes) at runtime.

## Project Structure

```
Blood-Donation-App/
├── build.gradle                    # top-level Gradle config
├── settings.gradle
├── gradle.properties
├── local.properties.template       # copy → local.properties, add API keys
└── app/
    ├── build.gradle                # app-module config, dependencies, key injection
    ├── proguard-rules.pro
    ├── google-services.json.template
    └── src/main/
        ├── AndroidManifest.xml      # permissions, activities, FCM service, Maps key
        ├── java/com/lifesaver/blooddonation/
        │   ├── LifeSaverApp.java    # Application class (notification channel setup)
        │   ├── activities/          # Splash, Auth, Main, 5 detail/feature screens
        │   ├── fragments/           # Home, FindBlood, BecomeDonor, Tracker, Awareness
        │   ├── adapters/            # 6 RecyclerView adapters
        │   ├── models/              # User, BloodCamp, BloodRequest, Donation, GeoLocation
        │   ├── managers/            # Auth, Firestore, Location, Notification, FCM service
        │   ├── database/            # SQLite DatabaseHelper + DatabaseContract
        │   ├── utils/               # DistanceUtils (Haversine), BloodCompatibility,
        │   │                        #   DonationEligibility, ValidationUtils, DateUtils
        │   ├── constants/           # BloodGroups (compatibility matrix), AppConstants
        │   └── ai/                  # GeminiClient
        └── res/
            ├── layout/              # activity_*, fragment_*, item_*, dialog_*
            ├── menu/                # bottom_nav_menu, main_toolbar_menu
            ├── drawable/            # icons, priority chips, gradients
            ├── values/              # colors, strings, dimens, themes
            ├── mipmap-anydpi-v26/   # adaptive launcher icon
            └── xml/                 # backup + data-extraction rules
```

---

## Contact

**Portfolio:** [Denistan](https://www.denistan.me)<br>
**LinkedIn:** [Denistan](https://www.linkedin.com/in/denistanb)<br>
**GitHub:** [DCode-v05](https://github.com/DCode-v05)<br>
**LeetCode:** [Denistan_B](https://leetcode.com/u/Denistan_B)<br>
**Email:** [denistanb05@gmail.com](mailto:denistanb05@gmail.com)

Made with ❤️ by **Denistan B**
