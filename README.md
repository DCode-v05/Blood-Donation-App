# Blood Donation App — LifeSaver

## Project Description
LifeSaver is a native Android application that connects blood donors, patients, and NGOs on a single platform. Built with Java in Android Studio and powered by Firebase, the app helps users discover nearby blood camps, post and respond to urgent blood requests, and track personal donation history with medically accurate eligibility intervals. A topic-restricted Gemini AI assistant answers blood-donation queries in real time, and Google Maps integration plots camps with Haversine-based distance sorting.

---

## Project Details

### Problem Statement
Around 38% of the population is medically eligible to donate blood, yet only 3% donate annually. In emergencies, patients struggle to locate matching donors quickly, NGOs lack a unified platform to broadcast camps, and donors themselves don't know when or where they can next donate. LifeSaver addresses this gap with a mobile-first, role-based platform that brings donors, patients, and healthcare organisations into one place.

### System Design / Architecture
- **Three-tier client-server architecture.**
- **Presentation Layer:** Activities, Fragments, Material XML layouts, RecyclerViews, BottomNavigationView, and Google Maps SDK.
- **Service Layer:** Java service classes — `AuthManager`, `FirestoreManager`, `LocationHelper`, `NotificationHelper`, `GeminiClient` — encapsulate all business logic.
- **Data Layer:** Firebase Authentication, Cloud Firestore (NoSQL), SQLite (offline cache), Firebase Cloud Messaging, Google Maps API.
- **Flow:** UI Activity → Service Class → Firebase / SQLite → real-time updates back to UI.

### Core Modules & Features
- **Role-Based Authentication**
  - Three account types: Donor, Patient, NGO/Hospital.
  - Email/password sign-in with Firebase Auth.
  - User profile persisted in Firestore `/users/{uid}`.
- **Blood Camp Discovery**
  - List + Map views, sorted by Haversine distance.
  - Donors can register for any active camp.
- **Blood Request Workflow**
  - Patients create requests with priority (Normal / Urgent / Emergency).
  - Browse requests filtered by blood group; respond increments the request counter.
- **Donation Tracker (Donors only)**
  - Logs every donation to both Firestore and local SQLite.
  - Auto-computes the next eligible donation date using medical intervals.
- **Push Notifications**
  - Firebase Cloud Messaging via `LifeSaverMessagingService`.
- **Awareness Centre**
  - Educational facts and myth-vs-fact cards.
- **Gemini AI Assistant**
  - Domain-restricted chatbot scoped to blood / donation / hospital topics only.

### Donation Eligibility Logic
The tracker computes the next eligible donation date from the last donation and donation type. Intervals follow medical safety guidelines:
```
Whole Blood   →  56 days  (8 weeks)
Platelets     →   7 days  (1 week)
Plasma        →  28 days  (4 weeks)
Red Cells     → 112 days  (16 weeks)
```

### Data Model (Firestore Collections)
- **`users`** — uid, email, fullName, phone, role, bloodGroup, location, isAvailable, organizationName, organizationType, pushToken, createdAt, updatedAt.
- **`blood_camps`** — name, organizer, date, startTime, endTime, address, location {lat, lng}, contactNumber, status, registeredDonors[], registrationCount.
- **`blood_requests`** — patientName, bloodGroup, unitsNeeded, priority, hospital, contactNumber, requiredDate, createdBy, status, responses.
- **`donations`** — donorId, donorName, donorBloodGroup, date, location, units, type, organization, notes.

### UI Screens
- Splash → Auth (Login / Register)
- Main (Bottom Nav host): Home · Find Blood · Become Donor · Tracker · Awareness
- Detail screens: Blood Camp Detail, Blood Request Detail, Profile, Map View, ChatBot

### Mobile Application
The Android app provides:
- Role-based signup with three account types
- Map and list view of nearby camps with distance sorting
- Create / browse blood requests with blood-group filter
- Donor profile with availability toggle
- Donation history with live next-eligible-date countdown
- Awareness section with facts, myths, and AI chatbot
- Push notifications for matching requests and nearby camps

