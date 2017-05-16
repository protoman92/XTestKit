package org.swiften.xtestkit.base.element.locator.general.type;

/**
 * Created by haipham on 5/8/17.
 */

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.swiften.javautilities.collection.CollectionUtil;
import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.javautilities.rx.RxUtil;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.element.locator.general.param.*;
import org.swiften.xtestkit.base.element.locator.general.xpath.XPath;
import org.swiften.xtestkit.base.element.property.type.base.FormatType;
import org.swiften.xtestkit.base.element.property.type.base.StringType;
import org.swiften.xtestkit.base.element.property.type.sub.ContainsIDType;
import org.swiften.xtestkit.base.element.property.type.sub.OfClassType;
import org.swiften.xtestkit.base.type.*;

import java.util.ArrayList;
import java.util.Collection;
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
    PlatformViewContainerType
{
    //region By XPath
    /**
     * Convenience method to create a new {@link XPath.Builder} instance.
     * @return A {@link XPath.Builder} instance.
     * @see XPath#builder(PlatformType)
     */
    @NotNull
    default XPath.Builder newXPathBuilder() {
        return XPath.builder(platform());
    }

    /**
     * Find all elements that satisfies an {@link XPath} request.
     * @param param A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     * @see #driver()
     * @see D#findElements(By)
     * @see CollectionUtil#unify(Collection[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rxElementsByXPath(@NotNull ByXPath param) {
        final WebDriver DRIVER = driver();
        final String XPATH = param.xPath();
        List<BaseViewType> classes = param.classes();

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
            .flatMap(Flowable::fromIterable)
            .switchIfEmpty(RxUtil.error(param.error()))
            .retry(param.retries());
    }

    /**
     * Get an error {@link Flowable} to be used when
     * {@link #rxElementsByXPath(ByXPath...)} fails to emit any
     * {@link WebElement}. We need to aggregate all error messages from
     * the {@link ByXPath} varargs parameter.
     * @param param A varargs of {@link ByXPath} instances.
     * @param <T> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see ByXPath#error()
     * @see String#join(CharSequence, CharSequence...)
     * @see RxUtil#error()
     */
    @NotNull
    default <T> Flowable<T> rxXPathQueryFailure(@NotNull ByXPath...param) {
        return Flowable
            .fromArray(param)
            .map(ByXPath::error)
            .toList()
            .map(a -> a.toArray(new String[a.size()]))
            .map(a -> String.join("\n", a))
            .toFlowable()
            .flatMap(RxUtil::error);
    }

    /**
     * Find all elements that satisfies a varargs of {@link ByXPath} requests.
     * This method can be used to find alternate versions of the same
     * {@link WebElement}, e.g. in case it has different texts for different
     * platforms.
     * @param param A varargs of {@link ByXPath} instances.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rxElementsByXPath(@NotNull ByXPath...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .subscribeOn(Schedulers.computation())
            .flatMap(a -> THIS.rxElementsByXPath(a).onErrorResumeNext(Flowable.empty()))
            .toList()
            .toFlowable()
            .flatMap(Flowable::fromIterable)
            .observeOn(Schedulers.trampoline())
            .switchIfEmpty(rxXPathQueryFailure(param));
    }

    /**
     * Find an element that satisfies a varargs of {@link ByXPath} request.
     * @param param A vararg of {@link ByXPath} instances.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rxElementByXPath(@NotNull ByXPath...param) {
        return rxElementsByXPath(param).firstElement().toFlowable();
    }
    //endregion

    //region With Class
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     */
    @NotNull
    default <P extends OfClassType & RetryType> ByXPath ofClassQuery(@NotNull P param) {
        XPath xPath = newXPathBuilder().ofClass(param).build();

        return ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsWithClass(param.value()))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link BaseViewType} elements of some classes.
     * @param param A vararg of {@link ClassParam} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends OfClassType & RetryType>
    Flowable<WebElement> rxElementsOfClass(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;
        return Flowable.fromArray(param)
            .map(THIS::ofClassQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsByXPath);
    }

    /**
     * Same as above, but uses default {@link ClassParam}.
     * @param cls A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsOfClass(OfClassType[])
     */
    @NotNull
    default Flowable<WebElement> rxElementsOfClass(@NotNull String...cls) {
        final BaseLocatorType THIS = this;
        return Flowable
            .fromArray(cls)
            .map(a -> ClassParam.builder().withClass(a).build())
            .toList()
            .map(a -> a.toArray(new ClassParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsOfClass);
    }

    /**
     * Get an element of some classes.
     * @param param A vararg of {@link ClassParam} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsOfClass(OfClassType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends OfClassType & RetryType>
    Flowable<WebElement> rxElementOfClass(@NotNull P...param) {
        return rxElementsOfClass(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link ClassParam}.
     * @param cls A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsOfClass(String...)
     */
    @NotNull
    default Flowable<WebElement> rxElementOfClass(@NotNull String...cls) {
        return rxElementsOfClass(cls).firstElement().toFlowable();
    }
    //endregion

    //region With ID
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     */
    @NotNull
    default <P extends ContainsIDType & RetryType> ByXPath containsIDQuery(@NotNull P param) {
        XPath xPath = newXPathBuilder().containsID(param).build();

        return ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsWithId(param.value()))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link BaseViewType} elements whose IDs contain certain
     * {@link String} values.
     * @param param A varargs of {@link IdParam} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends ContainsIDType & RetryType>
    Flowable<WebElement> rxElementsContainingID(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::containsIDQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsByXPath);
    }

    /**
     * Same as above, but uses default {@link IdParam}.
     * @param id A vararg of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingID(ContainsIDType[])
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingID(@NotNull String...id) {
        final BaseLocatorType THIS = this;

        return Flowable
            .fromArray(id)
            .map(a -> IdParam.builder().withId(a).build())
            .toList()
            .map(a -> a.toArray(new IdParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsContainingID);
    }

    /**
     * Get an element whose ID contains certain {@link String} values.
     * @param param A varargs of {@link IdParam} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingID(ContainsIDType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends ContainsIDType & RetryType>
    Flowable<WebElement> rxElementContainingID(@NotNull P...param) {
        return rxElementsContainingID(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link IdParam}.
     * @param id A vararg of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingID(String...)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingID(@NotNull String...id) {
        return rxElementsContainingID(id).firstElement().toFlowable();
    }
    //endregion

    //region With Text
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath hasTextQuery(@NotNull P param) {
        String localized = localizer().localize(param.value());

        TextParam newParam = TextParam.builder()
            .withText(localized)
            .withRetryType(param)
            .shouldIgnoreCase(param)
            .build();

        XPath xPath = newXPathBuilder().hasText(newParam).build();

        return ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsWithText(localized))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link BaseViewType#hasText()} {@link WebElement} that are
     * displaying some texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rxElementsWithText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::hasTextQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsByXPath);
    }

    /**
     * Same as above, but uses default {@link TextParam} with specified texts.
     * @param text A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsWithText(StringType[])
     */
    @NotNull
    default Flowable<WebElement> rxElementsWithText(@NotNull String...text) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsWithText);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} that is
     * displaying some texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsWithText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rxElementWithText(@NotNull P...param) {
        return rxElementsWithText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link TextParam} instances.
     * @param text A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsWithText(String...)
     */
    @NotNull
    default Flowable<WebElement> rxElementWithText(@NotNull String text) {
        return rxElementsWithText(text).firstElement().toFlowable();
    }
    //endregion

    //region Contains Text
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath containsTextQuery(@NotNull P param) {
        String localized = localizer().localize(param.value());

        TextParam newParam = TextParam.builder()
            .withText(localized)
            .withRetryType(param)
            .shouldIgnoreCase(param)
            .build();

        XPath xPath = newXPathBuilder().containsText(newParam).build();

        return ByXPath.builder()
            .withXPath(xPath)
            .withError(noElementsContainingText(localized))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link BaseViewType#hasText()} {@link WebElement} whose texts
     * contain some another texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsByXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rxElementsContainingText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::containsTextQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsByXPath);
    }

    /**
     * Same as above, but uses default {@link TextParam} with a specified
     * texts.
     * @param text A vararg of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(StringType[])
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingText(@NotNull String...text) {
        final BaseLocatorType<?> THIS = this;
        return Flowable
            .fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsContainingText);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} whose text
     * contains some other texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rxElementContainingText(@NotNull P...param) {
        return rxElementsContainingText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link TextParam}.
     * @param text A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(String...)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingText(@NotNull String...text) {
        return rxElementsContainingText(text).firstElement().toFlowable();
    }

    /**
     * Get a {@link TextParam} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link TextParam} instance.
     * @see #newXPathBuilder()
     */
    @NotNull
    default <P extends FormatType & RetryType> TextParam containsTextQuery(@NotNull P param) {
        String localized = localizer().localize(param.value());

        return TextParam.builder()
            .withText(localized)
            .shouldIgnoreCase(param)
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link BaseViewType#hasText()} {@link WebElement} whose texts
     * contain some other texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends FormatType & RetryType> Flowable<WebElement>
    rxElementsContainingText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::containsTextQuery)
            .toList()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsContainingText);
    }

    /**
     * Same as above, but uses default {@link TextFormatParam}.
     * @param format A vararg of {@link LCFormat} instances.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(FormatType[])
     */
    @NotNull
    default Flowable<WebElement> rxElementsContainingText(@NotNull LCFormat...format) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(format)
            .map(a -> TextFormatParam.builder().withLocalizationFormat(a).build())
            .toList()
            .map(a -> a.toArray(new TextFormatParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxElementsContainingText);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} whose text
     * contains some other texts.
     * @param param A vararg of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(FormatType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends FormatType & RetryType>
    Flowable<WebElement> rxElementContainingText(@NotNull P...param) {
        return rxElementsContainingText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link TextFormatParam}.
     * @param format A {@link LCFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rxElementsContainingText(LCFormat...)
     */
    @NotNull
    default Flowable<WebElement> rxElementContainingText(@NotNull LCFormat...format) {
        return rxElementsContainingText(format).firstElement().toFlowable();
    }
    //endregion

    //region Editable Elements
    /**
     * Get all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #platformView()
     * @see PlatformView#isEditable()
     * @see #rxElementsByXPath(ByXPath...)
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
     * @see #rxElementsByXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rxAllClickableElements() {
        XPath xPath = newXPathBuilder().isClickable(true).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_CLICKABLE_ELEMENT)
            .build();

        return rxElementsByXPath(query);
    }
    //endregion
}
