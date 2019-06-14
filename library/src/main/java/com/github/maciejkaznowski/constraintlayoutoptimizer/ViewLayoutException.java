package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class ViewLayoutException extends Throwable {

    @NonNull private final View view;
    @NonNull private String message;
    @Nullable private Layout layout;

    public ViewLayoutException(@NonNull Layout layout, @NonNull ViewLayoutException source) {
        this(source.getView(), source.getCause());
        this.layout = layout;
        this.message = "Could not layout " + source.getView() + " in layout resource " + layout.getResourceName() + "\n" + source.getCause();
    }

    public ViewLayoutException(@NonNull View view, @NonNull Throwable cause) {
        super(cause);
        this.view = view;
        this.layout = null;
        this.message = "Could not layout " + view + "\n" + cause;
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
