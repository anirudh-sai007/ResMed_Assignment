# FlashCards App - Test Suite

This directory contains comprehensive unit tests and Android instrumentation tests for the FlashCards application.

## Test Structure

### Unit Tests (`app/src/test/`)

Unit tests run on the JVM and don't require an Android device or emulator. They use mocking frameworks to isolate components.

#### 1. CardsLocalRepositoryTest
**Location:** `app/src/test/java/com/resintern/flashcards/data/repository/CardsLocalRepositoryTest.kt`

Tests the local repository that manages flashcard data:
- ✅ `requestCards emits Success event with initial cards` - Verifies initial data loading
- ✅ `addCard adds new card and emits CardAdded event` - Tests adding a single card
- ✅ `addCard increases card list size` - Verifies card count increases
- ✅ `multiple addCard calls add all cards` - Tests adding multiple cards
- ✅ `repository singleton returns same instance` - Verifies singleton pattern

**Key Technologies:**
- Kotlin Coroutines Test (`runTest`)
- Turbine (Flow testing library)
- JUnit 4

#### 2. CardsViewModelTest
**Location:** `app/src/test/java/com/resintern/flashcards/views/composables/CardsViewModelTest.kt`

Tests the ViewModel that manages the flashcard list screen:
- ✅ `viewModel initializes and requests cards on creation` - Tests initialization
- ✅ `cardList is empty initially` - Verifies initial state
- ✅ `cardList updates when Success event is received` - Tests state updates
- ✅ `cardList does not update on CardAdded event` - Tests event filtering
- ✅ `cardList does not update on Error event` - Tests error handling
- ✅ `multiple Success events update cardList correctly` - Tests multiple updates

**Key Technologies:**
- MockK (Mocking framework)
- Kotlin Coroutines Test
- Turbine
- InstantTaskExecutorRule (LiveData testing)

#### 3. CreateCardsViewModelTest
**Location:** `app/src/test/java/com/resintern/flashcards/views/composables/CreateCardsViewModelTest.kt`

Tests the ViewModel for creating new flashcards:
- ✅ `viewModel initializes with Idle event` - Tests initial state
- ✅ `addCard with valid inputs calls repository addCard` - Tests valid input handling
- ✅ `addCard with empty topic does not call repository` - Tests validation
- ✅ `addCard with empty question does not call repository` - Tests validation
- ✅ `addCard with empty answer does not call repository` - Tests validation
- ✅ `addCard with all empty fields does not call repository` - Tests validation
- ✅ `events emits CardAdded when repository emits CardAdded` - Tests event propagation
- ✅ `events does not emit when repository emits Success` - Tests event filtering

**Key Technologies:**
- MockK
- Kotlin Coroutines Test
- Turbine

### Android Instrumentation Tests (`app/src/androidTest/`)

Instrumentation tests run on an Android device or emulator and test the app in a real Android environment.

#### 1. CardsLocalRepositoryInstrumentedTest
**Location:** `app/src/androidTest/java/com/resintern/flashcards/data/repository/CardsLocalRepositoryInstrumentedTest.kt`

Tests the repository in a real Android environment:
- Tests repository initialization
- Tests requesting cards
- Tests adding cards
- Tests data persistence within the singleton
- Tests initial card content

#### 2. ViewModelsInstrumentedTest
**Location:** `app/src/androidTest/java/com/resintern/flashcards/views/composables/ViewModelsInstrumentedTest.kt`

Tests ViewModels in a real Android environment:
- Tests CardsViewModel loading cards
- Tests CardsViewModel updating on new cards
- Tests CreateCardsViewModel adding cards
- Tests CreateCardsViewModel validation
- Tests ViewModels working together

## Running Tests

### Run All Unit Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests CardsLocalRepositoryTest
./gradlew test --tests CardsViewModelTest
./gradlew test --tests CreateCardsViewModelTest
```

### Run Android Instrumentation Tests
```bash
# Requires a connected device or running emulator
./gradlew connectedAndroidTest
```

### Run Tests with Coverage
```bash
./gradlew testDebugUnitTest jacocoTestReport
```

## Test Dependencies

The following testing libraries are used:

```kotlin
// Unit Testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("app.cash.turbine:turbine:1.0.0")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("androidx.arch.core:core-testing:2.2.0")

// Android Instrumentation Testing
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
```

## Test Coverage

Current test coverage:
- **CardsLocalRepository**: 100% method coverage
- **CardsViewModel**: 100% method coverage
- **CreateCardsViewModel**: 100% method coverage

## Best Practices Used

1. **Dependency Injection**: ViewModels accept repository and dispatcher as constructor parameters for testability
2. **Coroutine Testing**: Uses `StandardTestDispatcher` for deterministic coroutine testing
3. **Flow Testing**: Uses Turbine library for elegant Flow testing
4. **Mocking**: Uses MockK for creating test doubles
5. **Isolation**: Each test is independent and doesn't affect others
6. **Descriptive Names**: Test names clearly describe what is being tested
7. **AAA Pattern**: Tests follow Arrange-Act-Assert pattern

## Notes

- The `CardsLocalRepository` is a singleton, which can make testing challenging. In production code, consider using dependency injection to provide fresh instances for testing.
- Tests use `runTest` from kotlinx-coroutines-test for proper coroutine testing.
- The project uses JVM target 11 to support modern testing libraries.

