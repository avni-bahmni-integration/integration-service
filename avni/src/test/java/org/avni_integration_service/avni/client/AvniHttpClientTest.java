package org.avni_integration_service.avni.client;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvniHttpClientTest {
    @Test
    public void setDifferentAvniConnections() {
        List<String> sessionErrors = new ArrayList<>();
        List<String> unknownThreadErrors = new ArrayList<>();

        AvniHttpClient avniHttpClient = new AvniHttpClient();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AvniSession avniSession = new AvniSession("example.com", String.format("User-%d", i), String.format("Password-%d", i), true);
            TestThreadRunner testThreadRunner = new TestThreadRunner(avniHttpClient, avniSession, sessionErrors, unknownThreadErrors);
            threads.add(new Thread(testThreadRunner));
        }

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        });

        assertEquals(0, sessionErrors.size(), "Avni Session Error");
        assertEquals(0, unknownThreadErrors.size(), "Test Error");
    }
}

class TestThreadRunner implements Runnable {
    private final AvniHttpClient avniHttpClient;
    private final AvniSession avniSession;
    private final List<String> errors;
    private final List<String> unknownThreadErrors;

    public TestThreadRunner(AvniHttpClient avniHttpClient, AvniSession avniSession, List<String> sessionErrors, List<String> unknownThreadErrors) {
        this.avniHttpClient = avniHttpClient;
        this.avniSession = avniSession;
        this.errors = sessionErrors;
        this.unknownThreadErrors = unknownThreadErrors;
    }

    @Override
    public void run() {
        avniHttpClient.setAvniSession(avniSession);
        try {
            Thread.sleep(Math.round(1000 * Math.random()));
            if (avniHttpClient.getAvniSession() != avniSession)
                errors.add(avniSession.getAvniImplUser());
        } catch (InterruptedException e) {
            e.printStackTrace();
            unknownThreadErrors.add(avniSession.getAvniImplUser());
        }
    }
}
