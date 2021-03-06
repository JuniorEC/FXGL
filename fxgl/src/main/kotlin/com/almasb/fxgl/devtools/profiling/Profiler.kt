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

package com.almasb.fxgl.devtools.profiling

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.core.math.FXGLMath
import javafx.scene.canvas.GraphicsContext
import javafx.scene.effect.BlendMode
import javafx.scene.paint.Color

/**
 * Basic profiler.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Profiler {

    companion object {
        private val runtime = Runtime.getRuntime()

        private val MB = 1024.0f * 1024.0f
    }

    // set to 1 to avoid div by 0
    var frames = 1
        private set

    private var fps = 0
    private var currentFPS = 0
    private var currentTimeTook = 0L

    fun getAvgFPS() = fps / frames

    private var timeTook = 0L

    /**
     * @return time took in nanoseconds
     */
    fun getAvgTimeTook() = timeTook / frames

    /**
     * @return time took in milliseconds
     */
    fun getAvgTimeTookRounded() = "%.2f".format(getAvgTimeTook() / 1_000_000.0)

    private var memoryUsage = 0L
    private var memoryUsageMin = Long.MAX_VALUE
    private var memoryUsageMax = 0L
    private var memoryUsageCurrent = 0L

    /**
     * @return average memory usage in MB
     */
    fun getAvgMemoryUsage() = memoryUsage / frames / MB

    fun getAvgMemoryUsageRounded() = FXGLMath.roundPositive(getAvgMemoryUsage())

    /**
     * @return max (highest peak) memory usage in MB
     */
    fun getMaxMemoryUsage() = memoryUsageMax / MB

    fun getMaxMemoryUsageRounded() = FXGLMath.roundPositive(getMaxMemoryUsage())

    /**
     * @return min (lowest peak) memory usage in MB
     */
    fun getMinMemoryUsage() = memoryUsageMin / MB

    fun getMinMemoryUsageRounded() = FXGLMath.roundPositive(getMinMemoryUsage())

    /**
     * @return how much memory is used at this moment in MB
     */
    fun getCurrentMemoryUsage() = memoryUsageCurrent / MB

    fun getCurrentMemoryUsageRounded() = FXGLMath.roundPositive(getCurrentMemoryUsage())

    private var gcRuns = 0

    fun update(fps: Int, timeTook: Long) {
        frames++

        currentFPS = fps
        currentTimeTook = timeTook

        this.fps += fps
        this.timeTook += timeTook

        val used = runtime.totalMemory() - runtime.freeMemory()

        // ignore incorrect readings
        if (used < 0)
            return

        if (used < memoryUsageCurrent) {
            gcRuns++
        }

        memoryUsageCurrent = used
        memoryUsage += memoryUsageCurrent

        if (memoryUsageCurrent > memoryUsageMax)
            memoryUsageMax = memoryUsageCurrent

        if (memoryUsageCurrent < memoryUsageMin)
            memoryUsageMin = memoryUsageCurrent
    }

    private val profilerFont = FXGLAssets.UI_MONO_FONT.newFont(20.0);

    fun render(g: GraphicsContext) {
        g.globalBlendMode = BlendMode.SRC_OVER
        g.globalAlpha = 1.0
        g.font = profilerFont
        g.fill = Color.RED

        g.fillText(getInfo(), 0.0, FXGL.getAppHeight() - 120.0)
    }

    fun print() {
        val log = FXGLLogger.get(javaClass)

        log.info("Processed Frames: $frames")
        log.info("Average FPS: ${getAvgFPS()}")
        log.info("Avg Frame Took: ${getAvgTimeTookRounded()} ms")
        log.info("Avg Memory Usage: ${getAvgMemoryUsageRounded()} MB")
        log.info("Min Memory Usage: ${getMinMemoryUsageRounded()} MB")
        log.info("Max Memory Usage: ${getMaxMemoryUsageRounded()} MB")
        log.info("Estimated GC runs: $gcRuns")
    }

    // the debug data max chars is ~110, so just add a margin
    // cache string builder to avoid object allocation
    private val sb = StringBuilder(128)

    fun getInfo(): String {
        // first clear the contents
        sb.setLength(0)
        sb.append("FPS: ").append(currentFPS)
            .append("\nLast Frame (ms): ").append("%.0f".format(currentTimeTook / 1_000_000.0))
            .append("\nNow Mem (MB): ").append(getCurrentMemoryUsageRounded())
            .append("\nAvg Mem (MB): ").append(getAvgMemoryUsageRounded())
            .append("\nMin Mem (MB): ").append(getMinMemoryUsageRounded())
            .append("\nMax Mem (MB): ").append(getMaxMemoryUsageRounded())

        return sb.toString()
    }
}