package com.example.sampleproject.controller;

class ServerControllerTest {

/*
    @Test
    void testServerController() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ServerService serverService = new ServerServiceImpl();

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                try {
                    ServerModel server=serverService.allocate(50);
                } catch (AppConfigExceptions appConfigExceptions) {
                    appConfigExceptions.printStackTrace();
                }
            });
        }
        latch.await();
    }
*/
}