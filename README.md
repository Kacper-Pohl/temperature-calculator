# temperature-calculator

Kalkulator, służący do konwersji temperatury między stopniami Celsjusza, Fahrenheita oraz Kelwina.

Aplikacja oparta na spring boot, obsługiwana przez _REST API_.

Do testowania wykorzystałem aplikacje Postman.

Przed uruchomieniem ważne jest aby w _**application.properties**_ zmienić ściężkę bazy, tak aby była ustawiona 
na _**calcHistory**_ w _**resources/data**_.

Przykład:
```
spring.datasource.url=jdbc:h2:file:E:/GitHub/temperature-calculator/calculator/src/main/resources/data/calcHistory;AUTO_SERVER=true;
```

## Autentykacja

Zastosowana jest podstawowa autentykacja _(basic auth)_ która pozwala na zapis historii operacji osobno dla każdego z użytkowników.
Stworzone są dwa konta w pamięci, **user** z rolą **_USER_**, oraz **admin** z rolą _**ADMIN**_. 
Przed korzystaniem z API trzeba się zalogować na user, zalogowanie na admina daje nam dostęp do H2-console, konsoli bazy danych.
Gdy wejdziemy przez przeglądarke powinno nam wyskoczyć okienko z prośba podania nazwy użytkownika oraz hasła.
W Postman wystarczy wybrać w zakładce **_Authorization_**, opcję **Basic Auth**, po czym wprowadzić **Username** i **Password**

```
Konta:
* user - password
* admin - password
```

```java
    public UserDetailsService users() {
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails user = users
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        UserDetails admin = users
                .username("admin")
                .password("password")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
```

```java
protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/h2-console/**").hasRole("ADMIN").and().httpBasic();

        http.authorizeRequests()
                .antMatchers("/**").hasRole("USER").and().httpBasic();

    }
```

## Obliczenia


W zależności od tego z jaką wybierzemy jednostkę temperatury w API, wywoła się inna metoda w klasie Calculator, do innego przeliczenia na resztę jednostek.
Zwraca nam to wyliczone temperatory, jednostkę z której wyliczaliśmy resztę, oraz timestamp wykonania operacji.

```java
    public static Calculator fromCelsius(double celsius){
        double fahrenheit = (celsius * 1.8) + 32;
        double kelvin = celsius + 273.15;
        return new Calculator(CalculationUnit.CELCIUS, kelvin,fahrenheit,celsius, new Timestamp(System.currentTimeMillis()));
    }

    public static Calculator fromFahrenheit(double fahrenheit){
        double celsius = (fahrenheit -32) / 1.8;
        double kelvin = (fahrenheit + 459.67) * 5/9;
        return new Calculator(CalculationUnit.FAHRENHEIT, kelvin,fahrenheit,celsius, new Timestamp(System.currentTimeMillis()));
    }

    public static Calculator fromKelvin(double kelvin){
        double celsius = kelvin - 273.15;
        double fahrenheit = (kelvin * 1.8) - 459.67 ;
        return new Calculator(CalculationUnit.KELVIN, kelvin,fahrenheit,celsius, new Timestamp(System.currentTimeMillis()));
    }
```

