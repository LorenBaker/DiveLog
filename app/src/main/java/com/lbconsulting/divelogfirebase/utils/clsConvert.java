package com.lbconsulting.divelogfirebase.utils;

/**
 * Created by Loren on 1/12/2015.
 */
public class clsConvert {

    public static double feetToMeters(double feet) {
        return feet / 3.28084;
    }

    public static double metersToFeet(double meters) {
        return meters * 3.28084;
    }


    public static double fahrenheitToCelsius(double fahrenheit) {
        return ((fahrenheit - 32) * 5) / 9;
    }

    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }


    public static double litreToCubicFeet(double litre) {
        return litre / 28.3168467117;
    }

    public static double cubicFeetToLitre(double cubicFeet) {
        return cubicFeet * 28.3168467117;
    }

    public static double psiToBars(double psi) {
        return psi / 14.5037738;
    }

    public static double barsToPsi(double bars) {
        return bars * 14.5037738;
    }

    public static double poundsToKg(double pounds) {
        return pounds * 0.453592;
    }

    public static double kgToPounds(double kg) {
        return kg / 0.453592;
    }

    public static double millisToMinutes(double milliseconds) {
        return milliseconds / 60000;
    }

    public static double minutesToMillis(double minutes) {
        return minutes * 60000;
    }
}
