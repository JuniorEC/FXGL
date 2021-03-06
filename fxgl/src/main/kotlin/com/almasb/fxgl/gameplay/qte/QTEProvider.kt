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

package com.almasb.fxgl.gameplay.qte

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.SubState
import javafx.event.EventHandler
import javafx.geometry.Pos.CENTER
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.layout.HBox
import javafx.util.Duration
import java.util.*
import java.util.function.Consumer

/**
 * FXGL default QTE service provider.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class QTEProvider : QTE {

    private val qteState = QTEState()
    private val qteKeys = ArrayDeque<QTEKey>()

    private lateinit var callback: Consumer<Boolean>

    init {
        val eventHandler = EventHandler<KeyEvent> {

            val qteKey = qteKeys.poll()

            if (qteKey.keyCode == it.code) {
                qteKey.lightUp()

                if (qteKeys.isEmpty()) {
                    close()
                    callback.accept(true)
                }

            } else {
                close()
                callback.accept(false)
            }
        }

        qteState.input.addEventHandler(KEY_PRESSED, eventHandler)
    }

    private fun show() {
        FXGL.getApp().stateMachine.pushState(qteState)
    }

    private fun close() {
        qteKeys.clear()

        FXGL.getApp().stateMachine.popState()
    }

    /**
     * Starts quick time event.
     * Game execution is blocked during the event.
     * The event can be finishes if one of the following conditions is met:
     * <ul>
     *     <li>User runs out of time (fail)</li>
     *     <li>User presses the wrong key (fail)</li>
     *     <li>User correctly presses all keys (success)</li>
     * </ul>
     *
     * @param callback called with true if user succeeds in the event, false otherwise
     * @param duration how long the event should last
     * @param keys what keys need to be pressed
     */
    override fun start(callback: Consumer<Boolean>, duration: Duration, vararg keys: KeyCode) {
        if (keys.isEmpty())
            throw IllegalArgumentException("At least 1 key must be specified")

        if (qteKeys.isNotEmpty())
            throw IllegalStateException("Cannot start more than 1 QTE at a time")

        this.callback = callback

        qteKeys.addAll(keys.map(::QTEKey))

        qteState.setKeys(qteKeys)

        show()

        qteState.timer.runOnceAfter({

            if (qteKeys.isNotEmpty()) {
                close()
                callback.accept(false)
            }
        }, duration)
    }

    private class QTEState : SubState() {

        private val keysBox = HBox(10.0)

        init {
            keysBox.alignment = CENTER

            children.add(keysBox)
        }

        fun setKeys(keys: Deque<QTEKey>) {
            keysBox.children.setAll(keys)

            view.translateX = FXGL.getAppWidth() / 2 - keysBox.children.size * 82 / 2.0
            view.translateY = FXGL.getAppHeight() / 2 - 72 / 2.0
        }

        override fun onExit() {
            timer.clear()
        }
    }
}