---

## Tech Stack
- Android Studio (Hedgehog or newer)
- Java 17, Android Gradle Plugin 8.5
- AndroidX, Material Design 3
- Firebase (Auth, Firestore, Cloud Messaging) via Firebase BoM
- Google Maps SDK & Play Services Location
- SQLite (`SQLiteOpenHelper`)
- Retrofit + OkHttp (networking)
- Glide (image loading)
- Google Generative AI SDK (Gemini)
- Gradle, RecyclerView, ViewBinding

---

## Getting Started

### 1. Clone the repository
```
git clone https://github.com/DCode-v05/Blood-Donation-App.git
cd Blood-Donation-App
```

### 2. Open in Android Studio
```
File → Open → choose the Blood-Donation-App/ folder
```
Let Gradle sync automatically (it will download all dependencies declared in `app/build.gradle`).

### 3. Configure API keys
Copy the template and fill in real values:
```
cp local.properties.template local.properties
```
Edit `local.properties`:
```
sdk.dir=/path/to/Android/Sdk
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

### 4. Set up Firebase
1. Create a project at https://console.firebase.google.com
2. Add an Android app with package `com.lifesaver.blooddonation`
3. Download `google-services.json` and place it in `app/`
4. In Firebase Console enable: **Authentication (Email/Password)**, **Firestore Database**, and **Cloud Messaging**

### 5. Build & run
```
./gradlew assembleDebug
./gradlew installDebug
```
Or simply press **Run ▶** in Android Studio with a connected device/emulator.

---

## Usage
- Launch the app and register as Donor, Patient, or NGO.
- **Donors:** browse nearby camps on Home/Map, register for a camp, and log every donation in Tracker. The app automatically tells you when you're next eligible.
- **Patients:** post a blood request with a priority level — compatible donors will see it in Find Blood.
- **NGOs/Hospitals:** post and manage active blood camps for donors to discover.
- **Awareness tab:** browse facts and myth-vs-fact cards, or ask the LifeSaver AI any blood-donation question.

---

## Project Structure
```
Blood-Donation-App/
│
├── build.gradle                  # top-level Gradle config
├── settings.gradle
├── gradle.properties
├── local.properties.template     # copy → local.properties, fill API keys
├── .gitignore
└── app/
    ├── build.gradle              # app-module Gradle config
    ├── proguard-rules.pro
    ├── google-services.json.template
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/lifesaver/blooddonation/
        │   ├── LifeSaverApp.java
        │   ├── activities/       # Splash, Auth, Main, Detail, Profile, Map, ChatBot
        │   ├── fragments/        # Home, FindBlood, BecomeDonor, Tracker, Awareness
        │   ├── adapters/         # RecyclerView adapters
        │   ├── models/           # User, BloodCamp, BloodRequest, Donation, GeoLocation
        │   ├── managers/         # AuthManager, FirestoreManager, LocationHelper, NotificationHelper, FCM service
        │   ├── database/         # SQLite DatabaseHelper + contract
        │   ├── utils/            # Distance, BloodCompatibility, DonationEligibility, Validation, Date
        │   ├── constants/        # BloodGroups, AppConstants
        │   └── ai/               # GeminiClient
        └── res/
            ├── layout/           # activity_*, fragment_*, item_*, dialog_*
            ├── menu/             # bottom_nav_menu, main_toolbar_menu
            ├── drawable/         # icons, gradients, chip backgrounds
            ├── values/           # colors, strings, dimens, themes
            ├── mipmap-anydpi-v26/# adaptive launcher icon
            └── xml/              # backup rules
```

---

## Contributing

Contributions are welcome! To contribute:
1. Fork the repository
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Open a pull request describing your changes.

---

## Contact
- **GitHub:** [DCode-v05](https://github.com/DCode-v05)
- **Email:** denistanb05@gmail.com
