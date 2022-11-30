package com.example.soteria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soteria.room.models.Contact

class RecyclerViewAdapter(val listener : RowClickListener): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    private var items = ArrayList<Contact>()

    fun setListData(data: ArrayList<Contact>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview1_row, parent, false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvNumber = view.findViewById<TextView>(R.id.tvNumber)
        val tvAccess = view.findViewById<TextView>(R.id.tvAccess)
        val accessMap = mapOf(0 to "No recording access", 1 to "Audio recording access",
                              2 to "Video recording access", 3 to "Audio and video recording access")

        fun bind(data: Contact) {
            tvName.text = data.first_name + " " + data.last_name
            tvNumber.text = data.phone_number
            tvAccess.text = accessMap[data.recording_access]
        }

    }

    interface RowClickListener {
        fun onItemClickListener(contact : Contact)
    }



}