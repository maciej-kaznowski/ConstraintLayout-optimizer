package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeterminedOptimisers implements Parcelable {

    private static final String TAG = "OptimiserDeterminer";

    @NonNull private final OptimiserResult current;
    @NonNull private final OptimiserResult bestByMin;
    @NonNull private final OptimiserResult bestByMax;
    @NonNull private final OptimiserResult bestByMean;
    @NonNull private final OptimiserResult bestByAverage;
    @NonNull private final OptimiserResult[] results;

    /**
     * @param context The context used to inflate the layout resource
     * @param layout  The layout which will be inflated
     * @return A list of DeterminedOptimisers, where each element of the list corresponds to a single ConstraintLayout within the inflated layout
     */
    public static List<DeterminedOptimisers> fromLayout(@NonNull Context context, @NonNull Layout layout) throws ViewMeasureException, ViewLayoutException {
        List<ConstraintLayout> layouts;
        try {
            layouts = layout.getConstraintLayouts(context);
        } catch (Layout.InflateException e) {
            Log.w(TAG, "Could not inflate layout " + layout.getResourceName() + ", skipping");
            return Collections.emptyList();
        }
        List<DeterminedOptimisers> optimisers = new ArrayList<>(layouts.size());
        for (int i = 0; i < layouts.size(); i++) {
            ConstraintLayout constraintLayout = layouts.get(i);
            try {
                DeterminedOptimisers determinedOptimisers = DeterminedOptimisers.fromConstraintLayout(constraintLayout);
                optimisers.add(determinedOptimisers);
            } catch (ViewLayoutException exception) {
                throw new ViewLayoutException(layout, exception);
            } catch (ViewMeasureException exception) {
                throw new ViewMeasureException(layout, exception);
            }
        }
        return optimisers;
    }

    public static DeterminedOptimisers fromConstraintLayout(@NonNull ConstraintLayout constraintLayout) throws ViewLayoutException, ViewMeasureException {
        OptimiserMetric[][] metrics = OptimiserPerformanceMeasurer.measureOptimisers(constraintLayout);
        return new DeterminedOptimisers(metrics, constraintLayout);
    }

    private DeterminedOptimisers(@NonNull OptimiserMetric[][] metrics, @NonNull ConstraintLayout constraintLayout) {
        this.results = new OptimiserResult[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            OptimiserMetric[] optimiserMetrics = metrics[i];
            results[i] = new OptimiserResult(optimiserMetrics);
        }

        sortByMin(results);
        this.bestByMin = results[0];

        sortByMax(results);
        this.bestByMax = results[0];

        sortByMedian(results);
        this.bestByMean = results[0];

        sortByAverage(results);
        this.bestByAverage = results[0];

        for (OptimiserResult result : results) {
            if (result.getOptimizer() == constraintLayout.getOptimizationLevel()) {
                this.current = result;
                return;
            }
        }

        throw new IllegalStateException("Could not determine the original metric for optimization level " + constraintLayout.getOptimizationLevel());
    }

    private void sortByAverage(OptimiserResult[] results) {
        Arrays.sort(results, (o1, o2) -> Double.compare(o1.getAverageDuration(), o2.getAverageDuration()));
    }

    private void sortByMedian(OptimiserResult[] results) {
        Arrays.sort(results, (o1, o2) -> Double.compare(o1.getMedianDuration(), o2.getMedianDuration()));
    }

    private void sortByMax(OptimiserResult[] results) {
        Arrays.sort(results, (o1, o2) -> Long.compare(o1.getMaxDuration(), o2.getMaxDuration()));
    }

    private void sortByMin(OptimiserResult[] results) {
        Arrays.sort(results, (o1, o2) -> Long.compare(o1.getMinDuration(), o2.getMinDuration()));
    }

    @NonNull
    public OptimiserResult getBestByMin() {
        return bestByMin;
    }

    @NonNull
    public OptimiserResult getBestByMax() {
        return bestByMax;
    }

    @NonNull
    public OptimiserResult getBestByMean() {
        return bestByMean;
    }

    @NonNull
    public OptimiserResult getBestByAverage() {
        return bestByAverage;
    }

    @NonNull
    public OptimiserResult getCurrent() {
        return current;
    }

    @NonNull
    public OptimiserResult[] getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "DeterminedOptimisers{" +
                "current=" + current +
                ", bestByMin=" + bestByMin +
                ", bestByMax=" + bestByMax +
                ", bestByMean=" + bestByMean +
                ", bestByAverage=" + bestByAverage +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.current, flags);
        dest.writeParcelable(this.bestByMin, flags);
        dest.writeParcelable(this.bestByMax, flags);
        dest.writeParcelable(this.bestByMean, flags);
        dest.writeParcelable(this.bestByAverage, flags);
        dest.writeTypedArray(this.results, flags);
    }

    protected DeterminedOptimisers(Parcel in) {
        this.current = in.readParcelable(OptimiserResult.class.getClassLoader());
        this.bestByMin = in.readParcelable(OptimiserResult.class.getClassLoader());
        this.bestByMax = in.readParcelable(OptimiserResult.class.getClassLoader());
        this.bestByMean = in.readParcelable(OptimiserResult.class.getClassLoader());
        this.bestByAverage = in.readParcelable(OptimiserResult.class.getClassLoader());
        this.results = in.createTypedArray(OptimiserResult.CREATOR);
    }

    public static final Parcelable.Creator<DeterminedOptimisers> CREATOR = new Parcelable.Creator<DeterminedOptimisers>() {
        @Override
        public DeterminedOptimisers createFromParcel(Parcel source) {
            return new DeterminedOptimisers(source);
        }

        @Override
        public DeterminedOptimisers[] newArray(int size) {
            return new DeterminedOptimisers[size];
        }
    };
}
