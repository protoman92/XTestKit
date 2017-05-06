package org.swiften.testapplication.test.common;

import org.swiften.testapplication.test.Config;
import org.swiften.xtestkit.kit.AfterClassParam;
import org.swiften.xtestkit.kit.AfterParam;
import org.swiften.xtestkit.kit.BeforeClassParam;
import org.swiften.xtestkit.kit.BeforeParam;
import org.swiften.xtestkit.kit.TestKit;
import org.jetbrains.annotations.NotNull;
import org.swiften.javautilities.log.LogUtil;
import org.testng.annotations.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 4/4/17.
 */
public class UIBaseTest {
    @NotNull
    @DataProvider
    public static Iterator<Object[]> dataProvider() {
        List<Object[]> data = new LinkedList<>();

        for (int i = 0, count = Config.runCount(); i < count; i++) {
            data.add(new Object[] { i });
        }

        return data.iterator();
    }

    @NotNull protected final TestKit TEST_KIT;
    @NotNull protected final Interaction INTERACTION;

    protected final int INDEX;

    public UIBaseTest(int index) {
        LogUtil.printf("Running test instance %d", index);
        INDEX = index;
        TEST_KIT = Config.TEST_KIT;
        INTERACTION = new Interaction(TEST_KIT, index);
    }

    protected long currentThread() {
        return Thread.currentThread().getId();
    }

    @BeforeSuite
    public void beforeSuite() {
        LogUtil.println(">>>>>>>>>> BeforeSuite <<<<<<<<<<");
        TEST_KIT.beforeSuite();
    }

    @AfterSuite
    public void afterSuite() {
        LogUtil.println(">>>>>>>>>> AfterSuite <<<<<<<<<<");
        TEST_KIT.afterSuite();
    }

    @BeforeClass
    public void beforeClass() {
        LogUtil.printf(">>>>>>>>>> BeforeClass for %s, thread %d <<<<<<<<<<",
            TEST_KIT.engine(INDEX),
            currentThread());

        /* Calling beforeClass() here ensures that each PlatformEngine will
         * only start the test environment once */
        TEST_KIT.beforeClass(beforeClassParam());
    }

    @AfterClass
    public void afterClass() {
        LogUtil.printf(">>>>>>>>>> AfterClass for %s, thread %d <<<<<<<<<<",
            TEST_KIT.engine(INDEX),
            currentThread());

        TEST_KIT.afterClass(afterClassParam());
    }

    @BeforeMethod
    public void beforeMethod() {
        TEST_KIT.before(beforeParam());
    }

    @AfterMethod
    public void afterMethod() {
        TEST_KIT.after(afterParam());
    }

    @NotNull
    private BeforeClassParam beforeClassParam() {
        return BeforeClassParam.builder().withIndex(INDEX).build();
    }

    @NotNull
    private AfterClassParam afterClassParam() {
        return AfterClassParam.builder().withIndex(INDEX).build();
    }

    @NotNull
    private BeforeParam beforeParam() {
        return BeforeParam.builder().withIndex(INDEX).build();
    }

    @NotNull
    private AfterParam afterParam() {
        return AfterParam.builder().withIndex(INDEX).build();
    }
}