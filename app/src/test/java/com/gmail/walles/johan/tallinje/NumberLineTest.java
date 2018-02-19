package com.gmail.walles.johan.tallinje;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.matchesPattern;

import org.junit.Assert;
import org.junit.Test;

public class NumberLineTest {
    @Test
    public void testFormatToPrecision() {
        Assert.assertThat(NumberLine.formatToPrecision(1.0, 1.0), is("1"));
        Assert.assertThat(NumberLine.formatToPrecision(0.0, 1.0), is("0"));
        Assert.assertThat(NumberLine.formatToPrecision(-2.0, 1.0), is("-2"));

        Assert.assertThat(NumberLine.formatToPrecision(0.9, 0.1), matchesPattern("0[^0-9]9"));
        Assert.assertThat(NumberLine.formatToPrecision(0.8, 0.2), matchesPattern("0[^0-9]8"));
        Assert.assertThat(NumberLine.formatToPrecision(0.6, 0.2), matchesPattern("0[^0-9]6"));
        Assert.assertThat(NumberLine.formatToPrecision(0.4, 0.2), matchesPattern("0[^0-9]4"));
        Assert.assertThat(NumberLine.formatToPrecision(1.5, 0.5), matchesPattern("1[^0-9]5"));
    }

    @Test
    public void testGetStepDigit() {
        Assert.assertThat(NumberLine.getStepDigit(10.0), is(1));
        Assert.assertThat(NumberLine.getStepDigit(5.0), is(5));
        Assert.assertThat(NumberLine.getStepDigit(2.0), is(2));
        Assert.assertThat(NumberLine.getStepDigit(1.0), is(1));
        Assert.assertThat(NumberLine.getStepDigit(0.5), is(5));
        Assert.assertThat(NumberLine.getStepDigit(0.2), is(2));
        Assert.assertThat(NumberLine.getStepDigit(0.1), is(1));
    }

    @Test
    public void testIncreaseStep() {
        Assert.assertThat(NumberLine.increaseStep(0.1), is(0.2));
        Assert.assertThat(NumberLine.increaseStep(0.2), is(0.5));
        Assert.assertThat(NumberLine.increaseStep(0.5), is(1.0));
        Assert.assertThat(NumberLine.increaseStep(1.0), is(2.0));
        Assert.assertThat(NumberLine.increaseStep(2.0), is(5.0));
        Assert.assertThat(NumberLine.increaseStep(5.0), is(10.0));
    }

    @Test
    public void testDecreaseStep() {
        Assert.assertThat(NumberLine.decreaseStep(5.0), is(2.0));
        Assert.assertThat(NumberLine.decreaseStep(2.0), is(1.0));
        Assert.assertThat(NumberLine.decreaseStep(1.0), is(0.5));
        Assert.assertThat(NumberLine.decreaseStep(0.5), is(0.2));
        Assert.assertThat(NumberLine.decreaseStep(0.2), is(0.1));
        Assert.assertThat(NumberLine.decreaseStep(0.1), is(0.05));
    }
}
