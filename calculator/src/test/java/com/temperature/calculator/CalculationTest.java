package com.temperature.calculator;

import com.temperature.calculator.dao.Calculator;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.assertEquals;

public class CalculationTest {

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

}
