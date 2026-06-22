# Valtio

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange.svg)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.06-blue.svg)](https://developer.android.com/compose)

**Digital vault for warranties, invoices and purchase documents.**  
Never lose a warranty again. Keep all your product guarantees, receipts, and documents in one secure place.

---

## ✨ Features

- 🔧 **Product registration** with detailed information (name, brand, model, category, store, price)
- 📸 **Photo & document attachment** for each product
- ⏱️ **Automatic warranty tracking** — calculates expiration dates and remaining days
- 🔔 **Smart notifications** — alerted 90, 30, 7 days and on the last day before warranty expires
- 📊 **Dashboard** with summary of active, expiring, and expired warranties
- 🔍 **Powerful search & filters** by name, brand, store, warranty status
- 📄 **Export to PDF & CSV** — portable, shareable reports
- 🌍 **7 languages** — Portuguese, English, Spanish, French, German, Italian, Dutch

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt (Dagger) |
| **Database** | Room (SQLite) |
| **Background** | WorkManager |
| **Navigation** | Navigation Compose |
| **Image Loading** | Coil |
| **PDF Generation** | iText 7 |
| **Preferences** | DataStore |

---

## 🚀 Build Instructions

### Prerequisites
- **JDK 17** (Eclipse Adoptium recommended)
- **Android SDK 34**
- **Android Studio** Hedgehog (2023.1+) or newer

### Steps
```bash
# Clone the repository
git clone https://github.com/halexys-dotcom/Valtio.git
cd Valtio

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing key)
./gradlew assembleRelease
```

The debug APK will be available at: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🌍 Supported Languages

| Language | Locale |
|----------|--------|
| 🇵🇹 Portuguese | pt-PT |
| 🇬🇧 English | en-US |
| 🇪🇸 Spanish | es-ES |
| 🇫🇷 French | fr-FR |
| 🇩🇪 German | de-DE |
| 🇮🇹 Italian | it-IT |
| 🇳🇱 Dutch | nl-NL |

---

## 📄 License

This project is licensed under the **GNU General Public License v3.0**.  
See the [LICENSE](LICENSE) file for details.

```
Valtio — Digital warranty manager
Copyright (C) 2026 HAConnect

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
```

---

## 🛡️ F-Droid

This app will be available on F-Droid — the free and open source Android app store.  
F-Droid metadata is maintained in the `fastlane/metadata/android/` directory.