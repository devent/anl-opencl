package com.anrisoftware.anlopencl.jmeapp.controllers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

/**
 * Copies implementation from
 * {@link SpinnerValueFactory#DoubleSpinnerValueFactory}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class MappingRangeSpinnerValueFactory extends SpinnerValueFactory<Float> {

    private float min;

    private float max;

    private float amountToStepBy;

    public MappingRangeSpinnerValueFactory(float min, float max, float initialValue, float amountToStepBy) {
        this.min = min;
        this.max = max;
        this.amountToStepBy = amountToStepBy;
        setConverter(new StringConverter<Float>() {
            private final DecimalFormat df = new DecimalFormat("#.#####");

            @Override
            public String toString(Float value) {
                if (value == null) {
                    return "";
                }
                return df.format(value);
            }

            @Override
            public Float fromString(String value) {
                try {
                    if (value == null) {
                        return null;
                    }
                    value = value.trim();
                    if (value.length() < 1) {
                        return null;
                    }
                    return df.parse(value).floatValue();
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        valueProperty().addListener((o, oldValue, newValue) -> {
            if (newValue == null)
                return;
            if (newValue < min) {
                setValue(min);
            } else if (newValue > max) {
                setValue(max);
            }
        });
        setValue(initialValue >= min && initialValue <= max ? initialValue : min);
    }

    /** {@inheritDoc} */
    @Override
    public void decrement(int steps) {
        final BigDecimal currentValue = BigDecimal.valueOf(getValue());
        final BigDecimal minBigDecimal = BigDecimal.valueOf(min);
        final BigDecimal maxBigDecimal = BigDecimal.valueOf(max);
        final BigDecimal amountToStepByBigDecimal = BigDecimal.valueOf(amountToStepBy);
        BigDecimal newValue = currentValue.subtract(amountToStepByBigDecimal.multiply(BigDecimal.valueOf(steps)));
        setValue(newValue.compareTo(minBigDecimal) >= 0 ? newValue.floatValue()
                : (isWrapAround() ? wrapValue(newValue, minBigDecimal, maxBigDecimal).floatValue() : min));
    }

    /** {@inheritDoc} */
    @Override
    public void increment(int steps) {
        final BigDecimal currentValue = BigDecimal.valueOf(getValue());
        final BigDecimal minBigDecimal = BigDecimal.valueOf(min);
        final BigDecimal maxBigDecimal = BigDecimal.valueOf(max);
        final BigDecimal amountToStepByBigDecimal = BigDecimal.valueOf(amountToStepBy);
        BigDecimal newValue = currentValue.add(amountToStepByBigDecimal.multiply(BigDecimal.valueOf(steps)));
        setValue(newValue.compareTo(maxBigDecimal) <= 0 ? newValue.floatValue()
                : (isWrapAround() ? wrapValue(newValue, minBigDecimal, maxBigDecimal).floatValue() : max));
    }

    /*
     * Convenience method to support wrapping values around their min / max
     * constraints. Used by the SpinnerValueFactory implementations when the Spinner
     * wrapAround property is true.
     */
    static BigDecimal wrapValue(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (max.doubleValue() == 0) {
            throw new RuntimeException();
        }

        // note that this wrap method differs from the others where we take the
        // difference - in this approach we wrap to the min or max - it feels better
        // to go from 1 to 0, rather than 1 to 0.05 (where max is 1 and step is 0.05).
        if (value.compareTo(min) < 0) {
            return max;
        } else if (value.compareTo(max) > 0) {
            return min;
        }
        return value;
    }

}
