# Privacy Policy

**Last updated:** June 22, 2026

## Valtio — Digital Warranty Manager

Valtio is developed by **HAConnect**. This privacy policy applies to the Valtio Android application, available on F-Droid, Aptoide, and other app stores.

---

## TL;DR — No Data Collection

**Valtio does not collect, store, transmit, or share any personal data.**

All your data (products, warranties, documents, photos, preferences) is stored **exclusively on your device** in a local database (Room/SQLite). There are no analytics, no crash reporters, no ad networks, and no third-party services integrated into the app.

---

## Data Storage

| Data Type | Where It's Stored |
|-----------|-------------------|
| Product information (name, brand, price, etc.) | Local Room database on device |
| Photos and documents attached to products | App-specific storage on device |
| Language and theme preferences | Local DataStore on device |
| Warranty expiration alerts | WorkManager (device-only, offline) |

All data remains on your device. You can delete it at any time by uninstalling the app or clearing app data from Android Settings.

---

## Permissions

Valtio requests the following Android permissions, **solely for app functionality**:

| Permission | Purpose |
|------------|---------|
| `POST_NOTIFICATIONS` | To send warranty expiration alerts |
| `CAMERA` | To take photos of products (only when you choose to) |
| `READ_MEDIA_IMAGES` | To attach existing photos to products |

No permission is used for tracking, advertising, or data collection. All permissions are optional and requested only when you use the corresponding feature.

---

## Third-Party Services

**None.** Valtio does not integrate any third-party services, SDKs, analytics, ad networks, or crash reporting tools.

The app uses only open-source libraries from the Android ecosystem (Jetpack Compose, Room, Hilt, WorkManager, iText, Coil — see `build.gradle.kts` for the full list). None of these libraries transmit data off-device.

---

## Internet Access

Valtio **does not require internet access**. The app functions fully offline. There is no networking code in the application.

---

## Children's Privacy

Valtio does not collect any data from anyone, including children under 13.

---

## GDPR Compliance

Valtio is fully compliant with the **General Data Protection Regulation (GDPR)** because:

- No personal data is collected or processed
- No data leaves the user's device
- Users retain full control over their data at all times

---

## Changes to This Policy

If this policy changes, the updated version will be published at:

**https://github.com/halexys-dotcom/Valtio-Warranty-Manager/blob/main/PRIVACY_POLICY.md**

---

## Contact

For questions about this privacy policy, please contact:

📧 **hah_correia@hotmail.com**

---

**HAConnect**  
Valtio is free and open-source software licensed under the [GNU General Public License v3.0](LICENSE).