package org.swiften.xtestkit.mobile.ios.element.action.date.type;

/**
 * Created by haipham on 22/5/17.
 */

import org.jetbrains.annotations.NotNull;
import org.swiften.xtestkit.base.element.action.date.CalendarUnit;
import org.swiften.xtestkit.base.element.action.date.type.DatePickerContainerType;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.locator.general.xpath.type.NewXPathBuilderType;
import org.swiften.xtestkit.mobile.Platform;

/**
 * This interface provides date picker properties for
 * {@link org.swiften.xtestkit.mobile.ios.IOSEngine}
 */
public interface IOSDatePickerContainerType extends DatePickerContainerType {
    enum IOSDatePickerType implements DatePickerType {
        BASIC;

        /**
         * @return {@link XPath.Builder} instance.
         * @see NewXPathBuilderType#newXPathBuilder()
         * @see Platform#IOS
         */
        @NotNull
        public XPath.Builder newXPathBuilder() {
            return XPath.builder(Platform.IOS);
        }

        /**
         * @param unit {@link CalendarUnit} instance.
         * @return {@link String} value.
         * @see DatePickerContainerType.DatePickerType#stringFormat(CalendarUnit)
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public String stringFormat(@NotNull CalendarUnit unit) {
            switch (unit) {
                case DAY:
                    return "dd";

                case MONTH:
                    return "MMM";

                case YEAR:
                    return "YYYY";

                default:
                    throw new RuntimeException(NOT_AVAILABLE);
            }
        }

        /**
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         * @see DatePickerContainerType.DatePickerType#displayViewXPath(CalendarUnit)
         * @see #newXPathBuilder()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public XPath displayViewXPath(@NotNull CalendarUnit unit) {
            return newXPathBuilder().build();
        }

        /**
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         * @see DatePickerContainerType.DatePickerType#pickerViewXPath(CalendarUnit)
         * @see #newXPathBuilder()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public XPath pickerViewXPath(@NotNull CalendarUnit unit) {
            return newXPathBuilder().build();
        }

        /**
         * @param unit {@link CalendarUnit} instance.
         * @return {@link XPath} instance.
         * @see DatePickerContainerType.DatePickerType#listViewItemXPath(CalendarUnit)
         * @see #newXPathBuilder()
         * @see #NOT_AVAILABLE
         */
        @NotNull
        @Override
        public XPath listViewItemXPath(@NotNull CalendarUnit unit) {
            return newXPathBuilder().build();
        }
    }

    /**
     * @return {@link DatePickerContainerType.DatePickerType} instance.
     * @see DatePickerContainerType#datePickerType()
     */
    @NotNull
    default IOSDatePickerType datePickerType() {
        return IOSDatePickerType.BASIC;
    }
}
