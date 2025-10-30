# Przykład: Prosty kalkulator matematyczny

Ten projekt demonstruje konfigurację narzędzi buildowych Maven i Gradle, pisanie testów JUnit 5 i mierzenie pokrycia kodu za pomocą JaCoCo na przykładzie klasy `Calculator`.

## Struktura projektu

```
calculator/
├── pom.xml                    # Konfiguracja Maven
├── build.gradle              # Konfiguracja Gradle
├── .gitignore                # Plik ignorowania dla Git
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/
│   │           └── Calculator.java
│   └── test/
│       └── java/
│           └── com/example/
│               └── CalculatorTest.java
├── target/                   # Artefakty Maven
│   └── site/jacoco/index.html
└── build/                    # Artefakty Gradle
    └── reports/jacoco/test/html/index.html
```

## Funkcjonalność

Klasa `Calculator` zawiera cztery podstawowe operacje matematyczne:
- `add(int a, int b)` - dodawanie
- `subtract(int a, int b)` - odejmowanie  
- `multiply(int a, int b)` - mnożenie
- `divide(int a, int b)` - dzielenie (z obsługą dzielenia przez zero)

## Testy

Projekt zawiera kompleksowe testy jednostkowe wykorzystujące:
- **JUnit 5** - framework testowy
- **Testy parametryzowane** - jeden test dla wielu zestawów danych
- **Testy wyjątków** - sprawdzanie obsługi błędów
- **Wzorzec AAA** (Arrange-Act-Assert) - struktura testów

## Komendy Maven

```bash
# Kompilacja i uruchomienie testów
mvn clean test

# Generowanie raportu pokrycia kodu
mvn jacoco:report

# Pełny cykl życia z weryfikacją
mvn clean verify
```

## Komendy Gradle

```bash
# Kompilacja i uruchomienie testów
./gradlew clean test

# Generowanie raportu pokrycia kodu
./gradlew jacocoTestReport

# Pełny build projektu
./gradlew build
```

## Raport pokrycia kodu

Po uruchomieniu testów, raport JaCoCo jest dostępny w:

**Maven:** `target/site/jacoco/index.html`  
**Gradle:** `build/reports/jacoco/test/html/index.html`

Raport pokazuje:
- **100% pokrycie instrukcji** - wszystkie linie kodu zostały wykonane
- **100% pokrycie gałęzi** - wszystkie warunki logiczne zostały przetestowane
- **100% pokrycie metod** - wszystkie metody zostały wywołane
- **100% pokrycie klas** - wszystkie klasy zostały użyte

## Wymagania

- Java 17+ (zalecana Java 17-23, Java 24 może mieć problemy z JaCoCo)
- Maven 3.6+ lub Gradle 7+
- JUnit 5
- JaCoCo

**Uwaga:** Jeśli używasz Java 24, możesz napotkać problemy z JaCoCo. W takim przypadku użyj Java 23 lub starszej wersji.

## Uruchomienie

### Opcja 1: Maven
1. Sklonuj repozytorium
2. Przejdź do katalogu projektu
3. Uruchom: `mvn clean test`
4. Otwórz raport: `target/site/jacoco/index.html`

### Opcja 2: Gradle
1. Sklonuj repozytorium
2. Przejdź do katalogu projektu
3. Uruchom: `./gradlew clean test`
4. Otwórz raport: `build/reports/jacoco/test/html/index.html`

## Przykład użycia

```java
Calculator calc = new Calculator();

int sum = calc.add(5, 3);        // 8
int diff = calc.subtract(5, 3); // 2
int product = calc.multiply(2, 3); // 6
double quotient = calc.divide(5, 2); // 2.5

// Obsługa wyjątku
try {
    calc.divide(10, 0);
} catch (IllegalArgumentException e) {
    System.out.println(e.getMessage()); // "Division by zero"
}
```