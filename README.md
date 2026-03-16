# Rick and Morty Wiki

Android application for viewing information about characters, locations, and episodes from the "Rick and Morty" animated series. The main goal of the project is to master the skills of working with API, paging and lists in Jetpack Compose

## Architecture

The project is built using **MVVM** (Model-View-ViewModel) pattern combined with **Clean Architecture**, ensuring clear separation of concerns between layers:

- **UI Layer** (app module) — responsible for data display and user interface using Jetpack Compose
- **Domain Layer** (domain module) — contains business logic and data models
- **Data Layer** (data module) — manages data fetching from network and local storage

## Key Libraries

### UI
- **Jetpack Compose** — modern declarative UI framework
- **Material 3** — design system from Google

### Networking
- **Ktor Client** — HTTP client for API communication

### Local Storage
- **Room** — library for SQLite database operations

### Dependency Injection
- **Hilt** — dependency injection framework

### Image Loading
- **Coil** — library for image loading and caching

### Pagination
- **Paging 3** — library for paginated data loading

### Other
- **Kotlin Serialization** — JSON data serialization
- **Navigation Compose** — navigation between screens
- **Coroutines** — asynchronous programming

## API

The application uses the public [Rick and Morty API](https://rickandmortyapi.com/), which provides:

- **Characters** — information about all characters in the series
- **Locations** — information about all places in the universe
- **Episodes** — information about all episodes

## Module Structure

```
app/          — UI layer (Compose screens, ViewModels)
domain/       — Domain layer (models, repositories)
data/         — Data layer (network services, Room database, repositories)
```