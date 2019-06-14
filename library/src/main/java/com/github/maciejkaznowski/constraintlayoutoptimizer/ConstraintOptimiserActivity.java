package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ConstraintOptimiserActivity extends Activity {

    private static final String EXTRA_LAYOUTS = "ConstraintOptimiserActivity.EXTRA_LAYOUTS";

    private RecyclerView recyclerView;
    private OptimisationResultsAdapter adapter;
    private Layouts layouts = null;

    public static void start(@NonNull Context context) {
        context.startActivity(getStartIntent(context));
    }

    public static void start(@NonNull Context context, @NonNull Layouts layouts) {
        context.startActivity(getStartIntent(context, layouts));
    }

    @NonNull
    public static Intent getStartIntent(@NonNull Context context, @NonNull Layouts layouts) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_LAYOUTS, layouts);

        Intent intent = getStartIntent(context);
        intent.putExtras(extras);

        return intent;

    }

    @NonNull
    private static Intent getStartIntent(@NonNull Context context) {
        Intent intent = new Intent(context, ConstraintOptimiserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        getExtras();
        measureLayouts();
    }

    private void measureLayouts() {
        List<OptimisationResultsAdapter.Item> items = new ArrayList<>();

        try {
            List<Layout> layouts = this.layouts.find(this);
            for (Layout layout : layouts) {
                List<DeterminedOptimisers> determinedOptimisers = new ArrayList<>();

                try {
                    determinedOptimisers = DeterminedOptimisers.fromLayout(this, layout);
                } catch (ViewMeasureException | ViewLayoutException e) {
                    e.printStackTrace();
                }

                Log.d("ConstraintOptimiser", "finished determining optimisers for " + layout);
                for (DeterminedOptimisers determinedOptimiser : determinedOptimisers) {
                    items.add(new OptimisationResultsAdapter.Item(layout, determinedOptimiser));
                }
            }
            Log.d("ConstraintOptimiser", "Finished");
            adapter = new OptimisationResultsAdapter(items);
            showResult();
            adapter.notifyDataSetChanged();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private void showResult() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) layouts = extras.getParcelable(EXTRA_LAYOUTS);
        if (layouts == null) layouts = new Layouts().includeAllLayouts(true);
    }
}
