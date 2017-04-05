package com.swiften.testapplication.sample.common;

import com.swiften.testapplication.sample.Config;
import com.swiften.testapplication.sample.test.TestApplicationRunner;
import com.swiften.xtestkit.engine.base.PlatformEngine;
import com.swiften.xtestkit.engine.base.param.AfterClassParam;
import com.swiften.xtestkit.engine.base.param.AfterParam;
import com.swiften.xtestkit.engine.base.param.BeforeClassParam;
import com.swiften.xtestkit.engine.base.param.BeforeParam;
import com.swiften.xtestkit.kit.TestKit;
import com.swiften.xtestkit.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * Created by haipham on 4/4/17.
 */
//@RunWith(TestApplicationRunner.class)
public class BaseUITest {
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

    public BaseUITest(int index) {
        INDEX = index;
        TEST_KIT = Config.TEST_KIT;
        INTERACTION = new Interaction(TEST_KIT, index);
    }

    @BeforeClass
    public void beforeClass() {
        /* Calling beforeClass() here ensures that each PlatformEngine will
         * only start the test environment once */
        TEST_KIT.beforeClass(beforeClassParam());
    }

    @AfterClass
    public void afterClass() {
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
        return BeforeClassParam.newBuilder().withIndex(INDEX).build();
    }

    @NotNull
    private AfterClassParam afterClassParam() {
        return AfterClassParam.newBuilder().withIndex(INDEX).build();
    }

    @NotNull
    private BeforeParam beforeParam() {
        return BeforeParam.newBuilder().withIndex(INDEX).build();
    }

    @NotNull
    private AfterParam afterParam() {
        return AfterParam.newBuilder().withIndex(INDEX).build();
    }
}
