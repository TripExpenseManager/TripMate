package com.tripmate;

import android.app.models.NotesModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
    NotesAdapter mAdapter = null;

    String trip_id;

    ArrayList<NotesModel> notesModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.fragment_notes, container, false);

        rvNotes = (RecyclerView) customView.findViewById(R.id.rvNotes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotes.setLayoutManager(linearLayoutManager);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        notesModels = dataBaseHelper.getNotes(trip_id);


        NotesModel model = new NotesModel();
        model.setNote_Date("21-05-2016");
        model.setNote_TripId("1");
        model.setNote_ContentType(1);
        model.setNote_Title("My first Note");
        model.setNote_Id("1");
        model.setNote_ContentStatus("1");
        model.setNote_Body("I wanna fhsfd sxcfsadfjh zxjshf sfjdkhs sjkhfs sfksksmfd x cjkxcasd jnasd");
        notesModels.add(model);

        model = new NotesModel();
        model.setNote_Date("21-05-2016");
        model.setNote_TripId("1");
        model.setNote_ContentType(1);
        model.setNote_Title("My first Note");
        model.setNote_Id("1");
        model.setNote_ContentStatus("1");
        model.setNote_Body("I wanna fhsfd sxcfsadfjh zxjshf sfjdkhs sjkhfs sfksksmfd x cjkxcasd jnasd");
        notesModels.add(model);

        model = new NotesModel();
        model.setNote_Date("21-05-2016");
        model.setNote_TripId("1");
        model.setNote_ContentType(1);
        model.setNote_Title("My first Note");
        model.setNote_Id("1");
        model.setNote_ContentStatus("1");
        model.setNote_Body("I wanna fhsfd sxcfsadfjh zxjshf sfjdkhs sjkhfs sfksksmfd x cjkxcasd jnasd ug bj kh jgjh bbfv hbmn cbv fvhbmnnfvhbmkgfvhkmnvbhgujjk jmn b jh j jkjm n hh hjhn mmn h gh ju kj jngh yh jk h h kjj hg j");
        notesModels.add(model);

        model = new NotesModel();
        model.setNote_Date("21-05-2016");
        model.setNote_TripId("1");
        model.setNote_ContentType(1);
        model.setNote_Title("My first Note");
        model.setNote_Id("1");
        model.setNote_ContentStatus("1");
        model.setNote_Body("I wanna fhsfd sxcfsadfjh zxjshf sfjdkhs sjkhfs sfksksmfd x cjkxcasd jnasd");
        notesModels.add(model);


        mAdapter = new NotesAdapter(notesModels);
        rvNotes.setAdapter(mAdapter);


        //hiding fab on scrolled up and showing it on scrolled down
        final FloatingActionButton fab = (FloatingActionButton)  getActivity().findViewById(R.id.fab);
        rvNotes.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 && fab.isShown())
                {
                    fab.hide();
                }else{

                    fab.show();

                }
            }
        });

        return customView;
    }

    class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{

        private ArrayList<NotesModel> notesModels;

        public NotesAdapter(ArrayList<NotesModel> notesModels) {
            this.notesModels = notesModels;
        }


        public  class NotesViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout rlNotes;
            LinearLayout rlNotesEdit;
            TextView tvNotesTitle,tvNotesMenu,tvNotesBody,tvNotesDate,tvNotesType;
            ImageView ivDeleteNotes,ivEditNotes,ivCancelNotes;
            CardView notesCardView;

            public NotesViewHolder(View itemView) {
                super(itemView);
                rlNotes = (RelativeLayout) itemView.findViewById(R.id.rlNotes);
                rlNotesEdit = (LinearLayout) itemView.findViewById(R.id.llNotesEdit);
                tvNotesTitle = (TextView) itemView.findViewById(R.id.tvNotesTitle);
                tvNotesMenu = (TextView) itemView.findViewById(R.id.tvNotesMenu);
                tvNotesBody = (TextView) itemView.findViewById(R.id.tvNotesBody);
                tvNotesDate = (TextView) itemView.findViewById(R.id.tvNotesDate);
                tvNotesType = (TextView) itemView.findViewById(R.id.tvNotesType);
                ivDeleteNotes = (ImageView) itemView.findViewById(R.id.ivDeleteNotes);
                ivEditNotes = (ImageView) itemView.findViewById(R.id.ivEditNotes);
                ivCancelNotes = (ImageView) itemView.findViewById(R.id.ivCancelNotes);
                notesCardView = (CardView) itemView.findViewById(R.id.notesCardView);


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
            holder.tvNotesTitle.setText(notesModel.getNote_Title());
            holder.tvNotesDate.setText(notesModel.getNote_Date());
            holder.tvNotesBody.setText(notesModel.getNote_Body());

            //notesContentType = 1 -> Note
            //notesContentType = 2 -> Checklist
            if(notesModel.getNote_ContentType() == 1){
                holder.tvNotesType.setText("Note");
            }else{
                holder.tvNotesType.setText("Checklist");
            }

            holder.tvNotesMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getActivity(), holder.tvNotesMenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_notes_item);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    //handle menu1 click
                                    break;
                                case R.id.delete:
                                    //handle menu2 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });

            holder.notesCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    holder.rlNotes.setEnabled(false);
                    holder.rlNotesEdit.setVisibility(View.VISIBLE);

                    return true;
                }
            });

            holder.ivCancelNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.rlNotes.setEnabled(true);
                    holder.rlNotesEdit.setVisibility(View.GONE);

                }
            });

        }

        @Override
        public int getItemCount() {
            return notesModels.size();
        }
    }
}
