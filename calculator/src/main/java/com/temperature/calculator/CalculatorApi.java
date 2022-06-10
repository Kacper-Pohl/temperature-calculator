package com.temperature.calculator;


import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calc")
public class CalculatorApi {

    int index = 1;
    private Map<Integer, Object> history = new HashMap<>();

    @GetMapping("/celsius/{degrees}")
    public Calculator celsius(@PathVariable double degrees) {
        history.put(index++, Calculator.fromCelsius(degrees));
        return Calculator.fromCelsius(degrees);
    }

    @GetMapping("/fahrenheit/{degrees}")
    public Calculator fahrenheit(@PathVariable double degrees) {
        history.put(index++, Calculator.fromFahrenheit(degrees));
        return Calculator.fromFahrenheit(degrees);
    }

    @GetMapping("/kelvin/{degrees}")
    public Calculator kelvin(@PathVariable double degrees) {
        history.put(index++, Calculator.fromKelvin(degrees));
        return Calculator.fromKelvin(degrees);
    }

    @GetMapping("/history")
    public Map<Integer, Object> history(){
        return history;
    }
}
