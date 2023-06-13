package ru.alemakave;

import ru.alemakave.mfstock.server.MFStockServer;
import ru.alemakave.slib.module.AbstractModule;

import java.util.ArrayList;

public class Bootstrap {
    public static ArrayList<AbstractModule> runnableModules = new ArrayList<>();

    public static void main(String... args) throws InterruptedException {
        initThreads(args);
        run();
    }

    private static void initThreads(String... args) {
        runnableModules.add(new AbstractModule("MFStock") {
            @Override
            public void run() {
                MFStockServer.run(args);
            }

        });
    }

    private static void run() throws InterruptedException {
        for (Thread thread : runnableModules) {
            thread.start();
        }

        boolean aliveAnyModule;
        do {
            aliveAnyModule = false;
            for (Thread thread : runnableModules) {
                aliveAnyModule |= thread.isAlive();
            }
            Thread.sleep(10);
        } while (aliveAnyModule);
    }
}
