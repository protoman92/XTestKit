package org.swiften.xtestkit.util;

import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.util.type.ValueRangeConverterType;
import org.swiften.xtestkit.util.type.ValueRangeType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by haipham on 5/11/17.
 */
public final class ValueRangeTest {
    @Test
    public void test_createIntegerRange_shouldWork() {
        // Setup
        Integer min = 1;
        Integer max = 100;
        Integer step = 2;
        ValueRangeType.Converter<Integer> converter = a -> (int)a;
        ValueRangeType rangeType1 = new ValueRangeType() {};
        ValueRangeConverterType<Integer> rangeType2 = (() -> a -> (int)a);

        // When
        List<Integer> range1 = rangeType1.valueRange(min, max, step, converter);
        List<Integer> range2 = rangeType2.valueRange(min, max, step);

        // Then
        Assert.assertEquals(range1.size(), (int)((double)((max - min) / 2) + 1));
        Assert.assertEquals(range1, range2);
    }

    @Test
    public void test_createFloatRange_shouldWork() {
        // Setup
        Float min = 1.053f;
        Float max = 99.95f;
        Float step = 2f;
        ValueRangeType.Converter<Float> converter = a -> (float)a;
        ValueRangeType rangeType1 = new ValueRangeType() {};
        ValueRangeConverterType<Float> rangeType2 = (() -> a -> (float)a);

        // When
        List<Float> range1 = rangeType1.valueRange(min, max, step, converter);
        List<Float> range2 = rangeType2.valueRange(min, max, step);

        // Then
        Assert.assertEquals(range1, range2);
    }
}
