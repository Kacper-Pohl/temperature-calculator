package com.temperature.calculator;


import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/calc")
public class CalculatorController {

    private CalculatorRepo calculatorRepo;
    private List<Calculator> history = new LinkedList<>();

    public CalculatorController(CalculatorRepo calculatorRepo) {
        this.calculatorRepo = calculatorRepo;
    }

    @GetMapping("/celsius/{degrees}")
    public Calculator celsius(@PathVariable double degrees) {
        var calulator = Calculator.fromCelsius(degrees);
        history.add(calulator);
        calculatorRepo.save(calulator);
        return calulator;
    }

    @GetMapping("/fahrenheit/{degrees}")
    public Calculator fahrenheit(@PathVariable double degrees) {
        var calulator = Calculator.fromFahrenheit(degrees);
        history.add(calulator);
        calculatorRepo.save(calulator);
        return calulator;
    }

    @GetMapping("/kelvin/{degrees}")
    public Calculator kelvin(@PathVariable double degrees) {
        var calulator = Calculator.fromKelvin(degrees);
        history.add(calulator);
        calculatorRepo.save(calulator);
        return calulator;
    }

    @GetMapping("/history")
    public List<Calculator> history(){
        return history;
    }

    @GetMapping("/history/all")
    public Iterable<Calculator> historyAll(){
        return calculatorRepo.findAll();
    }
}
