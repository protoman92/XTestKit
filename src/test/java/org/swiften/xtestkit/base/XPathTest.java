package org.swiften.xtestkit.base;

import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.android.element.date.AndroidDatePickerType;
import org.swiften.xtestkit.android.element.date.UnitNumberPickerWrapper;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.testng.annotations.Test;

/**
 * Created by haipham on 3/19/17.
 */
public final class XPathTest {
    @Test
    public void test_calendarUnitWrapper_shouldUseCorrectXPath() {
        // Setup
        UnitNumberPickerWrapper wrapper = UnitNumberPickerWrapper.builder()
            .withDatePicker(AndroidDatePickerType.hh_mm_TIME_PICKER)
            .withCalendarUnit(CalendarUnit.HOUR)
            .build();

        // When & Then
        LogUtil.println(wrapper.androidChoicePickerParentXP());
        LogUtil.println(wrapper.androidChoicePickerXP());
        LogUtil.println(wrapper.androidChoicePickerItemXP());
    }

    @Test
    public void test_androidDatePickerType_shouldUseCorrectXPath() {
        // Setup & When & Then
        LogUtil.println(AndroidDatePickerType.CALENDAR.pickerItemXP(CalendarUnit.YEAR));
        LogUtil.println(AndroidDatePickerType.CALENDAR.pickerViewXP(CalendarUnit.YEAR));
        LogUtil.println(AndroidDatePickerType.CALENDAR.targetItemXP(CalendarUnit.YEAR));
    }
}
