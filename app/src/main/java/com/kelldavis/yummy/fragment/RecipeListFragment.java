package com.kelldavis.yummy.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kelldavis.yummy.R;
import com.kelldavis.yummy.adapter.RecipeAdapter;
import com.kelldavis.yummy.model.Recipe;
import com.kelldavis.yummy.utilities.NetworkUtils;
import com.kelldavis.yummy.utilities.RecipeJsonUtils;

import java.net.URL;

public class RecipeListFragment extends Fragment implements RecipeAdapter.RecipeCardAdapterOnClickHandler {
    private final static String TAG = RecipeListFragment.class.getSimpleName();
    // Define a new interface OnStepClickListener that triggers a callback in the host activity
    RecipeListFragment.OnRecipeClickListener mRecipeCallback;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mRecipeAdapter;
    private TextView mTvErrorMessageDisplay;
    private ProgressBar mPbLoadingIndicator;
    private Recipe[] mRecipes;

    public RecipeListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mRecipeCallback = (RecipeListFragment.OnRecipeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + getString(R.string.must_implement_onrecipeclicklistener));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_recipe_cards);

        // set the number of columns according to the dp width of the device's screen and rotation
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dpWidth = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        int columns = Math.max(1, (int) Math.floor(dpWidth / 300));
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), columns);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecipeAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);
        mTvErrorMessageDisplay = rootView.findViewById(R.id.tv_error_message_display);
        mPbLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);
        mPbLoadingIndicator.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        if (savedInstanceState == null) {
            loadRecipes();
        } else {
            mRecipes = (Recipe[]) (savedInstanceState.getParcelableArray(getString(R.string.recipes_key)));
            mRecipeAdapter.setRecipeCardData(mRecipes);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(getString(R.string.recipes_key), mRecipes);
        super.onSaveInstanceState(outState);
    }

    private void loadRecipes() {
        new FetchRecipesTask().execute();
    }

    private void showRecipeCards() {
        mTvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mTvErrorMessageDisplay.setText(errorMessage);
        mTvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Recipe recipe) {
        mRecipeCallback.onRecipeSelected(recipe);
    }

    // OnStepClickListener interface, calls a method in the host activity named onStepSelected
    public interface OnRecipeClickListener {
        void onRecipeSelected(Recipe recipe);
    }

    public class FetchRecipesTask extends AsyncTask<String, Void, Recipe[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Recipe[] doInBackground(String... params) {
            try {
                URL recipesJsonUrl = NetworkUtils.getRecipesJsonUrl();
                String jsonRecipesResponse = NetworkUtils.getResponseFromHttpUrl(recipesJsonUrl);
                return RecipeJsonUtils.getRecipesFromJson(getActivity(), jsonRecipesResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Recipe[] theRecipes) {
            mPbLoadingIndicator.setVisibility(View.INVISIBLE);

            if (theRecipes != null) {
                mRecipes = theRecipes;
                showRecipeCards();
                mRecipeAdapter.setRecipeCardData(mRecipes);
            } else {
                showErrorMessage(getString(R.string.no_data_received));
            }
        }
    }

}
