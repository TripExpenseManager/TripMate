package com.tripmate;

/**
* Created by vinee_000 on 29-08-2017.
*/
interface ItemTouchHelperAdapter {
   boolean onItemMove(int fromPosition, int toPosition);
   void onItemDismiss(int position);
}
