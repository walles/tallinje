package com.gmail.walles.johan.tallinje;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;

public class NumberLineTest {
    @Test
    public void testFormatToPrecision() {
        Assert.assertThat(NumberLine.formatToPrecision(1.0, 1.0), is("1"));
        Assert.assertThat(NumberLine.formatToPrecision(0.0, 1.0), is("0"));
        Assert.assertThat(NumberLine.formatToPrecision(-2.0, 1.0), is("-2"));
    }
}
