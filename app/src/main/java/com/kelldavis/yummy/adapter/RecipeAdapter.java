package com.kelldavis.yummy.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kelldavis.yummy.R;
import com.kelldavis.yummy.databinding.RecipeListItemBinding;
import com.kelldavis.yummy.model.Recipe;
import com.kelldavis.yummy.utilities.OnItemClickListener;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    private ArrayList<Recipe> mRecipeList;
    private OnItemClickListener mRecipeOnItemClickListener;

    public RecipeAdapter(ArrayList<Recipe> incomingRecipeSet,
                         OnItemClickListener<Recipe> recipeOnItemClickListener) {
        this.mRecipeList = incomingRecipeSet;
        this.mRecipeOnItemClickListener = recipeOnItemClickListener;
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecipeListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.recipe_list_item, parent, false);
        return new RecipeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.mRecipe = mRecipeList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(holder.mRecipe.getImage())
                .apply(new RequestOptions()
                    .placeholder(R.drawable.cake)
                    .error(R.drawable.cake)
                    .dontAnimate()
                    .fitCenter())
                .into(holder.mBinding.ivRecipeImage);
        holder.mBinding.tvRecipeName.setText(holder.mRecipe.getName());
        holder.mBinding.tvRecipeName.setSelected(true);
        holder.mBinding.tvRecipeName.setHorizontallyScrolling(true);
        holder.mBinding.tvIngredientCount.setText(String.valueOf(holder.mRecipe.getIngredients().size()));
        holder.mBinding.tvServingCount.setText(String.valueOf(holder.mRecipe.getServings()));
        holder.mBinding.tvStepCount.setText(String.valueOf(holder.mRecipe.getSteps().size()));
    }

    @Override
    public int getItemCount() {
        return (mRecipeList == null) ? 0 : mRecipeList.size();
    }

    public class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecipeListItemBinding mBinding;
        private Recipe mRecipe;

        public RecipeHolder(RecipeListItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.cvRecipeList.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mRecipeOnItemClickListener.onClick(mRecipe, v);
        }
    }
}
