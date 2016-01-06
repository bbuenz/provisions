/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingExecutor
extends ThreadPoolExecutor {
    public BlockingExecutor() {
        super(Runtime.getRuntime().availableProcessors() - 1, Runtime.getRuntime().availableProcessors() - 1, 2, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors() * 4), new CallerRunsPolicy());
    }
}

