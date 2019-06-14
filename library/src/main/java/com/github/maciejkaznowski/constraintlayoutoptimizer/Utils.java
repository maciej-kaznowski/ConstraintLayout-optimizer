package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Optimizer;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    @NonNull
    static String getLayoutHexString(@LayoutRes int layout) {
        return String.format("0x%8x", layout);
    }

    static boolean hasFlags(int target, int flags) {
        return (target & flags) == flags;
    }


    @NonNull
    static <T> List<T> findAllViews(@NonNull View view, @NonNull Class<T> clazz) {
        List<T> result = new ArrayList<>();

        /*
        check if the given view is what we are looking for,
        but don't immediately return that since it might also have a view we are interested in
        */
        if (clazz.isAssignableFrom(view.getClass())) {
            result.add((T) view);
        }

        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();

            for (int i = 0; i < childCount; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                result.addAll(findAllViews(child, clazz));
            }
        }

        return result;
    }

    public static String describeOptimisations(int optimizer) {
        StringBuilder optimiserString = new StringBuilder();

        int highestFlag = Integer.highestOneBit(optimizer);
        appendingFlagsAsStrings:
        if (highestFlag != 0) {
            if (Utils.hasFlags(optimizer, Optimizer.OPTIMIZATION_DIRECT)) {
                optimiserString.append("OPTIMIZATION_DIRECT");
                if (highestFlag == Optimizer.OPTIMIZATION_DIRECT) break appendingFlagsAsStrings;
                optimiserString.append(" | ");
            }

            if (Utils.hasFlags(optimizer, Optimizer.OPTIMIZATION_BARRIER)) {
                optimiserString.append("OPTIMIZATION_BARRIER");
                if (highestFlag == Optimizer.OPTIMIZATION_BARRIER) break appendingFlagsAsStrings;
                optimiserString.append(" | ");
            }

            if (Utils.hasFlags(optimizer, Optimizer.OPTIMIZATION_CHAIN)) {
                optimiserString.append("OPTIMIZATION_CHAIN");
                if (highestFlag == Optimizer.OPTIMIZATION_CHAIN) break appendingFlagsAsStrings;
                optimiserString.append(" | ");
            }

            if (Utils.hasFlags(optimizer, Optimizer.OPTIMIZATION_DIMENSIONS)) {
                optimiserString.append("OPTIMIZATION_DIMENSIONS");
                if (highestFlag == Optimizer.OPTIMIZATION_DIMENSIONS) break appendingFlagsAsStrings;
                optimiserString.append(" | ");
            }

            if (Utils.hasFlags(optimizer, Optimizer.OPTIMIZATION_RATIO)) {
                optimiserString.append("OPTIMIZATION_RATIO");
            }
        } else {
            optimiserString.append("OPTIMIZATION_NONE");
        }

        return optimiserString.toString();
    }

    @NonNull
    public static String nsToMs(double ns) {
        return String.format("%.2f", ns / 1_000_000F);
    }
}
