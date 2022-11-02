package com.example.soteria

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class ContactDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as ImportRecyclerViewAdapter.ImportContactViewHolder)
                    .getItemDetails()
            }
            return null
        }
}