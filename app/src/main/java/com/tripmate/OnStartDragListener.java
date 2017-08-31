package com.tripmate;

import android.support.v7.widget.RecyclerView;

/**
* Created by vinee_000 on 29-08-2017.
*/
interface OnStartDragListener {

   /**
    * Called when a view is requesting a start of a drag.
    *
    * @param viewHolder The holder of the view to drag.
    */
   void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
