package org.swiften.xtestkit.base.element.locator.type;

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
import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.element.locator.param.*;
import org.swiften.xtestkit.base.element.locator.xpath.XPath;
import org.swiften.xtestkit.base.element.property.BaseElementPropertyType;
import org.swiften.xtestkit.base.element.property.base.FormatType;
import org.swiften.xtestkit.base.element.property.base.StringType;
import org.swiften.xtestkit.base.element.property.sub.ContainsIDType;
import org.swiften.xtestkit.base.element.property.sub.OfClassType;
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
    BaseElementPropertyType,
    BaseLocatorErrorType,
    PlatformContainerType,
    PlatformViewContainerType
{
    //region By XPath
    /**
     * Find all elements that satisfies {@link XPath} request.
     * @param param {@link ByXPath} instance.
     * @return {@link Flowable} instance.
     * @see #driver()
     * @see ByXPath#xPath()
     * @see ByXPath#retries()
     * @see BaseViewType#className()
     * @see CollectionUtil#unify(Collection[])
     * @see ObjectUtil#nonNull(Object)
     * @see D#findElements(By)
     * @see #rxv_errorWithPageSource(String)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rxe_byXPath(@NotNull ByXPath param) {
        final WebDriver DRIVER = driver();
        final String XPATH = param.xPath();

        return Flowable.just(XPATH)
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
            .switchIfEmpty(rxv_errorWithPageSource(param.error()))
            .retry(param.retries());
    }

    /**
     * Get an error {@link Flowable} to be used when
     * {@link #rxe_byXPath(ByXPath...)} fails to emit any {@link WebElement}.
     * We need to aggregate all error messages from the
     * {@link ByXPath} varargs parameter.
     * @param param A varargs of {@link ByXPath} instances.
     * @param <T> Generics parameter.
     * @return {@link Flowable} instance.
     * @see ByXPath#error()
     * @see String#join(CharSequence, CharSequence...)
     * @see #rxv_errorWithPageSource(String)
     */
    @NotNull
    default <T> Flowable<T> rxe_xPathQueryFailure(@NotNull ByXPath...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(ByXPath::error)
            .toList()
            .map(a -> a.toArray(new String[a.size()]))
            .map(a -> String.join("\n", a))
            .toFlowable()
            .flatMap(THIS::rxv_errorWithPageSource);
    }

    /**
     * Find all elements that satisfies a varargs of {@link ByXPath} requests.
     * This method can be used to find alternate versions of the same
     * {@link WebElement}, e.g. in case it has different texts for different
     * platforms.
     * @param param A varargs of {@link ByXPath} instances.
     * @return {@link Flowable} instance.
     * @see #rxe_byXPath(ByXPath)
     * @see #rxe_xPathQueryFailure(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rxe_byXPath(@NotNull ByXPath...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .flatMap(a -> THIS.rxe_byXPath(a).onErrorResumeNext(Flowable.empty()))
            .toList().toFlowable()
            .flatMap(Flowable::fromIterable)
            .switchIfEmpty(rxe_xPathQueryFailure(param));
    }
    //endregion

    //region With XPath

    /**
     * Get {@link ByXPath} from {@link XPath}.
     * @param xPath {@link XPath} instance.
     * @return {@link ByXPath} instance.
     * @see #NO_SUCH_ELEMENT
     */
    @NotNull
    default ByXPath withXPathQuery(@NotNull XPath xPath) {
        return ByXPath.builder().withXPath(xPath).build();
    }

    /**
     * Find all {@link WebElement} that satisfy some {@link XPath} queries.
     * @param param A varargs of {@link XPath}.
     * @return {@link Flowable} instance.
     * @see #withXPathQuery(XPath)
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_withXPath(@NotNull XPath...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::withXPathQuery)
            .toList().map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_byXPath);
    }
    //endregion

    //region With Class
    /**
     * Get {@link ByXPath} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link ByXPath} instance.
     * @see P#value()
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#ofClass(XPath.OfClass)
     * @see #noElementsWithClass(String)
     * @see #platform()
     */
    @NotNull
    default <P extends OfClassType & RetryType> ByXPath ofClassQuery(@NotNull P param) {
        PlatformType platform = platform();

        XPath xPath = XPath.builder(platform)
            .ofClass(param)
            .addAnyClass()
            .build();

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
     * @return {@link Flowable} instance.
     * @see #ofClassQuery(OfClassType)
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends OfClassType & RetryType> Flowable<WebElement> rxe_ofClass(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::ofClassQuery)
            .toList().map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_byXPath);
    }

    /**
     * Same as above, but uses default {@link ClassParam}.
     * @param cls A varargs of {@link String} values.
     * @return {@link Flowable} instance.
     * @see ClassParam.Builder#withClass(String)
     * @see #rxe_ofClass(OfClassType[])
     */
    @NotNull
    default Flowable<WebElement> rxe_ofClass(@NotNull String...cls) {
        final BaseLocatorType THIS = this;

        return Flowable
            .fromArray(cls)
            .map(a -> ClassParam.builder().withClass(a).build())
            .toList().map(a -> a.toArray(new ClassParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_ofClass);
    }
    //endregion

    //region With ID
    /**
     * Get {@link ByXPath} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link ByXPath} instance.
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#containsID(XPath.ContainsID)
     * @see #noElementsWithId(String)
     * @see #platform()
     */
    @NotNull
    default <P extends ContainsIDType & RetryType> ByXPath containsIDQuery(@NotNull P param) {
        XPath xPath = XPath
            .builder(platform())
            .containsID(param)
            .addAnyClass()
            .build();

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
     * @return {@link Flowable} instance.
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends ContainsIDType & RetryType> Flowable<WebElement> rxe_containsID(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::containsIDQuery)
            .toList().map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_byXPath);
    }

    /**
     * Same as above, but uses default {@link IdParam}.
     * @param id A vararg of {@link String} values.
     * @return {@link Flowable} instance.
     * @see #rxe_containsID(ContainsIDType[])
     */
    @NotNull
    default Flowable<WebElement> rxe_containsID(@NotNull String...id) {
        final BaseLocatorType THIS = this;

        return Flowable
            .fromArray(id)
            .map(a -> IdParam.builder().withId(a).build())
            .toList().map(a -> a.toArray(new IdParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_containsID);
    }
    //endregion

    //region With Text
    /**
     * Get {@link ByXPath} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link ByXPath} instance.
     * @see org.swiften.javautilities.localizer.LocalizerType#localize(String)
     * @see P#value()
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#hasText(String)
     * @see #localizer()
     * @see #noElementsWithText(String)
     * @see #platform()
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath hasTextQuery(@NotNull P param) {
        String localized = localizer().localize(param.value());
        PlatformType platform = platform();

        TextParam newParam = TextParam.builder()
            .withText(localized)
            .withRetryType(param)
            .shouldIgnoreCase(param)
            .build();

        XPath xPath = XPath.builder(platform)
            .hasText(newParam)
            .addAnyClass()
            .build();

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
     * @return {@link Flowable} instance.
     * @see #hasTextQuery(StringType)
     * @see #rxe_byXPath(ByXPath)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType> Flowable<WebElement> rxe_withText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::hasTextQuery)
            .toList().map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_byXPath);
    }

    /**
     * Same as above, but uses default {@link TextParam} with specified texts.
     * @param text A varargs of {@link String} values.
     * @return {@link Flowable} instance.
     * @see TextParam.Builder#withText(String)
     * @see #rxe_withText(StringType[])
     */
    @NotNull
    default Flowable<WebElement> rxe_withText(@NotNull String...text) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList().map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_withText);
    }
    //endregion

    //region Contains Text
    /**
     * Get {@link ByXPath} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link ByXPath} instance.
     * @see org.swiften.javautilities.localizer.LocalizerType#localize(String)
     * @see P#value()
     * @see TextParam.Builder#withText(String)
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#containsText(StringType)
     * @see #localizer()
     * @see #noElementsContainingText(String)
     * @see #platform()
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath containsTextQuery(@NotNull P param) {
        String localized = localizer().localize(param.value());
        PlatformType platform = platform();

        TextParam newParam = TextParam.builder()
            .withText(localized)
            .withRetryType(param)
            .shouldIgnoreCase(param)
            .build();

        XPath xPath = XPath.builder(platform)
            .containsText(newParam)
            .addAnyClass()
            .build();

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
     * @return {@link Flowable} instance.
     * @see #containsTextQuery(StringType)
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType> Flowable<WebElement> rxe_containsText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(param)
            .map(THIS::containsTextQuery)
            .toList().map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_byXPath);
    }

    /**
     * Same as above, but uses default {@link TextParam} with a specified
     * texts.
     * @param text A vararg of {@link String} values.
     * @return {@link Flowable} instance.
     * @see TextParam.Builder#withText(String)
     * @see #rxe_containsText(StringType[])
     */
    @NotNull
    default Flowable<WebElement> rxe_containsText(@NotNull String...text) {
        final BaseLocatorType<?> THIS = this;
        return Flowable
            .fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList().map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_containsText);
    }

    /**
     * Get {@link TextParam} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link TextParam} instance.
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
     * @return {@link Flowable} instance.
     * @see #containsTextQuery(FormatType)
     * @see #rxe_containsText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends FormatType & RetryType> Flowable<WebElement> rxe_containsText(@NotNull P...param) {
        final BaseLocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::containsTextQuery)
            .toList().map(a -> a.toArray(new TextParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_containsText);
    }

    /**
     * Same as above, but uses default {@link TextFormatParam}.
     * @param format A vararg of {@link LCFormat} instances.
     * @return {@link Flowable} instance.
     * @see TextFormatParam.Builder#withLCFormat(LCFormat)
     * @see #rxe_containsText(FormatType[])
     */
    @NotNull
    default Flowable<WebElement> rxe_containsText(@NotNull LCFormat...format) {
        final BaseLocatorType<?> THIS = this;

        return Flowable
            .fromArray(format)
            .map(a -> TextFormatParam.builder().withLCFormat(a).build())
            .toList().map(a -> a.toArray(new TextFormatParam[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_containsText);
    }
    //endregion

    //region Editable Elements
    /**
     * Get all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see ByXPath.Builder#withXPath(XPath)
     * @see PlatformView#isEditable()
     * @see XPath.Builder#addAnyClass()
     * @see XPath.Builder#ofClass(String)
     * @see #platform()
     * @see #platformView()
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_editables() {
        List<? extends BaseViewType> views = platformView().isEditable();
        final PlatformType PLATFORM = platform();

        ByXPath[] queries = views.stream()
            .map(BaseViewType::className)
            .map(a -> XPath.builder(PLATFORM).ofClass(a).addAnyClass().build())
            .map(a -> ByXPath.builder().withXPath(a).build())
            .toArray(ByXPath[]::new);

        return rxe_byXPath(queries);
    }

    /**
     * Clear all {@link BaseViewType#isEditable()} {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see WebElement#clear()
     * @see #rxe_editables()
     */
    @NotNull
    default Flowable<Boolean> rxa_clearAllEditables() {
        return rxe_editables()
            .flatMapCompletable(a -> Completable.fromAction(a::clear))
            .<Boolean>toFlowable()
            .defaultIfEmpty(true);
    }

    /**
     * Get the currently focused {@link BaseViewType#isEditable()} field.
     * @return {@link Flowable} instance.
     * @see #isFocused(WebElement)
     * @see #rxe_editables()
     */
    @NotNull
    default Flowable<WebElement> rxe_currentlyFocusedEditable() {
        final BaseLocatorType<?> THIS = this;
        return rxe_editables()
            .filter(THIS::isFocused)
            .doOnNext(LogUtil::println)
            .firstElement().toFlowable()
            .doOnNext(LogUtil::println);
    }
    //endregion

    /**
     * Get the status bar {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxe_statusBar();

    /**
     * Get the window {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxe_window();

    /**
     * Get all image views {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull
    Flowable<WebElement> rxe_imageViews();
}
