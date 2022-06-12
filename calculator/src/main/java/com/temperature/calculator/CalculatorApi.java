package com.temperature.calculator;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calc")
public class CalculatorApi {

    private Map<String, Object> history = new HashMap<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/celsius/{degrees}")
    public Calculator celsius(@PathVariable double degrees) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        history.put(sdf.format(timestamp), Calculator.fromCelsius(degrees));
        return Calculator.fromCelsius(degrees);
    }

    @Validated
    @GetMapping("/fahrenheit/{degrees}")
    public Calculator fahrenheit(@PathVariable double degrees) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        history.put(sdf.format(timestamp), Calculator.fromFahrenheit(degrees));
        return Calculator.fromFahrenheit(degrees);
    }

    @Validated
    @GetMapping("/kelvin/{degrees}")
    public Calculator kelvin(@PathVariable double degrees) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        history.put(sdf.format(timestamp), Calculator.fromKelvin(degrees));
        return Calculator.fromKelvin(degrees);
    }

    @GetMapping("/history")
    public Map<String, Object> history(){
        return history;
    }
}
