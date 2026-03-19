# PixelWatch

**Location:** `~/code/PixelWatch`

**Purpose:** Android/WearOS heart rate monitoring application with dual implementations (.NET MAUI + native WearOS).

## Tech Stack

- **Framework:** .NET MAUI + Android/WearOS native
- **Target:** net10.0-android
- **Min SDK:** Android 21+

## Project Structure

```
PixelWatch/
├── PixelWatch.sln          # Solution file
├── Maui/                   # .NET MAUI app
│   ├── WearHrMaui.csproj
│   └── Resources/
├── AndroidApp1/            # WearOS native app
│   ├── Poly.WearOS.csproj
│   ├── MainActivity.cs
│   ├── HeartRateService.cs
│   └── Services/
└── MyApplication/          # Legacy/alternative
```

## Entry Points

- `PixelWatch.sln` — Visual Studio solution
- `Maui/WearHrMaui.csproj` — MAUI project
- `AndroidApp1/Poly.WearOS.csproj` — Native WearOS project

## Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| CommunityToolkit.Mvvm | 8.2.2 | MVVM framework |

## Build & Run

```bash
# Build solution
dotnet build PixelWatch.sln

# Deploy via Visual Studio or ADB
```

## Notes

- Two implementations: MAUI app + native WearOS
- Heart rate monitoring service for WearOS
- Requires Android SDK and Visual Studio
- Consider consolidating to single approach