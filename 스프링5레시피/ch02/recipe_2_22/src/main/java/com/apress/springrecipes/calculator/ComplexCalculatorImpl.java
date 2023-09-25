package com.apress.springrecipes.calculator;

import org.springframework.stereotype.Component;

@Component("complexCalculator")
public class ComplexCalculatorImpl implements ComplexCalculator {

    @Override
    public Complex add(Complex a, Complex b) {
        Complex result = new Complex(a.getReal() + b.getReal(),
                a.getImaginary() + b.getImaginary());
        System.out.println(a + " + " + b + " = " + result);
        return result;
    }

    @Override
    public Complex sub(Complex a, Complex b) {
        Complex result = new Complex(a.getReal() - b.getReal(),
                a.getImaginary() - b.getImaginary());
        System.out.println(a + " - " + b + " = " + result);
        return result;
    }
}
