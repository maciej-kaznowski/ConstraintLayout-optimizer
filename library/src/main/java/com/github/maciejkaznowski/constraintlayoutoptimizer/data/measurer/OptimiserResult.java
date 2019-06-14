package com.github.maciejkaznowski.constraintlayoutoptimizer.data.measurer;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.maciejkaznowski.constraintlayoutoptimizer.Utils;

import java.util.Arrays;

import static com.github.maciejkaznowski.constraintlayoutoptimizer.Utils.nsToMs;


public class OptimiserResult implements Parcelable {

    private final long minDuration;
    private final long maxDuration;
    private final double medianDuration;
    private final double averageDuration;
    private final int optimizer;

    OptimiserResult(OptimiserMetric[] metrics) {
        Arrays.sort(metrics, (o1, o2) -> Long.compare(o1.getDuration(), o2.getDuration()));
        minDuration = metrics[0].getDuration();
        maxDuration = metrics[metrics.length - 1].getDuration();
        medianDuration = metrics[metrics.length / 2].getDuration();
        averageDuration = calculateAverageDuration(metrics);
        optimizer = metrics[0].getOptimiser();
    }

    private static double calculateAverageDuration(OptimiserMetric[] metrics) {
        double average = 0;
        for (OptimiserMetric metric : metrics) {
            average += metric.getDuration();
        }
        return average / metrics.length;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public double getMedianDuration() {
        return medianDuration;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public int getOptimizer() {
        return optimizer;
    }

    @Override
    public String toString() {
        return "min = " +
                nsToMs(minDuration) +
                "ms" +
                ", " +
                "max = " +
                nsToMs(maxDuration) +
                "ms" +
                ", " +
                "mean = " +
                nsToMs(medianDuration) +
                "ms" +
                ", " +
                "average = " +
                nsToMs(averageDuration) +
                "ms" +
                ", " +
                optimizer +
                ", " +
                Integer.toBinaryString(optimizer) +
                ", " +
                Utils.describeOptimisations(optimizer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.minDuration);
        dest.writeLong(this.maxDuration);
        dest.writeDouble(this.medianDuration);
        dest.writeDouble(this.averageDuration);
        dest.writeInt(this.optimizer);
    }

    protected OptimiserResult(Parcel in) {
        this.minDuration = in.readLong();
        this.maxDuration = in.readLong();
        this.medianDuration = in.readDouble();
        this.averageDuration = in.readDouble();
        this.optimizer = in.readInt();
    }

    public static final Parcelable.Creator<OptimiserResult> CREATOR = new Parcelable.Creator<OptimiserResult>() {
        @Override
        public OptimiserResult createFromParcel(Parcel source) {
            return new OptimiserResult(source);
        }

        @Override
        public OptimiserResult[] newArray(int size) {
            return new OptimiserResult[size];
        }
    };
}
