package com.example.soteria

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.soteria.room.models.Contact

class ImportRecyclerViewAdapter(val contactlist : ArrayList<Contact>): RecyclerView.Adapter<ImportRecyclerViewAdapter.ImportContactViewHolder>() {

    public var items = contactlist
    public var tracker: SelectionTracker<Long>? = null
    public var onItemClick: ((Contact) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    public fun update (newContactList:ArrayList<Contact>) {
        items = newContactList
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportRecyclerViewAdapter.ImportContactViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview2_row, parent, false)
        return ImportContactViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ImportRecyclerViewAdapter.ImportContactViewHolder, position: Int) {

        var contact = items[position]

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(contact)
        }
        if (tracker!!.isSelected(position.toLong())) {
            holder.itemView.background = ColorDrawable(Color.CYAN)
        } else {
            holder.itemView.background = ColorDrawable(Color.WHITE)
        }
        holder.bind(contact)
    }

    class ImportContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvNumber = view.findViewById<TextView>(R.id.tvNumber)
        val deleteContactId = view.findViewById<ImageView>(R.id.deleteContactID)

        fun bind(data: Contact, isActivated: Boolean = false) {
            tvName.text = data.first_name + " " + data.last_name
            tvNumber.text = data.phone_number
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long? = itemId
                override fun inSelectionHotspot(e: MotionEvent): Boolean = true
            }
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(contact : Contact)
        fun onItemClickListener(contact : Contact)
    }

}