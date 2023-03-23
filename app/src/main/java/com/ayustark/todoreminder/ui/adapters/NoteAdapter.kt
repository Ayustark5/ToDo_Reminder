package com.ayustark.todoreminder.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayustark.todoreminder.databinding.SingleItemTodoBinding
import com.ayustark.todoreminder.db.NoteEntity


class NoteAdapter(
    private val noteList: List<NoteEntity>,
    private val onEditClickListener: (Int) -> Unit?
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val bind: SingleItemTodoBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            SingleItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val bind = holder.bind
        val noteItem = noteList[position]
        setupEventListeners(bind, noteItem)
        populateData(bind, noteItem)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    private fun setupEventListeners(
        bind: SingleItemTodoBinding,
        noteItem: NoteEntity
    ) {
        bind.apply {
            cvNote.setOnClickListener {
                noteItem.expanded = !noteItem.expanded
                noteItem.clicked = true
                notifyItemChanged(noteList.indexOf(noteItem))
            }
            ibEdit.setOnClickListener {
                onEditClickListener(noteList.indexOf(noteItem))
            }
        }
    }

    private fun populateData(bind: SingleItemTodoBinding, noteItem: NoteEntity) {
        bind.apply {
            if (!noteItem.clicked){
                cvNote.apply {
                    alpha = 0.0f
                    scaleX = 0.0f
                    scaleY = 0.0f
                    pivotX = 0F
                    pivotY = 0F

                    animate()
                        .alpha(1f)
                        .scaleXBy(1.0f)
                        .scaleYBy(1.0f)
                        .duration = 700
                }
            }else{
                noteItem.clicked = false
            }
            if (noteItem.expanded) {
                clExpandable.visibility = View.VISIBLE
                ibEdit.visibility = View.VISIBLE
            } else {
                clExpandable.visibility = View.GONE
                ibEdit.visibility = View.GONE
            }
            tvTitle.text = noteItem.title
            tvDesc.text = noteItem.description
            tvEditDate.text = noteItem.creationDate
            tvDueDate.text = noteItem.dueDate
        }
    }
}