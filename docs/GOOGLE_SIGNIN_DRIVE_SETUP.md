# TheSlate: Google Sign-In + Google Drive API Setup (Android)

This guide gives you the exact setup sequence to enable:

1. **Google Sign-In** (Signup/Login with Google)
2. **Google Drive API** for note **storage** (upload) and **retrieval** (download/list)

---

## 1) Create and configure a Google Cloud project

1. Open [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project (example: `TheSlate`).
3. Go to **APIs & Services → Library**.
4. Enable:
   - **Google Drive API**
5. Go to **OAuth consent screen**:
   - User Type: **External** (or Internal for Workspace).
   - App name: `TheSlate`
   - Support email + developer email
   - Add scopes later (Drive scopes + basic profile/email)
   - Add test users while app is in testing mode

---

## 2) Configure OAuth client for Android (Google Sign-In)

You need OAuth clients for both Android and (recommended) Web.

### A. Android OAuth client

1. Open **APIs & Services → Credentials → Create Credentials → OAuth client ID**.
2. Application type: **Android**.
3. Provide:
   - Package name (example: `com.theslate.app`)
   - SHA-1 certificate fingerprint (debug and release each require registration)

Get SHA-1 values:

```bash
./gradlew signingReport
```

Use `Variant: debug` during development and later add release SHA-1.

### B. Web OAuth client (recommended)

Create another OAuth client ID:

1. **Create Credentials → OAuth client ID**
2. Type: **Web application**
3. Keep the generated **Web client ID**; Android uses this as server client id in sign-in config for tokens.

---

## 3) Firebase project linkage (recommended for easier Android setup)

Although Drive API is from Google Cloud, Firebase simplifies Android credential delivery.

1. Open [Firebase Console](https://console.firebase.google.com/)
2. Create project (or link to existing GCP project)
3. Add Android app using the same package name.
4. Add SHA-1 (and SHA-256 if requested)
5. Download `google-services.json`
6. Place at:

```text
app/google-services.json
```

---

## 4) Android dependencies and Gradle setup

Add Google services plugin and required libraries.

### Project-level `build.gradle.kts`

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

### App-level `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

dependencies {
    // Google Sign-In / Identity
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Google Drive API (REST)
    implementation("com.google.api-client:google-api-client-android:2.7.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20240815-2.0.0")
    implementation("com.google.http-client:google-http-client-gson:1.44.2")
}
```

Sync Gradle.

---

## 5) Request required OAuth scopes

For profile + Drive file access:

- `Scopes.EMAIL`
- `Scopes.PROFILE`
- `https://www.googleapis.com/auth/drive.file`

`drive.file` is recommended minimum scope (access to app-created/opened files).

If you need broader access, use:

- `https://www.googleapis.com/auth/drive`

(Use only if necessary due to stricter verification requirements.)

---

## 6) Implement Google Sign-In flow in app

Typical flow (Compose + MVVM):

1. User taps **Sign in with Google**
2. Launch Google sign-in intent
3. Parse `GoogleSignInAccount`
4. Store in local DB/DataStore:
   - Name
   - Email
   - Photo URL
   - Google account id
5. Navigate to Dashboard

Build sign-in options with Drive scope:

```kotlin
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestEmail()
    .requestProfile()
    .requestScopes(
        Scope(DriveScopes.DRIVE_FILE)
    )
    .requestIdToken(WEB_CLIENT_ID)
    .build()
```

Then:

```kotlin
val signInClient = GoogleSignIn.getClient(context, gso)
val intent = signInClient.signInIntent
// launch intent and handle result
```

Handle result:

```kotlin
val task = GoogleSignIn.getSignedInAccountFromIntent(data)
val account = task.getResult(ApiException::class.java)
```

---

## 7) Build a Drive service from signed-in account

Use `GoogleAccountCredential` with selected account:

```kotlin
val credential = GoogleAccountCredential.usingOAuth2(
    context,
    listOf(DriveScopes.DRIVE_FILE)
)
credential.selectedAccount = account.account

val driveService = Drive.Builder(
    NetHttpTransport(),
    GsonFactory.getDefaultInstance(),
    credential
).setApplicationName("TheSlate").build()
```

Keep this in a data-layer class (e.g., `DriveRemoteDataSource`) injected by Hilt.

---

## 8) Create TheSlate folder in Drive

On first sync, find or create app folder:

1. Query for folder:
   - name = `TheSlate`
   - mimeType = `application/vnd.google-apps.folder`
   - trashed = false
2. If not found, create folder
3. Save folder id locally for faster subsequent uploads

---

## 9) Upload notes as JSON (storage)

For each note:

1. Convert note model to JSON (`kotlinx.serialization` / Gson)
2. File naming strategy:
   - `note_<noteId>.json`
3. Upload into TheSlate folder using Drive Files API
4. Save returned `driveFileId` locally
5. Mark `isSynced = true` when successful

If upload fails:

- Keep local note
- mark `isSynced = false`
- enqueue WorkManager retry

---

## 10) Retrieve notes from Drive (download/list)

At sync pull time:

1. List files from TheSlate folder (`mimeType='application/json'`)
2. For each file:
   - download content
   - parse JSON to note object
3. Conflict rule: **updatedAt latest timestamp wins**
4. Upsert into Room

Use paging/batching if note count grows.

---

## 11) WorkManager integration for offline-first sync

1. Add a `SyncNotesWorker` with network constraint:
   - `NetworkType.CONNECTED`
2. On local save/update:
   - Write note to Room
   - Add row to sync queue if unsynced
3. Worker processes queue:
   - push pending notes
   - optionally pull remote updates
4. On success update `isSynced = true`

Recommended:

- Exponential backoff
- Unique work name to prevent duplicate workers

---

## 12) Handle token expiry and revoked permissions

If Drive calls return auth errors:

1. Clear cached account session
2. Prompt user to sign in again
3. Re-request scopes

Also support logout:

```kotlin
GoogleSignIn.getClient(context, gso).signOut()
```

And optionally revoke:

```kotlin
GoogleSignIn.getClient(context, gso).revokeAccess()
```

---

## 13) AndroidManifest requirements

Ensure these permissions/components are declared as needed:

- Internet permission
- Optional boot receiver for alarm reschedule (`BOOT_COMPLETED`)
- Notification permission on Android 13+

Drive API itself does not require storage permission when using app files through pickers/internal URIs.

---

## 14) Verification checklist

Before production release:

1. Debug + Release SHA-1 both added in Google credentials
2. OAuth consent screen published/verified as required
3. Test cases:
   - First sign-in
   - Sign-out/sign-in
   - Offline save then auto-sync
   - Conflict overwrite by latest `updatedAt`
   - Reinstall app and relink Drive
4. Ensure no secrets/client ids are hardcoded insecurely

---

## 15) Common errors and fixes

- **`DEVELOPER_ERROR (10)`**
  - Wrong SHA-1 or package name mismatch
- **`ApiException: 12500`**
  - OAuth consent setup incomplete / wrong client id
- **`403 insufficient permissions`**
  - Drive scope not requested or not granted
- **`UserRecoverableAuthIOException`**
  - Need user re-consent flow

---

## Suggested implementation order in TheSlate

1. Google Sign-In working end-to-end
2. Persist profile info and session
3. Build Drive folder create/find
4. Upload one sample note JSON
5. List and download notes
6. Integrate with Room + WorkManager sync queue
7. Add conflict resolution + retries + notifications

