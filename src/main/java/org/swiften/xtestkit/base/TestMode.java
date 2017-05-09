package org.swiften.xtestkit.base;

/**
 * Created by haipham on 3/22/17.
 */
public enum TestMode {
    /**
     * Perform tests on actual instances.
     */
    ACTUAL,

    /**
     * Perform tests on simulated instances. This depends on the platform;
     * for e.g. iOS should use Simulators, while Android, Emulators.
     */
    SIMULATED;

    /**
     * Check if tests are being run in an actual environment.
     * @return A {@link Boolean} value.
     */
    public boolean isTestingOnActualEnvironment() {
        switch (this) {
            case ACTUAL:
                return true;

            default:
                return false;
        }
    }

    /**
     * Check if tests are being run in a simulated environment.
     * @return A {@link Boolean} value.
     */
    public boolean isTestingOnSimulatedEnvironment() {
        switch (this) {
            case SIMULATED:
                return true;

            default:
                return false;
        }
    }
}
