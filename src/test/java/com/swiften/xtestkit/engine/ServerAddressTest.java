package com.swiften.xtestkit.engine;

import com.swiften.xtestkit.engine.base.ServerAddress;
import org.jetbrains.annotations.NotNull;
import static org.mockito.Mockito.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by haipham on 4/5/17.
 */
public class ServerAddressTest {
    @NotNull private final ServerAddress SERVER_ADDRESS;

    {
        SERVER_ADDRESS = spy(ServerAddress.defaultInstance());
    }

    @AfterMethod
    public void afterMethod() {
        reset(SERVER_ADDRESS);
    }

    @Test
    public void mock_serverAddress_shouldReturnCorrectUris() {
        // Setup
        List<String> uris = new LinkedList<>();
        List<Integer> ports = new LinkedList<>();
        int tries = 10;

        // When
        for (int i = 0; i < tries; i++) {
            uris.add(SERVER_ADDRESS.uri());
            ports.add(SERVER_ADDRESS.port());
        }

        // Then
        verify(SERVER_ADDRESS, times(tries)).uri();
        verify(SERVER_ADDRESS, times(tries)).defaultLocalUri(anyInt());
        verify(SERVER_ADDRESS, times(tries * 2)).port();
        verifyNoMoreInteractions(SERVER_ADDRESS);
    }
}
