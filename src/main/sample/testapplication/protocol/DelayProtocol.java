package sample.testapplication.protocol;

/**
 * Created by haipham on 3/25/17.
 */
public interface DelayProtocol {
    default long splashDelay() {
        return 2000;
    }
}