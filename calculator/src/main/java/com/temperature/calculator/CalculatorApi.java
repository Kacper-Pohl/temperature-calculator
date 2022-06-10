package com.temperature.calculator;


import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calc")
public class CalculatorApi {

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private Map<Timestamp, Object> history = new HashMap<>();

    @GetMapping("/celsius/{degrees}")
    public Calculator celsius(@PathVariable double degrees) {
        history.put(timestamp, Calculator.fromCelsius(degrees));
        return Calculator.fromCelsius(degrees);
    }

    @GetMapping("/fahrenheit/{degrees}")
    public Calculator fahrenheit(@PathVariable double degrees) {
        history.put(timestamp, Calculator.fromFahrenheit(degrees));
        return Calculator.fromFahrenheit(degrees);
    }

    @GetMapping("/kelvin/{degrees}")
    public Calculator kelvin(@PathVariable double degrees) {
        history.put(timestamp, Calculator.fromKelvin(degrees));
        return Calculator.fromKelvin(degrees);
    }

    @GetMapping("/history")
    public Map<Timestamp, Object> history(){
        return history;
    }
}
