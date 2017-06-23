package com.tripmate;


import android.app.models.NotesModel;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notes extends Fragment {

    RecyclerView rvNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View customView = inflater.inflate(R.layout.fragment_notes, container, false);

        rvNotes = (RecyclerView) customView.findViewById(R.id.rvNotes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotes.setLayoutManager(linearLayoutManager);





        return customView;
    }

    class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{

        private ArrayList<NotesModel> notesModels = new ArrayList<>();


        public  class NotesViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout rlNotes;
            LinearLayout rlNotesEdit;
            TextView tvNotesTitle,tvNotesMenu,tvNotesBody,tvNotesDate;
            ImageView ivDeleteNotes,ivEditNotes,ivCancelNotes;

            public NotesViewHolder(View itemView) {
                super(itemView);
                rlNotes = (RelativeLayout) itemView.findViewById(R.id.rlNotes);
                rlNotesEdit = (LinearLayout) itemView.findViewById(R.id.llNotesEdit);
                tvNotesTitle = (TextView) itemView.findViewById(R.id.tvNotesTitle);
                tvNotesMenu = (TextView) itemView.findViewById(R.id.tvNotesMenu);
                tvNotesBody = (TextView) itemView.findViewById(R.id.tvNotesBody);
                tvNotesDate = (TextView) itemView.findViewById(R.id.tvNotesDate);
                ivDeleteNotes = (ImageView) itemView.findViewById(R.id.ivDeleteNotes);
                ivEditNotes = (ImageView) itemView.findViewById(R.id.ivEditNotes);
                ivCancelNotes = (ImageView) itemView.findViewById(R.id.ivCancelNotes);


            }
        }

        @Override
        public NotesAdapter.NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_notes, parent, false);
            return new NotesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final NotesAdapter.NotesViewHolder holder, int position) {
            holder.rlNotesEdit.setVisibility(View.INVISIBLE);
            NotesModel notesModel = notesModels.get(position);
            holder.tvNotesTitle.setText(notesModel.getTitle());
            holder.tvNotesDate.setText(notesModel.getDate());
            holder.tvNotesBody.setText(notesModel.getBody());
            holder.tvNotesMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), holder.tvNotesMenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.persons_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.make_admin:
                                    //handle menu1 click
                                    break;
                                case R.id.call:
                                    //handle menu2 click
                                    break;
                                case R.id.edit:
                                    //handle menu3 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });


        }

        @Override
        public int getItemCount() {
            return notesModels.size();
        }
    }
}
