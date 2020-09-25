package com.example.downloadwhatsapstatus.adapter


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadwhatsapstatus.R
import kotlinx.android.synthetic.main.rt_status.view.*



class StatusAdapter : RecyclerView.Adapter<StatusAdapter.MyViewHolder>() {

    private val statuses = mutableListOf<String>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rt_status, parent, false)
        )

        return holder
    }


    override fun getItemCount(): Int {
        if (statuses.isEmpty())
            return 0
        return statuses.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context

        holder.itemView.rt_iv.setImageURI(Uri.parse(statuses[position]))

    }

    fun setList(list:List<String>){
        statuses.clear()
        statuses.addAll(list)
    }

}