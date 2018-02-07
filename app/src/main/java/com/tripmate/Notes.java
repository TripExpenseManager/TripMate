package com.tripmate;

import android.app.models.NotesModel;
import android.app.models.TodoModel;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import java.util.Date;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notes extends Fragment {

    RecyclerView rvNotes;
    NotesAdapter mAdapter = null;

    RelativeLayout no_notes_RL;

    String trip_id;

    ArrayList<NotesModel> notesModels = new ArrayList<>();

    ArrayList<TodoModel> completedTodosArrayList = new ArrayList<>();
    ArrayList<TodoModel> unCompletedTodosArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.fragment_notes, container, false);

        rvNotes = (RecyclerView) customView.findViewById(R.id.rvNotes);
        no_notes_RL = (RelativeLayout) customView.findViewById(R.id.no_notes_RL);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotes.setLayoutManager(linearLayoutManager);

        Intent intent = getActivity().getIntent();
        trip_id = intent.getStringExtra("trip_id");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        notesModels = dataBaseHelper.getNotes(trip_id);

        if(notesModels.size() == 0){
            no_notes_RL.setVisibility(View.VISIBLE);
        }else{
            no_notes_RL.setVisibility(View.GONE);
        }

        mAdapter = new NotesAdapter(notesModels);
        mAdapter.notifyDataSetChanged();
        rvNotes.setAdapter(mAdapter);


        final FloatingActionMenu fabMenu = (FloatingActionMenu)  getActivity().findViewById(R.id.fabMenu);
        com.github.clans.fab.FloatingActionButton fabNotes = (com.github.clans.fab.FloatingActionButton) getActivity().findViewById(R.id.fabNotes);
        final com.github.clans.fab.FloatingActionButton fabCheckList = (com.github.clans.fab.FloatingActionButton) getActivity().findViewById(R.id.fabCheckList);


        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);
                Intent intent = new Intent(getContext(),NotesEditActivity.class);
                intent.putExtra("tripId",trip_id);
                intent.putExtra("editOrAdd","add");
                intent.putExtra("anim","yes");
                startActivity(intent);
            }
        });

        fabCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);
                Intent intent = new Intent(getContext(),CheckListActivityNew.class);
                intent.putExtra("tripId",trip_id);
                intent.putExtra("editOrAdd","add");
                intent.putExtra("anim","yes");
                startActivity(intent);
            }
        });


        rvNotes.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 && fabMenu.isShown())
                {
                    fabMenu.hideMenuButton(true);
                }else if(((TabLayout)getActivity().findViewById(R.id.tabs)).getSelectedTabPosition() == 3){
                    fabMenu.showMenuButton(true);
                }
            }
        });

        return customView;
    }


    @Override
    public void onResume() {

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());

        notesModels.clear();
        notesModels.addAll(dataBaseHelper.getNotes(trip_id));

        if(notesModels.size() == 0){
            no_notes_RL.setVisibility(View.VISIBLE);
        }else{
            no_notes_RL.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();

        //hiding fab
        final FloatingActionButton fab = (FloatingActionButton)  getActivity().findViewById(R.id.fab);
        fab.hide();
        fab.setVisibility(View.GONE);

        super.onResume();

    }

    public ArrayList<TodoModel>  decryptTodos(String noteContent){
        String[] todosModelsasStrings = noteContent.split(Pattern.quote(Utils.DELIMETER_FOR_TODOS));
        ArrayList<TodoModel> todoModels = new ArrayList<>();

        for(String s : todosModelsasStrings) {
            TodoModel todoModel = new TodoModel();
            String[] todo = s.split(Pattern.quote(Utils.DELIMETER_FOR_A_TODO));
            todoModel.setName(todo[0].trim());
            if(todo[1].trim().equalsIgnoreCase("t"))
                todoModel.setCompleted(true);
            else
                todoModel.setCompleted(false);
            todoModels.add(todoModel);
        }
        return  todoModels;
    }

    public void seperateTodosBasedOnStatus(String noteContent){
        ArrayList<TodoModel> todoModelArrayList = decryptTodos(noteContent);

        completedTodosArrayList = new ArrayList<>();
        unCompletedTodosArrayList = new ArrayList<>();

        for(TodoModel todoModel : todoModelArrayList){
            if(todoModel.isCompleted())
                completedTodosArrayList.add(todoModel);
            else{
                unCompletedTodosArrayList.add(todoModel);
            }
        }
    }


    class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{

        private ArrayList<NotesModel> notesModels;

        public NotesAdapter(ArrayList<NotesModel> notesModels) {
            this.notesModels = notesModels;
        }

        boolean isHolderLongPressed = false;
        int longPressedPosition = 1;


        public  class NotesViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout rlNotes,dateNoteContainer;
            LinearLayout llNotesEdit;
            TextView tvNotesTitle,tvNotesMenu,tvNotesBody,tvNotesDate,tvNotesType;
            ImageView ivDeleteNotes,ivEditNotes,ivCancelNotes;
            CardView notesCardView;

            public NotesViewHolder(View itemView) {
                super(itemView);
                rlNotes = (RelativeLayout) itemView.findViewById(R.id.rlNotes);
                llNotesEdit = (LinearLayout) itemView.findViewById(R.id.llNotesEdit);
                tvNotesTitle = (TextView) itemView.findViewById(R.id.tvNotesTitle);
                tvNotesMenu = (TextView) itemView.findViewById(R.id.tvNotesMenu);
                tvNotesBody = (TextView) itemView.findViewById(R.id.tvNotesBody);
                tvNotesDate = (TextView) itemView.findViewById(R.id.tvNotesDate);
                tvNotesType = (TextView) itemView.findViewById(R.id.tvNotesType);
                ivDeleteNotes = (ImageView) itemView.findViewById(R.id.ivDeleteNotes);
                ivEditNotes = (ImageView) itemView.findViewById(R.id.ivEditNotes);
                ivCancelNotes = (ImageView) itemView.findViewById(R.id.ivCancelNotes);
                notesCardView = (CardView) itemView.findViewById(R.id.notesCardView);
                dateNoteContainer = (RelativeLayout) itemView.findViewById(R.id.dateNoteContainer);


            }
        }

        @Override
        public NotesAdapter.NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_notes, parent, false);
            return new NotesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final NotesAdapter.NotesViewHolder holder, final int position) {
            holder.llNotesEdit.setVisibility(View.GONE);
            final NotesModel notesModel = notesModels.get(position);
            holder.tvNotesTitle.setText(notesModel.getNote_Title());
            holder.tvNotesBody.setText(notesModel.getNote_Body());

         /*   Long datevalue = Long.valueOf(notesModel.getNote_Date());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = new Date(datevalue);
            String dateStr = format.format(date);

            holder.tvNotesDate.setText(dateStr);
            */

            //notesContentType = 1 -> Note
            //notesContentType = 2 -> Checklist
            if(notesModel.getNote_ContentType() == 1){
                holder.tvNotesType.setText("Note");
            }else{
                if(!notesModel.getNote_Body().trim().equals(""))
                    holder.tvNotesBody.setText(decryptUnCompletedTodosasLinesOfText(notesModel.getNote_Body()));
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
                                    holder.ivEditNotes.performClick();
                                    break;
                                case R.id.delete:
                                    holder.ivDeleteNotes.performClick();
                                    break;
                                case R.id.share:

                                    String notesContent = "";
                                    if(notesModel.getNote_ContentType() == 1){
                                        notesContent += notesModel.getNote_Title() + "\n\n";
                                        notesContent += notesModel.getNote_Body();
                                    }else {

                                        notesContent += notesModel.getNote_Title() + "\n\n";
                                        String checklistContent = notesModel.getNote_Body();
                                        if (!checklistContent.trim().equals(""))
                                            seperateTodosBasedOnStatus(checklistContent);

                                        if (unCompletedTodosArrayList.size() != 0) {
                                            int i = 1;
                                            for (TodoModel todoModel : unCompletedTodosArrayList) {
                                                // Name of todos
                                                if (todoModel.getName() != null)
                                                    notesContent += i + ". " + todoModel.getName().trim() + "\n";
                                                i++;
                                            }
                                        }

                                        if (completedTodosArrayList.size() != 0) {
                                            int i = 1;
                                            notesContent += "\n Completed Items : \n";
                                            for (TodoModel todoModel : completedTodosArrayList) {
                                                // Name of todos
                                                if (todoModel.getName() != null)
                                                    notesContent +=  i + ". " + todoModel.getName().trim() + "\n";

                                                i++;
                                            }
                                        }
                                    }


                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, notesContent);
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);


                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if(notesModel.getNote_ContentType()==1) {
                        intent = new Intent(getContext(), NotesEditActivity.class);
                    }
                    else{
                        intent = new Intent(getContext(), CheckListActivityNew.class);
                    }
                    intent.putExtra("tripId",trip_id);
                    intent.putExtra("editOrAdd","view");
                    intent.putExtra("notesId",notesModels.get(position).getNote_Id());
                    intent.putExtra("anim","yes");
                    startActivity(intent);
                }
            };


            holder.tvNotesTitle.setOnClickListener(listener);
            holder.tvNotesBody.setOnClickListener(listener);
            holder.dateNoteContainer.setOnClickListener(listener);


            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(isHolderLongPressed)
                        mAdapter.notifyItemChanged(longPressedPosition);
                    holder.rlNotes.setEnabled(false);
                    holder.llNotesEdit.setVisibility(View.VISIBLE);
                    longPressedPosition = position;
                    isHolderLongPressed = true;

                    return true;
                }
            };

            holder.tvNotesTitle.setOnLongClickListener(onLongClickListener);
            holder.tvNotesBody.setOnLongClickListener(onLongClickListener);
            holder.dateNoteContainer.setOnLongClickListener(onLongClickListener);


            holder.ivCancelNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.rlNotes.setEnabled(true);
                    holder.llNotesEdit.setVisibility(View.GONE);
                    isHolderLongPressed = false;

                }
            });
            holder.ivDeleteNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.rlNotes.setEnabled(true);
                    holder.llNotesEdit.setVisibility(View.GONE);
                    final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                    deleteDialog.setMessage("Are you sure to delete this " + holder.tvNotesType.getText().toString().trim().toLowerCase());
                    deleteDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Deleting Notes or CheckList
                            DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
                            dataBaseHelper.deleteNotes(notesModels.get(position));
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position,notesModels.size());

                            notesModels.clear();
                            notesModels.addAll(dataBaseHelper.getNotes(trip_id));

                            if(notesModels.size() == 0){
                                no_notes_RL.setVisibility(View.VISIBLE);
                            }else{
                                no_notes_RL.setVisibility(View.GONE);
                            }

                            Snackbar.make(getActivity().findViewById(R.id.fabMenu), "Notes deleted Succesfully", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    deleteDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = deleteDialog.create();
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimationCentreAlert);
                    dialog.show();
                    isHolderLongPressed = false;



                }
            });
            holder.ivEditNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.rlNotes.setEnabled(true);
                    holder.llNotesEdit.setVisibility(View.GONE);
                    isHolderLongPressed = false;
                    if(notesModel.getNote_ContentType() == 1){
                        Intent intent = new Intent(getContext(),NotesEditActivity.class);
                        intent.putExtra("tripId",trip_id);
                        intent.putExtra("editOrAdd","edit");
                        intent.putExtra("notesId",notesModels.get(position).getNote_Id());
                        intent.putExtra("anim","yes");
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(getContext(), CheckListActivityNew.class);
                        intent.putExtra("tripId", trip_id);
                        intent.putExtra("editOrAdd", "edit");
                        intent.putExtra("notesId", notesModels.get(position).getNote_Id());
                        intent.putExtra("anim", "yes");
                        startActivity(intent);
                    }
                   mAdapter.notifyItemChanged(position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return notesModels.size();
        }
    }

    public String decryptUnCompletedTodosasLinesOfText(String noteContent) {
        String[] todosModelsasStrings = noteContent.split(Pattern.quote(Utils.DELIMETER_FOR_TODOS));
        ArrayList<TodoModel> todoModels = new ArrayList<>();
        String notesBody = "";

        for (String s : todosModelsasStrings) {
            TodoModel todoModel = new TodoModel();
            String[] todo = s.split(Pattern.quote(Utils.DELIMETER_FOR_A_TODO));
            todoModel.setName(todo[0].trim());
            if (todo[1].trim().equalsIgnoreCase("t"))
                todoModel.setCompleted(true);
            else
                todoModel.setCompleted(false);


            if (!todoModel.isCompleted())
                todoModels.add(todoModel);
        }

        for(TodoModel todoModel : todoModels){
            String specialCharacter = "\u2022";
            notesBody+=specialCharacter+" ";
            notesBody+=todoModel.getName();
            notesBody+="\n";

            // include a special CHaracter for every item //// TODO: 06-07-2017
        }
        return notesBody;
    }

}
