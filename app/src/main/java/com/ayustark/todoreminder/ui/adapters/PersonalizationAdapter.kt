package com.ayustark.todoreminder.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayustark.todoreminder.network.responses.PersonalizationSequence

class PersonalizationAdapter() :
    RecyclerView.Adapter<PersonalizationAdapter.PersonalizationViewHolder>() {

    private val personalizationSequence: ArrayList<PersonalizationSequence> = arrayListOf()

    inner class PersonalizationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv1: TextView = view.findViewById(android.R.id.text1)
        val tv2: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalizationViewHolder {
        return PersonalizationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PersonalizationViewHolder, position: Int) {
        val item = personalizationSequence[position]
        holder.tv1.text = item.widgetId
        holder.tv2.text = item.widgetName
    }

    override fun getItemCount(): Int {
        return personalizationSequence.size
    }

    fun setItemList(itemList: List<PersonalizationSequence>){
        personalizationSequence.clear()
        personalizationSequence.addAll(itemList)
        notifyDataSetChanged()
    }
}