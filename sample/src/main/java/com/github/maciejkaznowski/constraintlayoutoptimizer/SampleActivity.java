package com.github.maciejkaznowski.constraintlayoutoptimizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.maciejkaznowski.constraintlayoutoptimizer.ui.list.ConstraintOptimiserActivity;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        findViewById(R.id.launchActivityBtn).setOnClickListener(view -> startAnalyseActivity());
    }

    private void startAnalyseActivity() {
        Layouts layouts = new Layouts()
                .includeAllLayouts(true);
        //TODO excluding the 3 layouts below causes no layouts to be diplayed as a result, despite there definitely being R.layout.sample_layout_1
//                .excludeLayout(R.layout.list_item_result)
//                .excludeLayout(R.layout.activity_details)
//                .excludeLayout(R.layout.activity_layout);

        Intent intent = ConstraintOptimiserActivity.getStartIntent(this, layouts);
        startActivity(intent);
    }
}
