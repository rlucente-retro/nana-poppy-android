# Nana & Poppy Android App

The **Nana & Poppy** app is a personalized Android application designed to provide daily greetings and information for grandparents using the recorded voices of their grandchildren. The app plays a sequence of messages—including the current date, time, and weather for two locations—randomly selecting a different grandchild's voice for each segment of the greeting.

## Features

- **Personalized Voice Messages:** Plays audio clips of grandchildren speaking the time, date, and weather.
- **Dynamic Greeting:** Greets Nana and Poppy with "Good morning", "Afternoon", etc., based on the current time of day.
- **Real-time Weather:** Fetches and speaks the current temperature for two configurable locations using the OpenWeatherMap API.
- **Remote Content Sync:** Downloads and updates grandchild audio clips from a remotely hosted ZIP file.
- **Secure Configuration:** Uses encrypted shared preferences to store sensitive information like API keys.

---

## Prerequisites

Before building and configuring the app, you will need:

1.  **OpenWeatherMap API Key:** A free or paid API key from [OpenWeatherMap](https://openweathermap.org/api).
2.  **Audio Hosting:** A URL pointing to a publicly accessible (or direct-download) ZIP file containing the grandchild audio clips.
3.  **Grandchild Audio Clips:** Recorded MP3 files for each phrase (see the [Audio Preparation Guide](#audio-preparation-guide) below).

---

## Getting Started

### 1. Build & Install

1.  Clone the repository.
2.  Open the project in **Android Studio**.
3.  Ensure you have the Android SDK 34 (Upside Down Cake) installed.
4.  Build the project using Gradle:
    ```bash
    ./gradlew assembleDebug
    ```
5.  Install the APK on an Android device (Minimum SDK 26 / Android 8.0+):
    ```bash
    ./gradlew installDebug
    ```

### 2. Testing

Run unit tests to verify the message generation and logic:
```bash
./gradlew test
```

---

## Configuration

Once the app is installed, you must configure it via the **Settings** menu:

1.  Open the app and tap the **Settings** icon (gear) in the top right.
2.  **OpenWeatherMap API Key:** Enter your OWM API key.
3.  **Audio ZIP URL:** Enter the direct download URL for your audio clips (e.g., `https://example.com/audio.zip`).
4.  **Location Queries:**
    *   **Location 1:** Enter the city name/query for the primary location (default: "Waynesboro,PA,US").
    *   **Location 2:** Enter the city name/query for the secondary location (default: "Ocean City,MD,US").
5.  **Save & Sync:**
    *   Tap **Save** to store your configuration.
    *   Tap **Sync Audio** to download and unzip the audio clips. The app will notify you when the sync is complete.

---

## Audio Preparation Guide

To fully personalize the app, you need to gather and organize audio clips for each grandchild.

### Directory Structure

The ZIP file must contain a directory for each child. Each directory should contain the required MP3 files. For example:

```text
audio.zip
├── owen/
│   ├── good.mp3
│   ├── morning.mp3
│   ├── location1.mp3
│   └── ...
├── piper/
│   ├── good.mp3
│   ├── morning.mp3
│   ├── location1.mp3
│   └── ...
```

### Phrase List

Each child's directory must include the following MP3 files. Filenames must match the phrase exactly (lowercase, underscores instead of spaces) and end in `.mp3`.

| Phrase / Filename | Description / Notes |
| :--- | :--- |
| `good` | "Good" |
| `morning`, `afternoon`, `evening`, `night` | Time of day greetings |
| `nana_and_poppy` | "Nana and Poppy" or whatever terms of endearment the grandchildren use |
| `the_time` | "The time" |
| `today` | "Today" |
| `the_current_temperature_for` | "The current temperature for" |
| `location1` | Name of their primary location (e.g., recorded as "Waynesboro") |
| `location2` | Name of their secondary location (e.g., recorded as "Ocean City") |
| `is`, `and`, `degrees`, `minus` | Connecting words |
| `am`, `pm` | AM/PM markers |
| `january` ... `december` | All 12 months |
| `first` ... `nineteenth` | Ordinal numbers for days |
| `twentieth`, `thirtieth` | Ordinal numbers for days |
| `oh` | Used for minutes (e.g., "four oh five") |
| `one` ... `twenty` | Cardinal numbers |
| `thirty`, `forty`, `fifty`, `sixty` | Cardinal numbers |
| `seventy`, `eighty`, `ninety`, `hundred` | Cardinal numbers |

> **Note:** For a complete list of required filenames, refer to the `phrase-list.txt` file in the project root.

### Recording Tips

- Record in a quiet environment.
- Use the same microphone for consistency.
- Save files as **MP3** format.
- Keep the recordings concise with minimal silence at the beginning and end.

---

## Usage

1.  Ensure you have completed the **Configuration** and **Sync Audio** steps.
2.  On the main screen, tap the **Play** button.
3.  The app will:
    - Determine the current time and weather.
    - Generate a sequence of messages.
    - Randomly pick a child's voice for each part of the message.
    - Play the audio sequence in order.

---

## Contributing

This is a private project, but suggestions and improvements are welcome. Please ensure any new features include corresponding unit tests.

---

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
