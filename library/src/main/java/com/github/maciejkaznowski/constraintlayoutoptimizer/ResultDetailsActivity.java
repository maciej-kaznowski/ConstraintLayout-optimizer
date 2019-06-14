package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultDetailsActivity extends AppCompatActivity {

    private static final String KEY_OPTIMISERS = "KEY_OPTIMISERS";
    private static final String KEY_LAYOUT = "KEY_LAYOUT";

    private Layout layout;
    private DeterminedOptimisers optimisers;

    @NonNull
    public static Intent getStartIntent(@NonNull Context context,
                                        @NonNull Layout layout,
                                        @NonNull DeterminedOptimisers optimisers) {
        return new Intent(context, ResultDetailsActivity.class)
                .putExtra(KEY_LAYOUT, layout)
                .putExtra(KEY_OPTIMISERS, optimisers);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getExtras();
        setTitle(layout.getResourceName());
        bindText();
    }

    private void bindText() {
        ((TextView) findViewById(R.id.current_min_duration)).setText(formatNs(optimisers.getCurrent().getMinDuration()));
        ((TextView) findViewById(R.id.current_mean_duration)).setText(formatNs(optimisers.getCurrent().getMedianDuration()));
        ((TextView) findViewById(R.id.current_max_duration)).setText(formatNs(optimisers.getCurrent().getMaxDuration()));

        String flags = optimisers.getCurrent().getOptimizer() + "=" + Integer.toBinaryString(optimisers.getCurrent().getOptimizer());
        ((TextView) findViewById(R.id.current_flags)).setText(flags);

        String codeFlags = Utils.describeOptimisations(optimisers.getCurrent().getOptimizer());
        ((TextView) findViewById(R.id.current_code_flags)).setText(codeFlags);

        //TODO
//        String xmlFlags =
//        ((TextView) findViewById(R.id.current_xml_flags)).setText(formatNs(optimisers.getCurrent().getMinDuration()));
    }

    @NonNull
    private String formatNs(double ns) {
        return Utils.nsToMs(ns) + "ms";
    }

    private void getExtras() {
        optimisers = getIntent().getExtras().getParcelable(KEY_OPTIMISERS);
        layout = getIntent().getExtras().getParcelable(KEY_LAYOUT);
    }
}