W konstruktorze Calculator odbywa się zaokrąglenie liczb, ze względu na to że bardzo często, liczby po przecinku
doążyły do nieskończoności. Wykorzystałem do tego _Precision.round_ z [Apache Commons Math](https://mvnrepository.com/artifact/org.apache.commons/commons-math3).

```java
    private Calculator(CalculationUnit calculationUnit, double kelvin, double fahrenheit, double celsius, Timestamp timestamp){
        this.calculationUnit = calculationUnit;
        this.kelvin = Precision.round(kelvin,PRECISION);
        this.fahrenheit = Precision.round(fahrenheit,PRECISION);
        this.celsius = Precision.round(celsius,PRECISION);
        this.timestamp = timestamp;
    }
```

## Controller

Do kalkulacji z każdej jednostki temperatur, mamy osobne end-pointy, gdzie na końcu wpisujemy stopnie, typu double.
Następnie jest przeliczane przez jedną metod, w zależności jaką jednostkę wybraliśmy, wartości są zapisywane do bazy, oraz 
do lokalnej historii obliczeń, a na końcu zwraca nam wynik.

```java
    @GetMapping("/celsius/{degrees}")
    public Calculator celsius(@PathVariable double degrees) {
        var calulator = Calculator.fromCelsius(degrees);
        calculatorRepo.save(calulator);
        localHistory.addUserCalculation(getUsername(), calulator);
        return calulator;
    }

    @GetMapping("/fahrenheit/{degrees}")
    public Calculator fahrenheit(@PathVariable double degrees) {
        var calulator = Calculator.fromFahrenheit(degrees);
        calculatorRepo.save(calulator);
        localHistory.addUserCalculation(getUsername(), calulator);
        return calulator;
    }

    @GetMapping("/kelvin/{degrees}")
    public Calculator kelvin(@PathVariable double degrees) {
        var calulator = Calculator.fromKelvin(degrees);
        calculatorRepo.save(calulator);
        localHistory.addUserCalculation(getUsername(), calulator);
        return calulator;
    }
```



Wynik operacji dla: http://localhost:8090/api/calc/celsius/27

```json
{
    "fahrenheit": 80.6,
    "celsius": 27.0,
    "kelvin": 300.15,
    "calculationUnit": "CELCIUS",
    "timestamp": "2022-06-13T18:11:32.123+00:00"
}
```

## Historia operacji

Stworzone są dwa end-pointy dla historii. Pierwsza historia jest dopiero od uruchomienia aplikacji, 
dla danego użytkownika, co oznacza że użytkownicy mają osobny zapis historii obliczeń.
Druga historia jest historią ze wszystkich dotychczas obliczeń, które są zapisywane w bazie danych.

```java
    @GetMapping("/history")
    public List<Calculator> historyLocal(){
        return localHistory.getUserHistory(getUsername());
    }

    @GetMapping("/history/all")
    public Iterable<Calculator> historyAll(){
        return calculatorRepo.findAll();
    }
```

Do lokalnej historii mamy stworzony wzorzec singleton, wraz z metodami, które odpowiadają za dodanie do historii  według nazwy użytkownika,
oraz drugą która zwraca nam historię dla użytkownika.

```java
    public void addUserCalculation(String user, Calculator calculator){
        var userHistory = getUserHistory(user);
        userHistory.add(calculator);
    }

    public List<Calculator> getUserHistory(String user){
        var userHistory = history.get(user);
        if(userHistory == null){
            userHistory = new LinkedList<Calculator>();
            history.put(user, userHistory);
        }
        return userHistory;
    }
```

Ważna jest także metoda która zwraca nam nazwę użytkownika który jest obecnie zalogowany. Jest ona wykorzystana przy histori lokalnej..

```java
    private String getUsername(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        return user;
    }
```

Przykładowy wynik operacji dla: http://localhost:8090/api/calc/history

```json
[
    {
        "fahrenheit": 73.4,
        "celsius": 23.0,
        "kelvin": 296.15,
        "calculationUnit": "CELCIUS",
        "timestamp": "2022-06-13T18:39:21.739+00:00"
    },
    {
        "fahrenheit": 25.0,
        "celsius": -3.89,
        "kelvin": 269.26,
        "calculationUnit": "FAHRENHEIT",
        "timestamp": "2022-06-13T18:39:30.773+00:00"
    }
]
```

Fragment przykładowego wyniku: http://localhost:8090/api/calc/history/all

```json
[
  {
    "fahrenheit": 73.4,
    "celsius": 23.0,
    "kelvin": 296.15,
    "calculationUnit": "CELCIUS",
    "timestamp": "2022-06-12T19:10:02.818+00:00"
  },
  {
    "fahrenheit": 91.4,
    "celsius": 33.0,
    "kelvin": 306.15,
    "calculationUnit": "CELCIUS",
    "timestamp": "2022-06-12T19:47:39.252+00:00"
  },
  {
    "fahrenheit": -407.47,
    "celsius": -244.15,
    "kelvin": 29.0,
    "calculationUnit": "KELVIN",
    "timestamp": "2022-06-13T14:31:28.345+00:00"
  },
```

## Obsługa błędów

Do obsługi błędów stworzyłem specjalny ExceptionHandler, który w przypadku gdy użyjemy innego typu 
niż double, wyskoczy nam status _BAD_REQUEST_, wraz z wiadomością i błędem, informującym 
nam o tym że _**degrees**_ powinny być typu _**double**_. 
Domyślnie Spring sam wyrzuca nam status 400 _BAD_REQUEST_, jednak bez dokładnej informacji dlaczego.


Przykład błędu w przypadku http://localhost:8090/api/calc/kelvin/temp
```json
{
    "status": "BAD_REQUEST",
    "message": "Failed to convert value of type 'java.lang.String' to required type 'double'; nested exception is java.lang.NumberFormatException: For input string: \"temp\"",
    "errors": [
        "degrees should be of type double"
    ]
}
```

```java
@ControllerAdvice
public class CalculatorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {

        logger.info(ex.getClass().getName());
        final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
        final ResponseError responseError = new ResponseError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(responseError, new HttpHeaders(), responseError.getStatus());
    }

}
```

Do tego stworzona została klasa ResponseError, która odpowiada za format w jakim zwracany jest nasz błąd.

```java
public class ResponseError {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ResponseError() {
        super();
    }

    public ResponseError(final HttpStatus status, final String message, final String error) {
        super();
        this.status = status;
        this.message = message;
        errors = List.of(error);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

}
```

## Testy - API

W ApiTest.java znajduje się pięc testów które sprawdzają czy przy zapytaniach do API wszystko zwraca się tak jak powinno.
Wszystkie testy poniżej przeszły pomyślnie.

Pierwszy test sprawdza czy zwraca nam poprawny wynik przy konwersji temperatur, oraz czy wyświetla się dobry calculationUnit, w zależności od wyboru jednostki z której konwertujemy.

```java
    @Test
    public void getCorrectCalculation() throws Exception {
        mvc.perform(get("/api/calc/kelvin/{degrees}", 27).with(httpBasic("admin","password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fahrenheit", Is.is(-411.07)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kelvin",Is.is(27.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.celsius",Is.is(-246.15)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.calculationUnit",Is.is("KELVIN")))
                .andDo(print());
    }
```

Test sprawdzający czy po wprowadzeniu danych złego typu, wyskoczy nam BAD_REQUEST, oraz wiadomoć błędu o złym typie.

```java
    @Test
    public void getErrorCalculation() throws Exception{
        mvc.perform(get("/api/calc/kelvin/{degrees}", "degrees").with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]", Is.is("degrees should be of type double"))).andDo(print());
    }
```

Mamy dwa testy sprawdzające czy autoryzacja (basicAuth) działa poprawnie. W pierwszym zostały podane poprawne dane do logowania,
w drugim teście hasło, które zostało podane - było nieprawidłowe.

```java
    @Test
    public void checkAuthCorrectPassword() throws Exception{
        mvc.perform(get("/api/calc/history").with(httpBasic("user","password")))
        .andExpect(status().isOk()).andDo(print());
        }

    @Test
    public void checkAuthWrongPassword() throws Exception{
        mvc.perform(get("/api/calc/history").with(httpBasic("user","password1")))
        .andExpect(status().isUnauthorized()).andDo(print());
        }
```

Ostatni test sprawdza czy historia lokalna poprawnie działa w zależności od zmiany użytkownika.
Wprowadzamy obliczenia na dwóch różnych użytkownikach i sprawdzamy czy wyniki się zgadzają w obu przypadkach.

```java
    @Test
    public void checkHistoryLocal() throws Exception{
        mvc.perform(get("/api/calc/kelvin/{degrees}", 27).with(httpBasic("admin","password"))).andExpect(status().isOk());
        mvc.perform(get("/api/calc/fahrenheit/{degrees}", 27).with(httpBasic("user","password"))).andExpect(status().isOk());
        mvc.perform(get("/api/calc/history").with(httpBasic("admin","password")))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fahrenheit", Is.is(-411.07)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].kelvin",Is.is(27.0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].celsius",Is.is(-246.15)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].calculationUnit",Is.is("KELVIN")));
        mvc.perform(get("/api/calc/history").with(httpBasic("user","password")))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fahrenheit", Is.is(27.0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].kelvin",Is.is(270.37)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].celsius",Is.is(-2.78)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].calculationUnit",Is.is("FAHRENHEIT")));

        }
```

## Testy - Calculation

Trzy testy sprawdzające czy wszystkie metody konwertujące wartości temperatur, zwracają poprawne wyniki, dla każdej z trzech jednostek. 

```java
    private static final double DELTA = 0;

    @Test
    @DisplayName("Calculation from Celsius")
    public void testCalcCelsius(){
        var calc = Calculator.fromCelsius(20);
        assertEquals(20,calc.getCelsius(),DELTA);
        assertEquals(68.0,calc.getFahrenheit(),DELTA);
        assertEquals(293.15,calc.getKelvin(),DELTA);
    }

    @Test
    @DisplayName("Calculation from Fahrenheit")
    public void testCalcFahrenheit(){
        var calc = Calculator.fromFahrenheit(20);
        assertEquals(-6.67,calc.getCelsius(),DELTA);
        assertEquals(20,calc.getFahrenheit(),DELTA);
        assertEquals(266.48,calc.getKelvin(),DELTA);
    }

    @Test
    @DisplayName("Calculation from Kelvin")
    public void testCalcKelvin(){
        var calc = Calculator.fromKelvin(20);
        assertEquals(-253.15,calc.getCelsius(),DELTA);
        assertEquals(-423.67,calc.getFahrenheit(),DELTA);
        assertEquals(20,calc.getKelvin(),DELTA);
    }
```
