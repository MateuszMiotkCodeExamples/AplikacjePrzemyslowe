# Przykład: Prosty kalkulator matematyczny

Ten projekt demonstruje konfigurację narzędzi buildowych Maven, pisanie testów JUnit 5 i mierzenie pokrycia kodu za pomocą JaCoCo na przykładzie klasy `Calculator`.

## Struktura projektu

```
calculator/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/
│   │           └── Calculator.java
│   └── test/
│       └── java/
│           └── com/example/
│               └── CalculatorTest.java
└── target/
    └── site/jacoco/index.html
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

## Raport pokrycia kodu

Po uruchomieniu testów, raport JaCoCo jest dostępny w:
`target/site/jacoco/index.html`

Raport pokazuje:
- **100% pokrycie instrukcji** - wszystkie linie kodu zostały wykonane
- **100% pokrycie gałęzi** - wszystkie warunki logiczne zostały przetestowane
- **100% pokrycie metod** - wszystkie metody zostały wywołane
- **100% pokrycie klas** - wszystkie klasy zostały użyte

## Wymagania

- Java 17+
- Maven 3.6+
- JUnit 5
- JaCoCo

## Uruchomienie

1. Sklonuj repozytorium
2. Przejdź do katalogu projektu
3. Uruchom: `mvn clean test`
4. Otwórz raport: `target/site/jacoco/index.html`

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