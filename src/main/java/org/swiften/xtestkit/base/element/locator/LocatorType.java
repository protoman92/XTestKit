package org.swiften.xtestkit.base.element.locator;

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
import org.swiften.javautilities.localizer.LCFormat;
import org.swiften.javautilities.localizer.LocalizerProviderType;
import org.swiften.javautilities.localizer.LocalizerType;
import org.swiften.javautilities.log.LogUtil;
import org.swiften.javautilities.object.ObjectUtil;
import org.swiften.xtestkit.base.PlatformView;
import org.swiften.xtestkit.base.element.property.ElementPropertyType;
import org.swiften.xtestkit.base.type.DriverProviderType;
import org.swiften.xtestkit.base.type.PlatformViewProviderType;
import org.swiften.xtestkitcomponents.common.RetryType;
import org.swiften.xtestkitcomponents.platform.PlatformProviderType;
import org.swiften.xtestkitcomponents.property.base.FormatType;
import org.swiften.xtestkitcomponents.property.base.IgnoreCaseType;
import org.swiften.xtestkitcomponents.property.base.StringType;
import org.swiften.xtestkitcomponents.property.sub.ContainsIDType;
import org.swiften.xtestkitcomponents.property.sub.OfClassType;
import org.swiften.xtestkitcomponents.view.ViewType;
import org.swiften.xtestkitcomponents.xpath.Attribute;
import org.swiften.xtestkitcomponents.xpath.AttributeType;
import org.swiften.xtestkitcomponents.xpath.Attributes;
import org.swiften.xtestkitcomponents.xpath.XPath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This interface provides general locator capabilities.
 * @param <D> Generics parameter that extends {@link WebDriver}.
 */
