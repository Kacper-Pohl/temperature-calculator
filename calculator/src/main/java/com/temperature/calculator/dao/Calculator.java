package com.temperature.calculator.dao;


import org.apache.commons.math3.util.Precision;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "HISTORY_CALC")
public class Calculator {

    private double fahrenheit;
    private double celsius;
    private double kelvin;

    private CalculationUnit calculationUnit;

    @Id
    private Timestamp timestamp;

    private static final int PRECISION = 2;

    private Calculator(CalculationUnit calculationUnit, double kelvin, double fahrenheit, double celsius, Timestamp timestamp){
        this.calculationUnit = calculationUnit;
        this.kelvin = Precision.round(kelvin,PRECISION);
        this.fahrenheit = Precision.round(fahrenheit,PRECISION);
        this.celsius = Precision.round(celsius,PRECISION);
        this.timestamp = timestamp;
    }

    public Calculator() {

    }

    public CalculationUnit getCalculationUnit() {
        return calculationUnit;
    }

    public void setCalculationUnit(CalculationUnit calculationUnit) {
        this.calculationUnit = calculationUnit;
    }

    public void setFahrenheit(double fahrenheit) {
        this.fahrenheit = fahrenheit;
    }

    public void setCelsius(double celsius) {
        this.celsius = celsius;
    }

    public void setKelvin(double kelvin) {
        this.kelvin = kelvin;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getFahrenheit() {
        return fahrenheit;
    }

    public double getCelsius() {
        return celsius;
    }

    public double getKelvin() {
        return kelvin;
    }

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
        double fahrenheit = (kelvin * 1.8) - 459.67;
        return new Calculator(CalculationUnit.KELVIN, kelvin,fahrenheit,celsius, new Timestamp(System.currentTimeMillis()));
    }
}
