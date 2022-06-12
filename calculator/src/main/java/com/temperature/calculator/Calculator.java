package com.temperature.calculator;


public class Calculator {

    private double fahrenheit;
    private double celsius;
    private double kelvin;



    private Calculator(double kelvin, double fahrenheit, double celsius){
        this.kelvin = kelvin;
        this.fahrenheit = fahrenheit;
        this.celsius = celsius;
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
        return new Calculator(kelvin,fahrenheit,celsius);
    }

    public static Calculator fromFahrenheit(double fahrenheit){
        double celsius = (fahrenheit -32) / 1.8;
        double kelvin = (fahrenheit + 459.67) * 5/9;
        return new Calculator(kelvin,fahrenheit,celsius);
    }

    public static Calculator fromKelvin(double kelvin){
        double celsius = kelvin - 273.15;
        double fahrenheit = (kelvin * 1.8) - 459.67 ;
        return new Calculator(kelvin,fahrenheit,celsius);
    }
}
