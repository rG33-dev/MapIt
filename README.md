# MapIt 🗺️

MapIt is a modern Android mapping application built with Kotlin and Jetpack Compose, focused on interactive maps, real-time location updates, and location-based points of interest.

The project explores practical implementation of Google Maps integration, location services, runtime permissions, network-state handling, and responsive map-based UI.

<img width="1280" height="853" alt="photo_2026-08-13_17-09-51" src="https://github.com/user-attachments/assets/f35da109-8c76-4fa1-b3af-fd7cb798d002" />


## ✨ Features

- 🗺️ Interactive Google Maps
  - Integrated Google Maps into a Jetpack Compose-based Android application.
  - Provides an interactive map experience with map controls and location-based content.

- 📍 Real-Time Location
  - Retrieves and updates the user's location in real time.
  - Handles location access through Android runtime permissions.

- 📌 Map Markers & POIs
  - Displays location-based markers and points of interest on the map.
  - Supports dynamic map content based on location data.

- 🌐 Network Awareness
  - Monitors connectivity and network state.
  - Handles application behavior based on network availability.

- 🎨 Responsive UI
  - Built using Jetpack Compose.
  - Uses modern Android UI development practices for a responsive map-based interface.

---

## 🛠️ Tech Stack

| Category | Technologies |
|----------|-------------|
| Language | Kotlin |
| Platform | Android |
| UI | Jetpack Compose |
| Maps | Google Maps SDK |
| Location | Android Location APIs |
| Architecture | Modern Android Architecture |
| Permissions | Android Runtime Permissions |
| Networking | Network / Connectivity APIs |
| Build | Gradle |
| IDE | Android Studio |

---

## 🏗️ Architecture & Development

MapIt follows modern Android development practices with a focus on maintainable application structure and lifecycle-aware components.

Key implementation areas include:

- Jetpack Compose UI
- Google Maps integration
- Real-time location updates
- Runtime location permissions
- Map markers and POI management
- Network connectivity awareness
- Lifecycle-aware location handling
- Responsive map-based UI

---

## 🗺️ Google Maps Integration

The application integrates the Google Maps SDK to provide an interactive map experience.

MapIt uses the map to:

- Display the user's location
- Render map markers
- Display points of interest
- Respond to location updates
- Provide an interactive map interface

---

## 📍 Location Handling

MapIt implements real-time location functionality using Android location services.

The application handles:

1. Location permission requests
2. Permission state changes
3. Location updates
4. Location-based UI updates
5. Map positioning based on the user's location

Location access is requested through Android's runtime permission system rather than assuming permission is available.

---

## 🌐 Network & Connectivity

Map-based functionality can depend on network availability, so MapIt includes connectivity and network-state awareness.

This allows the application to detect network changes and adapt its behavior accordingly.

---



## 🚀 Getting Started

### Prerequisites

- Android Studio
- Android SDK
- Kotlin
- Gradle
- Android device or emulator
- Google Maps API key

### Clone the Repository

`bash
git clone <your-repository-url>
cd MapIt
