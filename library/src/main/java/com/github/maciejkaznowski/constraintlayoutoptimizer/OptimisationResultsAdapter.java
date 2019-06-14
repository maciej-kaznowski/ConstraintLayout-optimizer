package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class OptimisationResultsAdapter extends RecyclerView.Adapter<OptimisationResultsAdapter.ViewHolder> {

    private static final int VIEW_TYPE_RESULT = R.layout.list_item_result;
    private final List<Item> items;
    private RecyclerView recyclerView;

    OptimisationResultsAdapter(@NonNull List<Item> items) {
        this.items = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = View.inflate(context, viewType, null);
        view.setOnClickListener(v -> {
            int position = recyclerView.getChildAdapterPosition(v);
            if (position == RecyclerView.NO_POSITION) return;
            Item item = items.get(position);
            DeterminedOptimisers optimiser = item.getDeterminedOptimiser();
            Layout layout = item.getLayout();

            Intent intent = ResultDetailsActivity.getStartIntent(context, layout, optimiser);
            context.startActivity(intent);
        });
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_RESULT;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.layout.setText(item.getLayout().getResourceName());
        holder.boxPlotView.setBoxes(item.getBoxes());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView layout;
        private final HorizontalBoxPlotView boxPlotView;

        public ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            boxPlotView = view.findViewById(R.id.box_plot);
        }
    }

    static class Item {

        @NonNull private final Layout layout;
        @NonNull private final DeterminedOptimisers determinedOptimiser;
        @NonNull private final List<HorizontalBoxPlotView.Box> boxes = new ArrayList<>();

        Item(@NonNull Layout layout, @NonNull DeterminedOptimisers determinedOptimiser) {
            this.layout = layout;
            this.determinedOptimiser = determinedOptimiser;
            for (OptimiserResult result : determinedOptimiser.getResults()) {
                HorizontalBoxPlotView.Box box = new HorizontalBoxPlotView.Box(result.getMinDuration(), (float) result.getMedianDuration(), result.getMaxDuration());
                if (result == determinedOptimiser.getCurrent()) box.setColor(Color.GREEN);
                else box.setColor(Color.RED);
                box.setText(Integer.toBinaryString(result.getOptimizer()));
                boxes.add(box);
            }
        }

        @NonNull
        public Layout getLayout() {
            return layout;
        }

        @NonNull
        public DeterminedOptimisers getDeterminedOptimiser() {
            return determinedOptimiser;
        }

        List<HorizontalBoxPlotView.Box> getBoxes() {
            return boxes;
        }
    }
}
