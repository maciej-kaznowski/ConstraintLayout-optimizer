package com.github.maciejkaznowski.constraintlayoutoptimizer.data.measurer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Optimizer;
import androidx.constraintlayout.widget.ConstraintLayout;

class OptimiserPerformanceMeasurer {

    private static final int MIN_OPTIMISATION = Optimizer.OPTIMIZATION_NONE;
    private static final int MAX_OPTIMISATION = 31; //bitwise or of all available optimisers
    private static final int ITERATION_COUNT = 5;

    static OptimiserMetric[][] measureOptimisers(@NonNull ConstraintLayout constraintLayout) throws ViewMeasureException, ViewLayoutException {
        //save the original optimisations to restore after changing it
        int originalOptimisations = constraintLayout.getOptimizationLevel();

        final OptimiserMetric[][] metrics = new OptimiserMetric[MAX_OPTIMISATION - MIN_OPTIMISATION + 1][ITERATION_COUNT];

        for (int optimisation = MIN_OPTIMISATION; optimisation <= MAX_OPTIMISATION; optimisation++) {
            for (int iteration = 0; iteration < ITERATION_COUNT; iteration++) {
                long fromNs = System.nanoTime();
                constraintLayout.setOptimizationLevel(optimisation);
                // Not to use the view cache in the View class, use the different measureSpecs
                // for each calculation. (Switching the
                // View.MeasureSpec.EXACT and View.MeasureSpec.AT_MOST alternately)
                measureAndLayoutWrapLength(constraintLayout);
                measureAndLayoutExactLength(constraintLayout);
                metrics[optimisation][iteration] = new OptimiserMetric(System.nanoTime() - fromNs, optimisation);
            }
        }

        //restore original optimisation level
        constraintLayout.setOptimizationLevel(originalOptimisations);

        return metrics;
    }

    private static void measureAndLayoutWrapLength(ConstraintLayout constraintLayout) throws ViewLayoutException, ViewMeasureException {
        measureAndLayout(constraintLayout, View.MeasureSpec.AT_MOST);
    }

    private static void measureAndLayoutExactLength(ConstraintLayout constraintLayout) throws ViewLayoutException, ViewMeasureException {
        measureAndLayout(constraintLayout, View.MeasureSpec.EXACTLY);
    }

    private static void measureAndLayout(ConstraintLayout constraintLayout, int mode) throws ViewMeasureException, ViewLayoutException {
        int width = View.MeasureSpec.makeMeasureSpec(1920, mode);
        int height = View.MeasureSpec.makeMeasureSpec(1080, mode);
        measure(constraintLayout, width, height);
        layout(constraintLayout);
    }

    private static void measure(ConstraintLayout constraintLayout, int width, int height) throws ViewMeasureException {
        try {
            constraintLayout.measure(width, height);
        } catch (Exception e) {
            throw new ViewMeasureException(constraintLayout, e);
        }
    }

    private static void layout(ConstraintLayout constraintLayout) throws ViewLayoutException {
        try {
            constraintLayout.layout(0, 0, constraintLayout.getMeasuredWidth(), constraintLayout.getMeasuredHeight());
        } catch (Exception e) {
            throw new ViewLayoutException(constraintLayout, e);
        }
    }
}
