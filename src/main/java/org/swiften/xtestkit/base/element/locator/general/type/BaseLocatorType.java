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
import org.swiften.xtestkit.base.element.locator.general.xpath.type.NewXPathBuilderType;
import org.swiften.xtestkit.base.element.property.type.base.FormatType;
import org.swiften.xtestkit.base.element.property.type.base.StringType;
import org.swiften.xtestkit.base.element.property.type.sub.ContainsIDType;
import org.swiften.xtestkit.base.element.property.type.sub.OfClassType;
import org.swiften.xtestkit.base.type.*;

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
    NewXPathBuilderType,
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
    @Override
    default XPath.Builder newXPathBuilder() {
        return XPath.builder(platform());
    }

    /**
     * Find all elements that satisfies an {@link XPath} request.
     * @param param A {@link ByXPath} instance.
     * @return A {@link Flowable} instance.
     * @see #driver()
     * @see ByXPath#classes()
     * @see ByXPath#xPath()
     * @see ByXPath#retries()
     * @see BaseViewType#className()
     * @see ObjectUtil#nonNull(Object)
     * @see D#findElements(By)
     * @see CollectionUtil#unify(Collection[])
     * @see RxUtil#error(String)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rx_elementsByXPath(@NotNull ByXPath param) {
        final WebDriver DRIVER = driver();
        final String XPATH = param.xPath();
        List<BaseViewType> classes = param.classes();

        return Flowable.fromIterable(classes)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map(cls -> String.format("//%1$s%2$s", cls.className(), XPATH))
            .doOnNext(a -> LogUtil.printfThread("Searching for %s", a))
            .concatMapIterable(path -> {
                try {
                    /* Check for error here just to be certain */
                    return DRIVER.findElements(By.xpath(path));
                } catch (Exception e) {
                    return Collections.<WebElement>emptyList();
                }
            })
            .filter(ObjectUtil::nonNull)
            .switchIfEmpty(RxUtil.error(param.error()))
            .retry(param.retries());
    }

    /**
     * Get an error {@link Flowable} to be used when
     * {@link #rx_elementsByXPath(ByXPath...)} fails to emit any
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
    default <T> Flowable<T> rx_xPathQueryFailure(@NotNull ByXPath...param) {
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
     * @see #rx_elementsByXPath(ByXPath)
     * @see #rx_xPathQueryFailure(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rx_elementsByXPath(@NotNull ByXPath...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .flatMap(a -> THIS.rx_elementsByXPath(a).onErrorResumeNext(Flowable.empty()))
            .toList()
            .toFlowable()
            .flatMap(Flowable::fromIterable)
            .switchIfEmpty(rx_xPathQueryFailure(param));
    }

    /**
     * Find an element that satisfies a varargs of {@link ByXPath} request.
     * @param param A vararg of {@link ByXPath} instances.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsByXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementByXPath(@NotNull ByXPath...param) {
        return rx_elementsByXPath(param).firstElement().toFlowable();
    }
    //endregion

    //region With XPath

    /**
     * Get a {@link ByXPath} from a {@link XPath}.
     * @param xPath A {@link XPath} instance.
     * @return A {@link ByXPath} instance.
     * @see #NO_SUCH_ELEMENT
     */
    @NotNull
    default ByXPath withXPathQuery(@NotNull XPath xPath) {
        return ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_SUCH_ELEMENT)
            .build();
    }

    /**
     * Find all {@link WebElement} that satisfy some {@link XPath} queries.
     * @param param A varargs of {@link XPath}.
     * @return A {@link Flowable} instance.
     * @see #withXPathQuery(XPath)
     * @see #rx_elementByXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementsWithXPath(@NotNull XPath...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::withXPathQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsByXPath);
    }

    /**
     * Get the first {@link WebElement} that satisfies some {@link XPath}
     * queries.
     * @param param A varargs of {@link XPath}.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsWithXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementWithXPath(@NotNull XPath...param) {
        return rx_elementsWithXPath(param).firstElement().toFlowable();
    }
    //endregion

    //region With Class
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     * @see XPath.Builder#ofClass(XPath.OfClass)
     * @see P#value()
     * @see #noElementsWithClass(String)
     */
    @NotNull
    default <P extends OfClassType & RetryType>
    ByXPath ofClassQuery(@NotNull P param) {
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
     * @see #ofClassQuery(OfClassType)
     * @see #rx_elementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends OfClassType & RetryType>
    Flowable<WebElement> rx_elementsOfClass(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::ofClassQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsByXPath);
    }

    /**
     * Same as above, but uses default {@link ClassParam}.
     * @param cls A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see ClassParam.Builder#withClass(String)
     * @see #rx_elementsOfClass(OfClassType[])
     */
    @NotNull
    default Flowable<WebElement> rx_elementsOfClass(@NotNull String...cls) {
        final BaseLocatorType THIS = this;

        return Flowable
            .fromArray(cls)
            .map(a -> ClassParam.builder().withClass(a).build())
            .toList()
            .map(a -> a.toArray(new ClassParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsOfClass);
    }

    /**
     * Get an element of some classes.
     * @param param A vararg of {@link ClassParam} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsOfClass(OfClassType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends OfClassType & RetryType>
    Flowable<WebElement> rx_elementOfClass(@NotNull P...param) {
        return rx_elementsOfClass(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link ClassParam}.
     * @param cls A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsOfClass(String...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementOfClass(@NotNull String...cls) {
        return rx_elementsOfClass(cls).firstElement().toFlowable();
    }
    //endregion

    //region With ID
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     * @see XPath.Builder#containsID(XPath.ContainsID)
     * @see #noElementsWithId(String)
     */
    @NotNull
    default <P extends ContainsIDType & RetryType>
    ByXPath containsIDQuery(@NotNull P param) {
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
     * @see #rx_elementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends ContainsIDType & RetryType>
    Flowable<WebElement> rx_elementsContainingID(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::containsIDQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsByXPath);
    }

    /**
     * Same as above, but uses default {@link IdParam}.
     * @param id A vararg of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingID(ContainsIDType[])
     */
    @NotNull
    default Flowable<WebElement> rx_elementsContainingID(@NotNull String...id) {
        final BaseLocatorType THIS = this;

        return Flowable
            .fromArray(id)
            .map(a -> IdParam.builder().withId(a).build())
            .toList()
            .map(a -> a.toArray(new IdParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsContainingID);
    }

    /**
     * Get an element whose ID contains certain {@link String} values.
     * @param param A varargs of {@link IdParam} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingID(ContainsIDType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends ContainsIDType & RetryType>
    Flowable<WebElement> rx_elementContainingID(@NotNull P...param) {
        return rx_elementsContainingID(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link IdParam}.
     * @param id A vararg of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingID(String...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementContainingID(@NotNull String...id) {
        return rx_elementsContainingID(id).firstElement().toFlowable();
    }
    //endregion

    //region With Text
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     * @see #localizer()
     * @see org.swiften.javautilities.localizer.LocalizerType#localize(String)
     * @see P#value()
     * @see XPath.Builder#hasText(String)
     * @see #noElementsWithText(String)
     */
    @NotNull
    default <P extends StringType & RetryType>
    ByXPath hasTextQuery(@NotNull P param) {
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
     * @see #hasTextQuery(StringType)
     * @see #rx_elementsByXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rx_elementsWithText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::hasTextQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsByXPath);
    }

    /**
     * Same as above, but uses default {@link TextParam} with specified texts.
     * @param text A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see TextParam.Builder#withText(String)
     * @see #rx_elementsWithText(StringType[])
     */
    @NotNull
    default Flowable<WebElement> rx_elementsWithText(@NotNull String...text) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsWithText);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} that is
     * displaying some texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsWithText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rx_elementWithText(@NotNull P...param) {
        return rx_elementsWithText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link TextParam} instances.
     * @param text A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsWithText(String...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementWithText(@NotNull String text) {
        return rx_elementsWithText(text).firstElement().toFlowable();
    }
    //endregion

    //region Contains Text
    /**
     * Get a {@link ByXPath} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link ByXPath} instance.
     * @see #newXPathBuilder()
     * @see P#value()
     * @see #localizer()
     * @see org.swiften.javautilities.localizer.LocalizerType#localize(String)
     * @see TextParam.Builder#withText(String)
     * @see XPath.Builder#containsText(StringType)
     * @see #noElementsContainingText(String)
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath
    containsTextQuery(@NotNull P param) {
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
     * @see #containsTextQuery(StringType)
     * @see #rx_elementsByXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rx_elementsContainingText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::containsTextQuery)
            .toList()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsByXPath);
    }

    /**
     * Same as above, but uses default {@link TextParam} with a specified
     * texts.
     * @param text A vararg of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see TextParam.Builder#withText(String)
     * @see #rx_elementsContainingText(StringType[])
     */
    @NotNull
    default Flowable<WebElement> rx_elementsContainingText(@NotNull String...text) {
        final BaseLocatorType<?> THIS = this;
        return Flowable
            .fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsContainingText);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} whose text
     * contains some other texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType>
    Flowable<WebElement> rx_elementContainingText(@NotNull P...param) {
        return rx_elementsContainingText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses default {@link TextParam}.
     * @param text A varargs of {@link String} values.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingText(String...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementContainingText(@NotNull String...text) {
        return rx_elementsContainingText(text).firstElement().toFlowable();
    }

    /**
     * Get a {@link TextParam} from a {@link P} param.
     * @param param A {@link P} instance.
     * @param <P> Generics parameter.
     * @return A {@link TextParam} instance.
     * @see #newXPathBuilder()
     * @see P#value()
     * @see #localizer()
     * @see org.swiften.javautilities.localizer.LocalizerType#localize(LCFormat)
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
     * @see #containsTextQuery(FormatType)
     * @see #rx_elementsContainingText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends FormatType & RetryType> Flowable<WebElement>
    rx_elementsContainingText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::containsTextQuery)
            .toList()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsContainingText);
    }

    /**
     * Same as above, but uses default {@link TextFormatParam}.
     * @param format A vararg of {@link LCFormat} instances.
     * @return A {@link Flowable} instance.
     * @see TextFormatParam.Builder#withLCFormat(LCFormat)
     * @see #rx_elementsContainingText(FormatType[])
     */
    @NotNull
    default Flowable<WebElement> rx_elementsContainingText(@NotNull LCFormat...format) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(format)
            .map(a -> TextFormatParam.builder().withLCFormat(a).build())
            .toList()
            .map(a -> a.toArray(new TextFormatParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rx_elementsContainingText);
    }

    /**
     * Get a {@link BaseViewType#hasText()} {@link WebElement} whose text
     * contains some other texts.
     * @param param A vararg of {@link P} instances.
     * @param <P> Generics parameter.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingText(FormatType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends FormatType & RetryType>
    Flowable<WebElement> rx_elementContainingText(@NotNull P...param) {
        return rx_elementsContainingText(param).firstElement().toFlowable();
    }

    /**
     * Same as above, but uses a default {@link TextFormatParam}.
     * @param format A {@link LCFormat} instance.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsContainingText(LCFormat...)
     */
    @NotNull
    default Flowable<WebElement> rx_elementContainingText(@NotNull LCFormat...format) {
        return rx_elementsContainingText(format).firstElement().toFlowable();
    }
    //endregion

    //region Editable Elements
    /**
     * Get all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #platformView()
     * @see PlatformView#isEditable()
     * @see ByXPath.Builder#withClasses(Collection)
     * @see #rx_elementsByXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_allEditableElements() {
        List<? extends BaseViewType> views = platformView().isEditable();
        ByXPath query = ByXPath.builder().withClasses(views).build();
        return rx_elementsByXPath(query);
    }

    /**
     * Clear all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #rx_allEditableElements()
     * @see WebElement#clear()
     */
    @NotNull
    default Flowable<Boolean> rx_clearAllEditableElements() {
        return rx_allEditableElements()
            .flatMapCompletable(a -> Completable.fromAction(a::clear))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }
    //endregion

    //region Clickable Elements
    /**
     * Get all {@link BaseViewType#isClickable()} {@link WebElement}.
     * @return A {@link Flowable} instance.
     * @see #rx_elementsByXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rx_allClickableElements() {
        XPath xPath = newXPathBuilder().isClickable(true).build();

        ByXPath query = ByXPath.builder()
            .withXPath(xPath)
            .withError(NO_CLICKABLE_ELEMENT)
            .build();

        return rx_elementsByXPath(query);
    }
    //endregion
}
