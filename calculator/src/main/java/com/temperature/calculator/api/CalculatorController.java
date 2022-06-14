package com.temperature.calculator.api;


import com.temperature.calculator.dao.LocalHistory;
import com.temperature.calculator.dao.Calculator;
import com.temperature.calculator.dao.CalculatorRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calc")
public class CalculatorController {


    private CalculatorRepo calculatorRepo;

    private String getUsername(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth.getName();
        return user;
    }

    private LocalHistory localHistory = LocalHistory.getInstance();

    public CalculatorController(CalculatorRepo calculatorRepo) {
        this.calculatorRepo = calculatorRepo;
    }

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

    @GetMapping("/history")
    public List<Calculator> historyLocal(){
        return localHistory.getUserHistory(getUsername());
    }

    @GetMapping("/history/all")
    public Iterable<Calculator> historyAll(){
        return calculatorRepo.findAll();
    }
}
