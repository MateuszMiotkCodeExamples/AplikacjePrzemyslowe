# Spring Boot REST API Example with MockMVC Testing

## Projekt

Ten projekt demonstruje implementację REST API w Spring Boot z wykorzystaniem MockMVC do testowania.

## Struktura Projektu

```
src/main/java/com/example/library/
├── model/
│   └── Book.java                  # Model domenowy - reprezentuje książkę
├── dto/
│   ├── BookDTO.java              # DTO do wysyłania danych do klienta
│   └── CreateBookRequest.java    # DTO do przyjmowania danych od klienta
├── service/
│   └── BookService.java          # Serwis biznesowy zarządzany przez Spring
├── controller/
│   └── BookController.java       # Kontroler REST obsługujący żądania HTTP
└── LibraryApplication.java       # Główna klasa aplikacji Spring Boot
```

## Główne Koncepcje

### 1. Dependency Injection

Spring Boot automatycznie zarządza obiektami (beanami) używając kontenera Spring:

```java
@Service
public class BookService {
    // Spring automatycznie utworzy instancję tej klasy
}
```

### 2. DTO (Data Transfer Objects)

DTO to obiekty służące do bezpiecznego przesyłania danych:

- **BookDTO** - zawiera tylko dane publiczne (tytuł, autor, rok)
- **CreateBookRequest** - używa klient do tworzenia nowych książek

### 3. REST API Endpoints

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/books` | Pobiera wszystkie książki |
| POST | `/api/books` | Tworzy nową książkę |
| GET | `/api/books/author/{name}` | Wyszukuje książki po autorze |

### 4. MockMVC Testowanie

MockMVC pozwala testować kontrolery bez uruchamiania serwera HTTP:

```java
@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
}
```

## Jak Uruchomić

### Zbudować projekt:
```bash
mvn clean compile
```

### Uruchomić testy:
```bash
mvn test
```

### Uruchomić aplikację:
```bash
mvn spring-boot:run
```

Aplikacja będzie dostępna na: http://localhost:8080

## Przykłady Użycia API

### Pobranie wszystkich książek:
```bash
curl http://localhost:8080/api/books
```

### Dodanie nowej książki:
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "1984",
    "author": "George Orwell",
    "year": 1949
  }'
```

### Wyszukiwanie po autorze:
```bash
curl http://localhost:8080/api/books/author/George%20Orwell
```

## Kluczowe Różnice: Assert vs Verify

### Assert - Testowanie Stanu
Sprawdza **wynik** operacji:
```java
assertEquals(expected, actual);
```

### Verify - Testowanie Zachowania
Sprawdza **interakcje** między obiektami:
```java
verify(bookService).addBook(any(Book.class));
```

W testach kontrolerów używamy **obu** podejść:
- `andExpect()` - sprawdza wynik HTTP (assert)
- `verify()` - sprawdza czy kontroler wywołał serwis (verify)

## Konfiguracja

Plik `application.properties` zawiera:
- Konfigurację serwera (port 8080)
- Poziomy logowania
- Właściwości aplikacji

## Pokrycie Kodu - JaCoCo

Projekt jest skonfigurowany z JaCoCo do mierzenia pokrycia kodu testami. Aktualne pokrycie:

- **BookController**: 100% instrukcji
- **CreateBookRequest**: 100%
- **Calculator**: 78.6%
- **Book**: 72.4%
- **BookDTO**: 58.3%

Aby wygenerować raport pokrycia:
```bash
mvn clean test
open target/site/jacoco/index.html
```

## Rozwiązanie Problemu z Java 25

Projekt używa Java 25, która nie jest jeszcze w pełni obsługiwana przez wszystkie biblioteki testowe.
Rozwiązanie: 
1. Dodano flagę `-Dnet.bytebuddy.experimental=true` w konfiguracji Surefire plugin
2. Konfiguracja JaCoCo używa `@{argLine}` aby poprawnie przekazać parametry do Surefire
3. Wykluczono klasy aplikacji i konfiguracyjne z raportu pokrycia

## Zależności

- Spring Boot 3.2.0
- Spring Web (REST API)
- Spring Boot Test (MockMVC)
- JUnit 5
- Mockito

## Autor

Przykład stworzony na podstawie poradników o Spring Boot, Dependency Injection i testowaniu REST API.

