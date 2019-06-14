package com.github.maciejkaznowski.constraintlayoutoptimizer;

import androidx.annotation.NonNull;

class OptimiserMetric {

    private final long duration;
    private final int optimizer;

    /**
     * @param duration  The duration in nanoseconds
     * @param optimizer The ConstraintLayout optimizer
     */
    OptimiserMetric(long duration, int optimizer) {
        this.duration = duration;
        this.optimizer = optimizer;
    }

    /**
     * @return The duration in ns
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @return The optimiser used, will be a bitwise OR of optimisers from {@link androidx.constraintlayout.solver.widgets.Optimizer}
     */
    public int getOptimiser() {
        return optimizer;
    }

    @NonNull
    @Override
    public String toString() {
        return duration + "ns, " +
                optimizer + ", " +
                Integer.toBinaryString(optimizer) + ", " +
                Utils.describeOptimisations(optimizer);
    }
}
