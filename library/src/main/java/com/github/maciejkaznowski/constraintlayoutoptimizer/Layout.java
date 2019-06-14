package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Objects;

public class Layout implements Parcelable {

    public static final Parcelable.Creator<Layout> CREATOR = new Parcelable.Creator<Layout>() {
        @Override
        public Layout createFromParcel(Parcel source) {
            return new Layout(source);
        }

        @Override
        public Layout[] newArray(int size) {
            return new Layout[size];
        }
    };
    @NonNull private final String resourceName;
    @LayoutRes private final int resource;

    public Layout(@NonNull String resourceName, @LayoutRes int resource) {
        this.resourceName = resourceName;
        this.resource = resource;
    }

    protected Layout(Parcel in) {
        this.resourceName = in.readString();
        this.resource = in.readInt();
    }

    @NonNull
    public String getResourceName() {
        return resourceName;
    }

    @LayoutRes
    public int getResource() {
        return resource;
    }

    @NonNull
    public List<ConstraintLayout> getConstraintLayouts(@NonNull Context context) throws InflateException {
        Log.d("Layout", "inflating " + toString());
        View inflatedLayout = inflate(context);
        return Utils.findAllViews(inflatedLayout, ConstraintLayout.class);
    }

    @NonNull
    private View inflate(@NonNull Context context) throws InflateException {
        try {
            return LayoutInflater.from(context).inflate(resource, null);
        } catch (Exception e) {
            throw new InflateException(e, this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layout layout = (Layout) o;
        return resource == layout.resource &&
                Objects.equals(resourceName, layout.resourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceName, resource);
    }

    @Override
    public String toString() {
        return "Layout{" +
                "resourceName='" + resourceName + '\'' +
                ", resource=" + resource +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.resourceName);
        dest.writeInt(this.resource);
    }

    public static class InflateException extends Throwable {

        InflateException(@NonNull Throwable cause, @NonNull Layout layout) {
            super("Could not inflate layout " + layout);
            initCause(cause);
        }
    }
}
