package com.tripmate;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoriesEdit extends AppCompatActivity {

    RecyclerView rvCategories;
    FloatingActionButton fabAddCategory;

    ArrayList<String> categoriesList = new ArrayList<>();
    DataBaseHelper dataBaseHelper = new DataBaseHelper(CategoriesEdit.this);

    CategoriesAdapter categoriesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int tripmate_theme_id = app_preferences.getInt("tripmate_theme_id",1);
        setTheme(Utils.getThemesHashMap().get(tripmate_theme_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Categories");

        fabAddCategory = (FloatingActionButton) findViewById(R.id.fabAddCategory);
        rvCategories = (RecyclerView) findViewById(R.id.rvCategories);

        categoriesList.clear();
        categoriesList.addAll(Arrays.asList(dataBaseHelper.getCategories()));

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        categoriesAdapter =new CategoriesAdapter();
        rvCategories.setAdapter(categoriesAdapter);

        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(CategoriesEdit.this);
                View promptsView = li.inflate(R.layout.edit_category_individual_open, null);
                AlertDialog.Builder alertDialogBuilderCategories = new AlertDialog.Builder(CategoriesEdit.this);
                alertDialogBuilderCategories.setView(promptsView);

                TextView tvCatEditHeading = (TextView) promptsView.findViewById(R.id.tvCatEditHeading);
                final EditText etCatNameEdit = (EditText) promptsView.findViewById(R.id.etCatNameEdit);
                Button btnCatDelete = (Button) promptsView.findViewById(R.id.btnCatDelete);
                Button btnCatEdit = (Button) promptsView.findViewById(R.id.btnCatEdit);

                btnCatDelete.setText("Cancel");
                btnCatEdit.setText("Add");
                tvCatEditHeading.setText("Add Category");


                alertDialogBuilderCategories.setCancelable(true);
                final AlertDialog alertDialogCategories = alertDialogBuilderCategories.create();
                alertDialogCategories.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                alertDialogCategories.show();


                btnCatDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCategories.dismiss();
                    }
                });

                btnCatEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newCategoryToBeAdded = etCatNameEdit.getText().toString().trim();
                        if(newCategoryToBeAdded.equalsIgnoreCase("")){
                            etCatNameEdit.setError("Please enter category name");
                        }else{
                            newCategoryToBeAdded = newCategoryToBeAdded.substring(0,1).toUpperCase() +
                                    newCategoryToBeAdded.substring(1).toLowerCase();
                            if(categoriesList.contains(newCategoryToBeAdded)){
                                etCatNameEdit.setError(newCategoryToBeAdded + " already exists, please add new category");
                            }else {
                                dataBaseHelper.addCategory(newCategoryToBeAdded);
                                categoriesList.clear();
                                categoriesList.addAll(Arrays.asList(dataBaseHelper.getCategories()));
                                categoriesAdapter.notifyDataSetChanged();
                                alertDialogCategories.dismiss();
                                Snackbar.make(fabAddCategory,newCategoryToBeAdded + " added successfully",Snackbar.LENGTH_LONG).show();
                            }

                        }
                    }
                });

            }
        });

    }

    class CategoriesAdapter extends  RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>{

        class CategoryViewHolder extends RecyclerView.ViewHolder{
            ImageView ivCategoryEdit;
            TextView tvCategoryEdit;
            LinearLayout llEditCat;
            CategoryViewHolder(View itemView) {
                super(itemView);
                tvCategoryEdit = (TextView) itemView.findViewById(R.id.tvCategoryEdit);
                ivCategoryEdit = (ImageView) itemView.findViewById(R.id.ivCategoryEdit);
                llEditCat = (LinearLayout) itemView.findViewById(R.id.llEditCat);
            }
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_edit_categories,parent,false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            final String category = categoriesList.get(position);
            if(Utils.getCategoriesHashMap().get(category) != null){
                holder.ivCategoryEdit.setImageDrawable(getDrawable(Utils.getCategoriesHashMap().get(category)));
            }else{
                String firstLetter = String.valueOf(category.charAt(0));

                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate random color
                int color = generator.getColor(category);
                //int color = generator.getRandomColor();

                TextDrawable drawable = TextDrawable.builder().beginConfig()
                        .bold().endConfig()
                        .buildRound(firstLetter, color); // radius in px

                holder.ivCategoryEdit.setImageDrawable(drawable);
            }


            holder.llEditCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isEditable;
                    if(category.equalsIgnoreCase("Miscellaneous")){
                        Snackbar.make(fabAddCategory,"You cannot edit/delete Miscellaneous category",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        if(category.equalsIgnoreCase("Drink") || category.equalsIgnoreCase("Entertainment") || category.equalsIgnoreCase("Food")
                                || category.equalsIgnoreCase("Hotel") || category.equalsIgnoreCase("Medical") || category.equalsIgnoreCase("Parking")
                                || category.equalsIgnoreCase("Shopping") || category.equalsIgnoreCase("Toll") || category.equalsIgnoreCase("Travel")){
                            isEditable = false;
                        }else{
                            isEditable = true;
                        }

                        LayoutInflater li = LayoutInflater.from(CategoriesEdit.this);
                        View promptsView = li.inflate(R.layout.edit_category_individual_open, null);
                        AlertDialog.Builder alertDialogBuilderCategories = new AlertDialog.Builder(CategoriesEdit.this);
                        alertDialogBuilderCategories.setView(promptsView);

                        final EditText etCatNameEdit = (EditText) promptsView.findViewById(R.id.etCatNameEdit);
                        Button btnCatDelete = (Button) promptsView.findViewById(R.id.btnCatDelete);
                        Button btnCatEdit = (Button) promptsView.findViewById(R.id.btnCatEdit);

                        etCatNameEdit.setText(category);
                        etCatNameEdit.setSelection(category.length());

                        alertDialogBuilderCategories.setCancelable(true);
                        final AlertDialog alertDialogCategories = alertDialogBuilderCategories.create();
                        alertDialogCategories.getWindow().setWindowAnimations(R.style.DialogAnimationCentreInsta);
                        alertDialogCategories.show();

                        if(isEditable){
                            btnCatEdit.setEnabled(true);
                            etCatNameEdit.setEnabled(true);
                        }
                        else{
                            btnCatEdit.setEnabled(false);
                            etCatNameEdit.setEnabled(false);
                            etCatNameEdit.setFocusableInTouchMode(false);
                            btnCatEdit.setBackgroundColor(Color.GRAY);
                        }


                        btnCatDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder alertDialogBuilderCategories = new AlertDialog.Builder(CategoriesEdit.this);
                                alertDialogBuilderCategories.setCancelable(true);

                                alertDialogBuilderCategories.setTitle("Are you sure? You want to delete "+category+" category?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataBaseHelper.deleteCategory(category);
                                        Snackbar.make(fabAddCategory,category + " deleted successfully",Snackbar.LENGTH_LONG).show();
                                        alertDialogCategories.dismiss();
                                        categoriesList.clear();
                                        categoriesList.addAll(Arrays.asList(dataBaseHelper.getCategories()));
                                        categoriesAdapter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                                final AlertDialog alertDialogCategories = alertDialogBuilderCategories.create();
                                alertDialogCategories.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                                alertDialogCategories.show();

                            }
                        });

                        btnCatEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                String newCategoryToBeAdded = etCatNameEdit.getText().toString().trim();
                                if(newCategoryToBeAdded.equalsIgnoreCase("")){
                                    etCatNameEdit.setError("Please enter category name");
                                }else{
                                    newCategoryToBeAdded = newCategoryToBeAdded.substring(0,1).toUpperCase() +
                                            newCategoryToBeAdded.substring(1).toLowerCase();
                                    if(categoriesList.contains(newCategoryToBeAdded)){
                                        alertDialogCategories.dismiss();
                                    }else {
                                        dataBaseHelper.updateCategory(newCategoryToBeAdded,category);
                                        categoriesList.clear();
                                        categoriesList.addAll(Arrays.asList(dataBaseHelper.getCategories()));
                                        categoriesAdapter.notifyDataSetChanged();
                                        alertDialogCategories.dismiss();
                                        Snackbar.make(fabAddCategory,newCategoryToBeAdded + " edited successfully",Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });


                    }

                }
            });

            holder.tvCategoryEdit.setText(category);

        }

        @Override
        public int getItemCount() {
            return categoriesList.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
