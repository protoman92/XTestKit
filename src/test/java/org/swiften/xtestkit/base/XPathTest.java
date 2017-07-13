package org.swiften.xtestkit.base;

import org.swiften.javautilities.util.HPLog;
import org.swiften.xtestkit.android.element.date.AndroidDatePickerType;
import org.swiften.xtestkit.android.element.date.UnitNumberPickerWrapper;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by haipham on 3/19/17.
 */
public final class XPathTest {
    @Test
    public void test_calendarUnitWrapper_shouldUseCorrectXPath() {
        // Setup
        InputHelperType helper = mock(InputHelperType.class);

        UnitNumberPickerWrapper wrapper = UnitNumberPickerWrapper.builder()
            .withDatePicker(AndroidDatePickerType.TIME_NUMBER_PICKER_HH_mm)
            .withCalendarUnit(CalendarUnit.HOUR_12)
            .build();

        // When & Then
        HPLog.println(wrapper.androidChoicePickerParentXP(helper));
        HPLog.println(wrapper.androidChoicePickerXP(helper));
        HPLog.println(wrapper.androidChoicePickerItemXP(helper));
    }

    @Test
    public void test_androidDatePickerType_shouldUseCorrectXPath() {
        // Setup & When & Then
        HPLog.println(AndroidDatePickerType.DATE_CALENDAR_PICKER.pickerItemXP(CalendarUnit.YEAR));
        HPLog.println(AndroidDatePickerType.DATE_CALENDAR_PICKER.pickerViewXP(CalendarUnit.YEAR));
        HPLog.println(AndroidDatePickerType.DATE_CALENDAR_PICKER.targetItemXP(CalendarUnit.YEAR));
    }
}
