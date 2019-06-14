package com.github.maciejkaznowski.constraintlayoutoptimizer.data.measurer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.maciejkaznowski.constraintlayoutoptimizer.Layout;

public class ViewMeasureException extends Throwable {

    @NonNull private final View view;
    @NonNull private String message;
    @Nullable private Layout layout;

    public ViewMeasureException(@NonNull Layout layout, @NonNull ViewMeasureException source) {
        this(source.getView(), source.getCause());
        this.layout = layout;
        this.message = "Could not measure " + source.getView() + " in layout resource " + layout.getResourceName() + "\n" + source.getCause();
    }

    public ViewMeasureException(@NonNull View view, @NonNull Throwable cause) {
        super(cause);
        this.view = view;
        this.layout = null;
        this.message = "Could not measure " + view + "\n" + cause;
    }

    @NonNull
    public View getView() {
        return view;
    }

    @Nullable
    public Layout getLayout() {
        return layout;
    }

    @NonNull
    @Override
    public String getMessage() {
        return message;
    }
}
