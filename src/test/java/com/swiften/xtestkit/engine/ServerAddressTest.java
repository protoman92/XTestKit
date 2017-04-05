package com.swiften.xtestkit.engine;

import com.swiften.xtestkit.engine.base.ServerAddress;
import com.swiften.xtestkit.util.Log;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.testng.annotations.Test;

/**
 * Created by haipham on 4/5/17.
 */
public class ServerAddressTest {
    @NotNull private final ServerAddress SERVER_ADDRESS;

    {
        SERVER_ADDRESS = Mockito.spy(ServerAddress.DEFAULT);
    }

    @Test
    public void mock_serverAddress_shouldReturnCorrectUri() {
        // Setup

        // When
        for (int i = 0; i < 10; i++) {
            Log.println(SERVER_ADDRESS.uri());
        }

        // Then
    }
}
