package org.swiften.xtestkit.base.element.locator.general.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.localizer.LocalizationFormat;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.element.locator.general.param.*;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.type.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This interface provides general locator capabilities.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface BaseLocatorType<D extends WebDriver> extends
    DriverContainerType<D>,
    LocalizerContainerType,
    BaseLocatorErrorType,
    PlatformContainerType,
    PlatformViewContainerType,
    PlatformErrorType
{
    /**
     * @return A {@link PlatformType} instance.
     * @see PlatformContainerType#platform()
     */
    @NotNull
    @Override
    default PlatformType platform() {
        throw new RuntimeException(PLATFORM_UNAVAILABLE);
    }

    //region By XPath
    /**
     * Convenience method to create a new {@link XPath.Builder} instance.
     * @return A {@link XPath.Builder} instance.
     */
    @NotNull
    default XPath.Builder newXPathBuilder() {
        return XPath.builder(platform());
    }

    /**
     * Find all elements that satisfies an {@link XPath} request.
     * @param param A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rxElementsByXPath(@NotNull ByXPath param) {
        final WebDriver DRIVER = driver();
        final String XPATH = param.xPath();
        final String ERROR = param.error();
        List<BaseViewType> classes = param.classes();
        List<WebElement> elements = new ArrayList<>();

        return Flowable.fromIterable(classes)
            .map(cls -> String.format("//%1$s%2$s", cls.className(), XPATH))
            .doOnNext(LogUtil::println)
            .map(path -> {
                try {
                    /* Check for error here just to be certain */
                    return DRIVER.findElements(By.xpath(path));
                } catch (Exception e) {
                    return Collections.<WebElement>emptyList();
                }
            })
            .filter(ObjectUtil::nonNull)
            .reduce(elements, (a, b) -> CollectionUtil.unify(a, b))
            .toFlowable()
            .flatMap(Flowable::fromIterable)
            .switchIfEmpty(RxUtil.error(ERROR))
            .retry(param.retries());
    }

    /**
     * Find an element that satisfies an {@link XPath} request.
     * @param param A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementByXPath(@NotNull ByXPath param) {
        return rxElementsByXPath(param).firstElement().toFlowable();
    }
    //endregion

    //region With Class
    /**
     * Get all {@link BaseViewType} elements of a certain class.
     * @param param An {@link ClassParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxElementsOfClass(@NotNull ClassParam param) {
        XPath xPath = newXPathBuilder().ofClass(param).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsWithClass(param.value()))
            .withRetryType(param)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link ClassParam}.
     * @param cls A {@link String} value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementsOfClass(@NotNull String cls) {
        ClassParam param = ClassParam.builder().withClass(cls).build();
        return rxElementsOfClass(param);
    }

    /**
     * Same as above, but uses a default {@link ClassParam} instance.
     * @param param A {@link ClassContainerType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsOfClass(String)
     */
    @NotNull
    default Flowable<WebElement> rxElementsOfClass(@NotNull ClassContainerType param) {
        return rxElementsOfClass(param.className());
    }

    /**
     * Get an element of a certain class.
     * @param param An {@link IdParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementOfClass(@NotNull ClassParam param) {
        return rxElementsOfClass(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link ClassParam}.
     * @param cls A {@link String} value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementOfClass(@NotNull String cls) {
        ClassParam param = ClassParam.builder().withClass(cls).build();
        return rxElementOfClass(param);
    }

    /**
     * Same as above, but uses a default {@link ClassParam} instance.
     * @param param A {@link ClassContainerType} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementOfClass(String)
     */
    @NotNull
    default Flowable<WebElement> rxElementOfClass(@NotNull ClassContainerType param) {
        return rxElementOfClass(param.className());
    }
    //endregion

    //region With ID
    /**
     * Get all {@link BaseViewType} elements whose IDs contain a certain
     * {@link String}.
     * @param param An {@link IdParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingID(@NotNull IdParam param) {
        XPath xPath = newXPathBuilder().containsID(param).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsWithId(param.value()))
            .withRetryType(param)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link IdParam}.
     * @param id A {@link String} value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingID(@NotNull String id) {
        IdParam param = IdParam.builder().withId(id).build();
        return rxElementsContainingID(param);
    }

    /**
     * Get an element whose ID contains a certain {@link String}.
     * @param param An {@link IdParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingID(@NotNull IdParam param) {
        return rxElementsContainingID(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link IdParam}.
     * @param id A {@link String} value.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingID(@NotNull String id) {
        IdParam param = IdParam.builder().withId(id).build();
        return rxElementContainingID(param);
    }
    //endregion

    //region With Text
    /**
     * Get all {@link BaseViewType#hasText()} {@link WebElement} that are
     * displaying a text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxElementsWithText(@NotNull TextParam param) {
        String localized = localizer().localize(param.value());
        TextParam newParam = param.withNewText(localized);
        XPath xPath = newXPathBuilder().hasText(newParam).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsWithText(localized))
            .withRetryType(param)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link TextParam} with a specified
     * text.
     * @param text A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #rxElementsWithText(TextParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementsWithText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementsWithText(param);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} that is displaying
     * a text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     */
    @NotNull
    default Flowable<WebElement> rxElementWithText(@NotNull TextParam param) {
        return rxElementsWithText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link TextParam} instance.
     * @param text The {@link String} to be found.
     * @return A {@link Flowable} instance.
     * @see #rxElementWithText(TextParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementWithText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementWithText(param);
    }
    //endregion

    //region Contains Text
    /**
     * Get all {@link BaseViewType#hasText()} {@link WebElement} whose texts
     * contain another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingText(@NotNull TextParam param) {
        String localized = localizer().localize(param.value());
        TextParam newParam = param.withNewText(localized);
        XPath xPath = newXPathBuilder().containsText(newParam).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsContainingText(localized))
            .withRetryType(param)
            .build();

        return rxElementsByXPath(query);
    }

    /**
     * Same as above, but uses a default {@link TextParam} with a specified
     * text.
     * @param text A {@link String} value.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(TextParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementsContainingText(param);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} whose text
     * contains another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(TextParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingText(@NotNull TextParam param) {
        return rxElementsContainingText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link TextParam}.
     * @param text The text to be found.
     * @return A {@link Flowable} instance.
     * @see #rxElementWithText(TextParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingText(@NotNull String text) {
        TextParam param = TextParam.builder().withText(text).build();
        return rxElementContainingText(param);
    }

    /**
     * Get all {@link BaseViewType#hasText()} {@link WebElement} whose texts
     * contain another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(TextParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingText(@NotNull TextFormatParam param) {
        String localized = localizer().localize(param.value());

        TextParam textParam = TextParam.builder()
            .withText(localized)
            .shouldIgnoreCase(param)
            .withRetryType(param)
            .build();

        return rxElementsContainingText(textParam);
    }

    /**
     * Same as above, but uses a default {@link TextFormatParam}.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(TextFormatParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingText(@NotNull LocalizationFormat format) {
        TextFormatParam param = TextFormatParam.builder()
            .withLocalizationFormat(format)
            .shouldIgnoreCase(true)
            .build();

        return rxElementsContainingText(param);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} whose text
     * contains another text.
     * @param param A {@link TextParam} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(TextFormatParam)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingText(@NotNull TextFormatParam param) {
        return rxElementsContainingText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link TextFormatParam}.
     * @param format A {@link LocalizationFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(LocalizationFormat)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingText(@NotNull LocalizationFormat format) {
        return rxElementsContainingText(format).firstElement().toFlowable();
    }
    //endregion

    //region Editable Elements
    /**
     * Get all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #platformView()
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxAllEditableElements() {
        List<? extends BaseViewType> views = platformView().isEditable();
        ByXPath query = ByXPath.builder().withClasses(views).build();
        return rxElementsByXPath(query);
    }

    /**
     * Clear all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #rxAllEditableElements()
     * @see WebElement#clear()
     */
    @NotNull
    default Flowable<Boolean> rxClearAllEditableElements() {
        return rxAllEditableElements()
            .flatMapCompletable(a -> Completable.fromAction(a::clear))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Clickable Elements
    /**
     * Get all {@link BaseViewType#isClickable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    default Flowable<WebElement> rxAllClickableElements() {
        XPath xPath = newXPathBuilder().isClickable(true).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_EDITABLE_ELEMENTS)
            .build();

        return rxElementsByXPath(query);
    }
    //endregion
}
