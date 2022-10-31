package com.example.soteria

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soteria.room.models.Contact

class ImportRecyclerViewAdapter(val contactlist : ArrayList<Contact>): RecyclerView.Adapter<ImportRecyclerViewAdapter.MyViewHolder>() {

    private var items = contactlist
    private var selectedPos = RecyclerView.NO_POSITION
    public var onItemClick: ((Contact) -> Unit)? = null

    public fun update (newContactList:ArrayList<Contact>) {
        items = newContactList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportRecyclerViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview2_row, parent, false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ImportRecyclerViewAdapter.MyViewHolder, position: Int) {

        var contact = items[position]
        holder.bind(contact)

        holder.itemView.setOnClickListener {
            if (selectedPos == position) {
                holder.itemView.setBackgroundColor(Color.CYAN)
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE)
            }
            onItemClick?.invoke(contact)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvNumber = view.findViewById<TextView>(R.id.tvNumber)
        val deleteContactId = view.findViewById<ImageView>(R.id.deleteContactID)

        fun bind(data: Contact) {
            tvName.text = data.first_name + " " + data.last_name
            tvNumber.text = data.phone_number

        }

    }

    interface RowClickListener {
        fun onDeleteUserClickListener(contact : Contact)
        fun onItemClickListener(contact : Contact)
    }

}