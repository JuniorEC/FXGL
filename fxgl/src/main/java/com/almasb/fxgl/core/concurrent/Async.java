/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.core.concurrent;

import java.util.concurrent.Callable;

/**
 * Easy way of invoking async tasks.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Async<T> {

    /**
     * Starts an async task in a background thread.
     *
     * @param func function to run for result
     * @param <T> result type
     * @return an async object
     */
    public static <T> Async<T> start(Callable<T> func) {
        return new Coroutine<T>(func);
    }

    /**
     * Starts an async task in a background thread.
     *
     * @param func function to run
     * @return an async object
     */
    public static Async<Void> start(Runnable func) {
        return start(() -> {
            func.run();
            return null;
        });
    }

    /**
     * Runs the task on the JavaFX UI Thread.
     *
     * @param func function to run for result
     * @param <T> result type
     * @return async object
     */
    public static <T> Async<T> startFX(Callable<T> func) {
        return new FXCoroutine<T>(func);
    }

    /**
     * Runs the task on the JavaFX UI Thread.
     *
     * @param func function to run
     * @return async object
     */
    public static Async<Void> startFX(Runnable func) {
        return startFX(() -> {
            func.run();
            return null;
        });
    }

    /**
     * Blocks current thread to wait for the result of
     * this async task.
     *
     * @return task result
     */
    public abstract T await();
}
