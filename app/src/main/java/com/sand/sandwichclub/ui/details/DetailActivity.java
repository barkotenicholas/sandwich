package com.sand.sandwichclub.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.flexbox.FlexboxLayout;
import com.sand.sandwichclub.R;
import com.sand.sandwichclub.databinding.ActivityDetailBinding;
import com.sand.sandwichclub.model.Sandwich;
import com.sand.sandwichclub.utils.GlideApp;
import com.sand.sandwichclub.utils.UiUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private BillingProcessor billingProcessor;

    private ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        FloatingActionButton floatingActionButton = mBinding.sub;

        setUpsub();

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        final int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        setupToolbar();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingProcessor.subscribe(DetailActivity.this,"acup");

            }
        });

        SandwichViewModel mViewModel = obtainViewModel(this, position);

        // Subscribe to sandwich changes
        mViewModel.getSandwich().observe(this, new Observer<Sandwich>() {
            @Override
            public void onChanged(@Nullable Sandwich sandwich) {
                if (sandwich != null) {
                    populateUI(sandwich);
                } else {
                    closeOnError();
                }
            }
        });
    }

    private void setUpsub() {
        billingProcessor = new BillingProcessor(this,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAimYOGnBuxZXnU5GCiXsaWdSFW3ToKhiEOB25l1GvbGAVKdOksfAfkWFbi3aFz39Xpl61Ef7K/0kmUcb2yYBA4olyW8rFhlpRtIi1s4oIm1ZIaWUZ730jnejctr8XWVEFFCtnLbh9gS1wuzB4txu5xM1mjs3rQAZ1jO7NL96s1wwoFm30a9iNPxsUcEHTF/Dho+ufvXKnAGu8/SqVm3erQFzL0sTST/AY4Yw4o2ViDxqqe2l69GlJgYu9T7ccf/ZahQM25bS4v71iD5LrRMwQjDc4528UbWn6iqJCsKeS8cCICc3Oj5CLTJ/Pb12DbvfKkbdf0/LwQpn8HDguH9zhCQIDAQAB" , null, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {

            }

            @Override
            public void onPurchaseHistoryRestored() {

            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {

            }

            @Override
            public void onBillingInitialized() {

            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private SandwichViewModel obtainViewModel(FragmentActivity activity, int position) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication(), position);
        return ViewModelProviders.of(activity, factory).get(SandwichViewModel.class);
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void populateUI(Sandwich sandwich) {
        // Sandwich image
        GlideApp.with(this)
                .load(sandwich.getImage())
                .placeholder(R.color.md_grey_200)
                .into(mBinding.imageRecipe);

        // Sandwich main name
        mBinding.textSandwichName.setText(sandwich.getMainName());

        // Sandwich origin
        String placeOfOrigin = sandwich.getPlaceOfOrigin();
        if (placeOfOrigin.isEmpty()) {
            mBinding.textOrigin.setVisibility(View.GONE);
        } else {
            UiUtils.setTextViewDrawableColor(this, mBinding.textOrigin, R.color.text_black_54);
            mBinding.textOrigin.setText(placeOfOrigin);
        }

        // Programmatically create & add "also known as" labels
        List<String> names = sandwich.getAlsoKnownAs();
        if (!names.isEmpty()) {
            for (String name : names) {
                TextView textView = new TextView(this);
                textView.setText(name);
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_shape));
                TextViewCompat.setTextAppearance(textView, R.style.Chips);
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 8, 8);
                textView.setLayoutParams(layoutParams);
                mBinding.flexbox.addView(textView);
            }
        } else {
            mBinding.flexbox.setVisibility(View.GONE);
        }

        // Sandwich description
        UiUtils.setTextViewDrawableColor(this, mBinding.textView3, R.color.text_black_54);
        mBinding.descriptionTv.setText(sandwich.getDescription());

        // Ingredients List
        UiUtils.setTextViewDrawableColor(this, mBinding.textView4, R.color.text_black_54);
        List<String> ingredients = sandwich.getIngredients();
        for (String ingredient : ingredients) {
            TextView textView = new TextView(this);
            textView.setText(ingredient);
            TextViewCompat.setTextAppearance(textView, R.style.Chips);
            textView.setPadding(0, 32, 0, 32);
            textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(
                    this, R.drawable.bullet), null, null, null);
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.dashed_line));
            textView.setCompoundDrawablePadding(32);
            mBinding.ingredientsList.addView(textView);
        }

        mBinding.executePendingBindings();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
