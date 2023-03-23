package com.ayustark.todoreminder.ui.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayustark.todoreminder.R
import com.ayustark.todoreminder.databinding.ActivityNoteBinding
import com.ayustark.todoreminder.db.NoteEntity
import com.ayustark.todoreminder.utils.*
import com.ayustark.todoreminder.ui.adapters.NoteAdapter
import com.ayustark.todoreminder.ui.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {

    companion object {
        fun startThisActivity(context: Context) {
            context.startThisActivity<NoteActivity>()
        }
    }

    private val viewModel: NoteViewModel by viewModels()
    private var noteAdapter: NoteAdapter? = null

    private var bottomSheetNoteDetails: BottomSheetBehavior<ConstraintLayout>? = null

    private var editingPosition = 0

    private var sortMode: Int? = null

    private val dateComparator = Comparator<NoteEntity> { note1, note2 ->
        convertStringToDate(note2.dueDate)?.let {
            convertStringToDate(note1.dueDate)?.time?.compareTo(
                it.time
            )
        } ?: 0
    }

    private val titleComparator = Comparator<NoteEntity> { note1, note2 ->
        note1.title.compareTo(note2.title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setActivityAnimation()
        val bind = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(bind.root)
        handleBackPress()
        setupActionBar(bind)
        subscribeToObservers()
        setEventListeners(bind)
        setupUI(bind)
    }

    private fun handleBackPress() {
        OnBackPressedDispatcher().addCallback(
            this@NoteActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetNoteDetails?.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetNoteDetails?.close()
                        return
                    }
                }
            })
    }

    private fun setupActionBar(bind: ActivityNoteBinding) {
        setSupportActionBar(bind.mtToolbar)
        supportActionBar?.title = "Note List"
        createMenu()
    }

    private fun createMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.sort_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.actionSort -> {
                        val builder =
                            MaterialAlertDialogBuilder(this@NoteActivity)
                        builder.setTitle("Sort By")
                        val customLayout: View = layoutInflater.inflate(R.layout.sorting_menu, null)
                        builder.setView(customLayout)
                        val radioGroup: RadioGroup = customLayout.findViewById(R.id.rbGroup)
                        builder.setPositiveButton("Sort") { _, _ ->
                            sortMode = radioGroup.checkedRadioButtonId
                            sortData()
//                            checkRadioButton()
                        }
                        builder.setNegativeButton("Cancel") { _, _ ->

                        }
                        val dialog = builder.create()
                        dialog.show()
                        true
                    }
                    R.id.actionLogout -> {
                        viewModel.logout()
                        true
                    }
                    else -> false
                }
            }

        }, this@NoteActivity, Lifecycle.State.RESUMED)
    }

    private fun sortData() {
        when (sortMode) {
            R.id.rbDateAsc -> {
                Collections.sort(viewModel.noteList, dateComparator)
            }
            R.id.rbDateDec -> {
                Collections.sort(viewModel.noteList, dateComparator)
                viewModel.noteList.reverse()
            }
            R.id.rbAlphaAsc -> {
                Collections.sort(viewModel.noteList, titleComparator)
            }
            R.id.rbAlphaDec -> {
                Collections.sort(viewModel.noteList, titleComparator)
                viewModel.noteList.reverse()
            }
            else -> {
                return
            }
        }
        noteAdapter?.notifyDataSetChanged()
    }

    private fun subscribeToObservers() {
        viewModel.logoutLiveData.observe(this@NoteActivity) {
            when (it) {
                is Resource.Loading -> {
                    Log.d("LogoutResponse", "Loading")
                }
                is Resource.Error -> {
                    Log.e("LogoutResponse", "${it.message}")
                    showToast("${it.message}")
                }
                is Resource.Success -> {
                    showToast("Logged Out")
                    LoginActivity.startThisActivity(this@NoteActivity)
                    finish()
                }
            }
        }
    }

    private fun setEventListeners(bind: ActivityNoteBinding) {
        bind.apply {
            fabAddTodo.setOnClickListener {
                overlay.isVisible = true
                bottomSheetNoteDetails?.open()
            }
            overlay.setOnClickListener {
                bottomSheetNoteDetails?.close()
            }
            bottomSheet.btnCreate.setOnClickListener {
                onBtnCreatePress()
            }
            bottomSheet.btnEdit.setOnClickListener {
                onBtnEditPress()
            }
            bottomSheet.btnRemove.setOnClickListener {
                onBtnRemovePress()
            }
            bottomSheet.tilDue.editText?.setOnClickListener {
                showDateTimePickerDialog(this)
            }
        }
    }

    private fun ActivityNoteBinding.onBtnCreatePress() {
        val note = NoteEntity(
            bottomSheet.tilTitle.editText?.text.toString(),
            bottomSheet.tilDesc.editText?.text.toString(),
            bottomSheet.tilDue.editText?.text.toString()
        )
        viewModel.noteList.add(note)
        noteAdapter?.notifyItemInserted(viewModel.noteList.indexOf(note))
        bottomSheetNoteDetails?.close()
        sortData()
    }

    private fun ActivityNoteBinding.onBtnEditPress() {
        val note = viewModel.noteList[editingPosition]
        note.title = bottomSheet.tilTitle.editText?.text.toString()
        note.description = bottomSheet.tilDesc.editText?.text.toString()
        note.dueDate = bottomSheet.tilDue.editText?.text.toString()
        noteAdapter?.notifyItemChanged(editingPosition)
        bottomSheetNoteDetails?.close()
        sortData()
    }

    private fun onBtnRemovePress() {
        noteAdapter?.notifyItemRangeRemoved(editingPosition, 1)
        viewModel.noteList.removeAt(editingPosition)
        bottomSheetNoteDetails?.close()
    }

    private fun setupUI(bind: ActivityNoteBinding) {
        setupBottomSheet(bind)
        setupRecyclerView(bind)
    }

    private fun setupBottomSheet(bind: ActivityNoteBinding) {
        bind.apply {
            bottomSheetNoteDetails =
                BottomSheetBehavior.from(bottomSheet.containerBottomSheet).apply {
                    halfExpandedRatio = 0.9F
                    addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheetView: View, newState: Int) {
                            when (newState) {
                                BottomSheetBehavior.STATE_EXPANDED -> {
                                    overlay.isVisible = true
                                    bottomSheet.tilTitle.editText?.requestFocus()
                                }
                                BottomSheetBehavior.STATE_COLLAPSED -> {
                                    overlay.isVisible = false
                                    updateBottomSheet(bind, false)
                                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                                        bottomSheet.btnCreate.windowToken,
                                        0
                                    )
                                }
                                else -> {
                                    //Do nothing
                                }
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            //Do nothing
                        }

                    })
                }
        }
    }

    private fun setupRecyclerView(bind: ActivityNoteBinding) {
        noteAdapter = NoteAdapter(
            viewModel.noteList
        ) { position: Int ->
            editingPosition = position
            val note = viewModel.noteList[position]
            updateBottomSheet(bind, true, note)
            bottomSheetNoteDetails?.open()
        }
        bind.rvTodoList.layoutManager = LinearLayoutManager(this@NoteActivity)
        bind.rvTodoList.adapter = noteAdapter
    }

    private fun showDateTimePickerDialog(bind: ActivityNoteBinding) {
        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)
        var hour = c.get(Calendar.HOUR)
        var min = c.get(Calendar.MINUTE)
        var am = c.get(Calendar.AM_PM)

        if (bind.bottomSheet.tilDue.editText?.text?.isNotEmpty() == true) {
            val dateTime = bind.bottomSheet.tilDue.editText?.text?.toString()?.split(' ')
            val date = dateTime?.get(0)?.split('/')
            val time = dateTime?.get(1)?.split(':')
            val timeAMPM = dateTime?.get(2)
            date?.let {
                year = date[2].toInt().plus(2000)
                month = date[1].toInt().minus(1)
                day = date[0].toInt()
            }
            time?.let {
                hour = time[0].toInt()
                min = time[1].toInt()
            }
            timeAMPM?.let {
                am = if (it == "AM") 0 else 1
            }
        }
        showDatePickerDialog(bind, year, month, day, hour, min, am)
    }

    private fun showDatePickerDialog(
        bind: ActivityNoteBinding,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        min: Int,
        am: Int
    ) {
        val dpd = DatePickerDialog(this@NoteActivity, { _, dueYear, dueMonth, dueDay ->
            bind.bottomSheet.tilDue.editText?.setText(
                getString(
                    R.string.dob_str,
                    dueDay,
                    dueMonth + 1,
                    dueYear
                )
            )
            showTimePickerDialog(bind, hour, min, am)
        }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis()

        dpd.show()
    }

    private fun showTimePickerDialog(bind: ActivityNoteBinding, hour: Int, min: Int, am: Int) {
        TimePickerDialog(
            this@NoteActivity,
            { _, hourOfDay, minute ->
                var amPm = 0
                var hourIn12 = hourOfDay
                if (hourOfDay == 0) {
                    hourIn12 = 12
                } else if (hourOfDay == 12) {
                    amPm = 1
                } else if (hourOfDay > 12) {
                    amPm = 1
                    hourIn12 = hourOfDay.minus(12)
                }
                setDateTime(bind, "$hourIn12:$minute ${if (amPm == 0) "AM" else "PM"}")
            },
            if (hour == 12 && am == 0) hour.minus(12) else hour.plus(if (am == 1 && hour != 12) 12 else 0),
            min,
            false
        ).show()
    }

    private fun setDateTime(bind: ActivityNoteBinding, time: String) {
        val date = bind.bottomSheet.tilDue.editText?.text.toString()
        bind.bottomSheet.tilDue.editText?.setText(
            convertStringToDate(
                "$date $time"
            )
                ?.let { convertDateToString(it) }
        )
    }

    private fun updateBottomSheet(
        bind: ActivityNoteBinding,
        isEditing: Boolean,
        note: NoteEntity? = null
    ) {
        bind.bottomSheet.apply {
            tilTitle.editText?.setText(note?.title)
            tilDesc.editText?.setText(note?.description)
            tilDue.editText?.setText(note?.dueDate)
            when (isEditing) {
                false -> {
                    btnCreate.visibility = View.VISIBLE
                    btnEdit.visibility = View.GONE
                    btnRemove.visibility = View.GONE
                }
                true -> {
                    btnCreate.visibility = View.GONE
                    btnEdit.visibility = View.VISIBLE
                    btnRemove.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun BottomSheetBehavior<ConstraintLayout>.open() {
        state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun BottomSheetBehavior<ConstraintLayout>.close() {
        state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this@NoteActivity, msg, Toast.LENGTH_SHORT)
            .show()
    }
}