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

package com.almasb.fxgl.service;

import com.almasb.fxgl.core.concurrent.Async;
import javafx.util.Duration;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

/**
 * Asynchronous executor service.
 * Allows submitting tasks to be run in the background, including after a certain delay.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Executor extends java.util.concurrent.Executor {

    /**
     * Schedule a single action to run after delay.
     * Unlike MasterTimer service, this is not blocked by game execution
     * and runs even if the game is paused.
     *
     * @param action the action
     * @param delay delay
     * @return scheduled future which can be cancelled
     */
    ScheduledFuture<?> schedule(Runnable action, Duration delay);

    /**
     * Instantly starts a non-blocking async task.
     *
     * @param func the code to run
     * @param <T> return type of the code block
     * @return async object
     */
    <T> Async<T> async(Callable<T> func);

    /**
     * Instantly starts a non-blocking async task.
     *
     * @param func the code to run
     * @return async object
     */
    Async<Void> async(Runnable func);
}