public interface LocatorType<D extends WebDriver> extends
    DriverProviderType<D>,
    ElementPropertyType,
    LocalizerProviderType,
    LocatorDelayType,
    LocatorErrorType,
    PlatformProviderType,
    PlatformViewProviderType
{
    //region By XPath
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
    default <T> Flowable<T> rxe_xpathQueryFailure(@NotNull ByXPath...param) {
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(param)
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
     * @see ByXPath#logXPath()
     * @see ByXPath#retries()
     * @see ByXPath#xpath()
     * @see ObjectUtil#eq(Object)
     * @see WebDriver#findElements(By)
     * @see #driver()
     * @see #elementLocateTimeout()
     * @see #rxe_xpathQueryFailure(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default Flowable<WebElement> rxe_byXPath(@NotNull ByXPath...param) {
        final WebDriver DRIVER = driver();

        int retries = Arrays.stream(param)
            .map(ByXPath::retries)
            .max(Comparator.comparingInt(ObjectUtil::eq))
            .orElse(3);

        return Flowable.fromArray(param)
            .doOnNext(a -> {
                boolean logXPath = a.logXPath();
                String xpath = a.xpath();
                if (logXPath) LogUtil.printft("Searching for %s", xpath);
            })
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .map(ByXPath::xpath)
            .map(By::xpath)
            .flatMapIterable(DRIVER::<WebElement>findElements)
            .switchIfEmpty(rxe_xpathQueryFailure(param))
            .retry(retries);
    }
    //endregion

    //region With XPath
    /**
     * Get {@link ByXPath} from {@link XPath}.
     * @param xpath {@link XPath} instance.
     * @return {@link ByXPath} instance.
     * @see ByXPath.Builder#withXPath(XPath)
     * @see XPath#attribute()
     * @see #noSuchElement(String)
     */
    @NotNull
    default ByXPath withXPathQuery(@NotNull XPath xpath) {
        String query = xpath.attribute();
        String error = noSuchElement(query);
        return ByXPath.builder().withXPath(xpath).withError(error).build();
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
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::withXPathQuery)
            .toList().toFlowable()
            .map(a -> a.toArray(new ByXPath[a.size()]))
            .flatMap(THIS::rxe_byXPath);
    }

    /**
     * Get {@link XPath} from {@link Attribute}.
     * @param attribute {@link Attribute} instance.
     * @return {@link XPath} instance.
     * @see XPath.Builder#addAttribute(AttributeType)
     */
    @NotNull
    default XPath withAttributeQuery(@NotNull Attribute<?> attribute) {
        return XPath.builder().addAttribute(attribute).build();
    }

    /**
     * Find all {@link WebElement} that satisfy some {@link Attribute}. Beware
     * that each {@link Attribute} will be wrapped in a separate {@link XPath},
     * not used together.
     * @param attrs Varargs of {@link Attribute}.
     * @return {@link Flowable} instance.
     * @see #withAttributeQuery(Attribute)
     * @see #rxe_withXPath(XPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_withAttributes(@NotNull Attribute<?>... attrs) {
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(attrs)
            .map(THIS::withAttributeQuery)
            .toList().toFlowable()
            .map(a -> a.toArray(new XPath[a.size()]))
            .flatMap(THIS::rxe_withXPath);

    }
    //endregion

    //region With Class
    /**
     * Get {@link ByXPath} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link ByXPath} instance.
     * @see Attributes#of(PlatformProviderType)
     * @see Attributes#ofClass(String)
     * @see ByXPath.Builder#withError(String)
     * @see ByXPath.Builder#withRetryType(RetryType)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see P#value()
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see #noElementsWithClass(String)
     * @see #platform()
     */
    @NotNull
    default <P extends OfClassType & RetryType> ByXPath ofClassQuery(@NotNull P param) {
        Attributes attrs = Attributes.of(this);

        XPath xpath = XPath.builder()
            .addAttribute(attrs.ofClass(param.value()))
            .build();

        return ByXPath.builder()
            .withXPath(xpath)
            .withError(noElementsWithClass(param.value()))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link ViewType} elements of some classes.
     * @param param A vararg of {@link ClassParam} instances.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #ofClassQuery(OfClassType)
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends OfClassType & RetryType> Flowable<WebElement>
    rxe_ofClass(@NotNull P...param) {
        final LocatorType<?> THIS = this;

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
        final LocatorType THIS = this;

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
     * @see Attributes#of(PlatformProviderType)
     * @see Attributes#containsID(String)
     * @see ByXPath.Builder#withError(String)
     * @see ByXPath.Builder#withRetryType(RetryType)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see P#value()
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see #noElementsWithId(String)
     * @see #platform()
     */
    @NotNull
    default <P extends ContainsIDType & RetryType> ByXPath containsIDQuery(@NotNull P param) {
        Attributes attrs = Attributes.of(this);

        XPath xpath = XPath.builder()
            .addAttribute(attrs.containsID(param.value()))
            .build();

        return ByXPath.builder()
            .withXPath(xpath)
            .withError(noElementsWithId(param.value()))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link ViewType} elements whose IDs contain certain
     * {@link String} values.
     * @param param A varargs of {@link IdParam} instances.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends ContainsIDType & RetryType> Flowable<WebElement>
    rxe_containsID(@NotNull P...param) {
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::containsIDQuery)
            .toList().map(a -> a.toArray(new ByXPath[a.size()]))
            .toFlowable()
            .flatMap(THIS::rxe_byXPath);
    }

    /**
     * Same as above, but uses default {@link IdParam}.
     * @param id A vararg of {@link String} values.
     * @return {@link Flowable} instance.
     * @see IdParam#builder()
     * @see IdParam.Builder#withId(String)
     * @see #rxe_containsID(ContainsIDType[])
     */
    @NotNull
    default Flowable<WebElement> rxe_containsID(@NotNull String...id) {
        final LocatorType THIS = this;

        return Flowable.fromArray(id)
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
     * @see Attributes#hasText(String)
     * @see Attributes#of(PlatformProviderType)
     * @see ByXPath.Builder#withError(String)
     * @see ByXPath.Builder#withRetryType(RetryType)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see LocalizerType#localize(String)
     * @see P#value()
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see #localizer()
     * @see #noElementsWithText(String)
     * @see #platform()
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath hasTextQuery(@NotNull P param) {
        LocalizerType localizer = localizer();
        String localized = localizer.localize(param.value());
        Attributes attrs = Attributes.of(this);

        XPath xpath = XPath.builder()
            .addAttribute(attrs.hasText(localized))
            .build();

        return ByXPath.builder()
            .withXPath(xpath)
            .withError(noElementsWithText(localized))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link ViewType#hasText()} {@link WebElement} that are
     * displaying some texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #hasTextQuery(StringType)
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType> Flowable<WebElement>
    rxe_withText(@NotNull P...param) {
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(param)
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
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(text)
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
     * @see Attributes#of(PlatformProviderType)
     * @see ByXPath.Builder#withError(String)
     * @see ByXPath.Builder#withRetryType(RetryType)
     * @see ByXPath.Builder#withXPath(XPath)
     * @see LocalizerType#localize(String)
     * @see P#value()
     * @see TextParam.Builder#withText(String)
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see #localizer()
     * @see #noElementsContainingText(String)
     * @see #platform()
     */
    @NotNull
    default <P extends StringType & RetryType> ByXPath containsTextQuery(@NotNull P param) {
        LocalizerType localizer = localizer();
        String localized = localizer.localize(param.value());
        Attributes attrs = Attributes.of(this);

        XPath xpath = XPath.builder()
            .addAttribute(attrs.containsText(localized))
            .build();

        return ByXPath.builder()
            .withXPath(xpath)
            .withError(noElementsContainingText(localized))
            .withRetryType(param)
            .build();
    }

    /**
     * Get all {@link ViewType#hasText()} {@link WebElement} whose texts
     * contain some another texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #containsTextQuery(StringType)
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends StringType & RetryType> Flowable<WebElement>
    rxe_containsText(@NotNull P...param) {
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(param)
            .map(THIS::containsTextQuery)
            .toList().toFlowable()
            .map(a -> a.toArray(new ByXPath[a.size()]))
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
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(text)
            .map(a -> TextParam.builder().withText(a).build())
            .toList().toFlowable()
            .map(a -> a.toArray(new TextParam[a.size()]))
            .flatMap(THIS::rxe_containsText);
    }

    /**
     * Get {@link TextParam} from {@link P} param.
     * @param param {@link P} instance.
     * @param <P> Generics parameter.
     * @return {@link TextParam} instance.
     * @see LocalizerType#localize(LCFormat)
     * @see P#value()
     * @see TextParam.Builder#shouldIgnoreCase(IgnoreCaseType)
     * @see TextParam.Builder#withRetryType(RetryType)
     * @see TextParam.Builder#withText(String)
     * @see #localizer()
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
     * Get all {@link ViewType#hasText()} {@link WebElement} whose texts
     * contain some other texts.
     * @param param A varargs of {@link P} instances.
     * @param <P> Generics parameter.
     * @return {@link Flowable} instance.
     * @see #containsTextQuery(FormatType)
     * @see #rxe_containsText(StringType[])
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <P extends FormatType & RetryType> Flowable<WebElement>
    rxe_containsText(@NotNull P...param) {
        final LocatorType<?> THIS = this;

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
        final LocatorType<?> THIS = this;

        return Flowable.fromArray(format)
            .map(a -> TextFormatParam.builder().withLCFormat(a).build())
            .toList().toFlowable()
            .map(a -> a.toArray(new TextFormatParam[a.size()]))
            .flatMap(THIS::rxe_containsText);
    }
    //endregion

    //region Editable Elements
    /**
     * Get all {@link ViewType#isEditable()} {@link WebElement}.
     * @return {@link Flowable} instance.
     * @see Attributes#of(PlatformProviderType)
     * @see Attributes#ofClass(String)
     * @see ViewType#className()
     * @see ByXPath.Builder#withXPath(XPath)
     * @see PlatformView#isEditable()
     * @see XPath.Builder#addAttribute(AttributeType)
     * @see #platform()
     * @see #platformView()
     * @see #rxe_byXPath(ByXPath...)
     */
    @NotNull
    default Flowable<WebElement> rxe_editables() {
        List<? extends ViewType> views = platformView().isEditable();
        final Attributes ATTRS = Attributes.of(this);

        ByXPath[] queries = views.stream()
            .map(ViewType::className)
            .map(a -> XPath.builder().addAttribute(ATTRS.ofClass(a)).build())
            .map(a -> ByXPath.builder().withXPath(a).build())
            .toArray(ByXPath[]::new);

        return rxe_byXPath(queries);
    }

    /**
     * Clear all {@link ViewType#isEditable()} {@link WebElement}.
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
     * Get the currently focused {@link ViewType#isEditable()} field.
     * @return {@link Flowable} instance.
     * @see #isFocused(WebElement)
     * @see #rxe_editables()
     */
    @NotNull
    default Flowable<WebElement> rxe_currentlyFocusedEditable() {
        final LocatorType<?> THIS = this;

        return rxe_editables()
            .filter(THIS::isFocused)
            .firstElement().toFlowable();
    }
    //endregion

    /**
     * Get the status bar {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<WebElement> rxe_statusBar();

    /**
     * Get the window {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<WebElement> rxe_window();

    /**
     * Get all image views {@link WebElement}.
     * @return {@link Flowable} instance.
     */
    @NotNull Flowable<WebElement> rxe_imageViews();
}
