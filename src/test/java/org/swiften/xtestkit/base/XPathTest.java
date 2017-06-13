package org.swiften.xtestkit.base;

import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.xtestkit.android.element.date.AndroidDatePickerType;
import org.swiften.xtestkit.android.element.date.UnitNumberPickerWrapper;
import org.swiften.xtestkit.base.element.date.CalendarUnit;
import org.swiften.xtestkit.base.model.InputHelperType;
import org.swiften.xtestkitcomponents.platform.PlatformType;
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
            .withDatePicker(AndroidDatePickerType.HH_mm_TIME_PICKER)
            .withCalendarUnit(CalendarUnit.HOUR)
            .build();

        // When & Then
        LogUtil.println(wrapper.androidChoicePickerParentXP(helper));
        LogUtil.println(wrapper.androidChoicePickerXP(helper));
        LogUtil.println(wrapper.androidChoicePickerItemXP(helper));
    }

    @Test
    public void test_androidDatePickerType_shouldUseCorrectXPath() {
        // Setup & When & Then
        LogUtil.println(AndroidDatePickerType.CALENDAR.pickerItemXP(CalendarUnit.YEAR));
        LogUtil.println(AndroidDatePickerType.CALENDAR.pickerViewXP(CalendarUnit.YEAR));
        LogUtil.println(AndroidDatePickerType.CALENDAR.targetItemXP(CalendarUnit.YEAR));
    }
}
