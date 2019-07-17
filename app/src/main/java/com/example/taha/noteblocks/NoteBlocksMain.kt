package com.example.taha.noteblocks

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_note_blocks_main.*
import kotlinx.android.synthetic.main.card.view.*

class NoteBlocksMain : AppCompatActivity() {
    //



    // إنشاء arraylist من نوع الكائن Note
    var noteList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_blocks_main)

    }

    override fun onResume() {
        super.onResume()
        //
        LoadQuery("%")
        //
        serviceControl()
    }

    override fun onPause() {
        super.onPause()
        //
        serviceControl()
    }

    override fun onDestroy() {
        super.onDestroy()


    }

    fun LoadQuery(title: String) {

        var myNotesAdapter = myNotesAdapter(this, noteList)
        notesListView.adapter = myNotesAdapter


        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Note_Text")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.query(projections, "Title like ?", selectionArgs, "Title")
        noteList.clear()
        if (cursor.moveToFirst()) {

            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Note_Text"))

                noteList.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        val sv = menu.findItem(R.id.app_bar_search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%" + newText + "%")
                return false
            }
        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNote -> {
                    //go to new note activity
                    val newNoteIntent = Intent(this, AddNote::class.java)
                    startActivity(newNoteIntent)
                }
                R.id.settingsbtn -> {
                    startActivity(Intent(this,Settings::class.java))
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    inner class myNotesAdapter : BaseAdapter {
        var noteListAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(context: Context, noteList: ArrayList<Note>) : super() {
            this.noteListAdapter = noteList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var cardView = layoutInflater.inflate(R.layout.card, null)
            var cardNote = noteListAdapter[position]
            cardView.titleTextView.text = cardNote.noteTitle
            cardView.noteTextView.text = cardNote.noteText

            //حذف ملاحظة
            cardView.delIV.setOnClickListener(View.OnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(cardNote.noteID.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQuery("%")
            })
            cardView.editIV.setOnClickListener(View.OnClickListener {
                update(cardNote)

            })
            return cardView
        }

        override fun getItem(position: Int): Any {
            return noteListAdapter.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return noteListAdapter.size
        }


    }

    fun update(note: Note) {
        //go to new note activity
        var newNoteIntent = Intent(this, AddNote::class.java)
        newNoteIntent.putExtra("ID", note.noteID)
        newNoteIntent.putExtra("Title", note.noteTitle)
        newNoteIntent.putExtra("Note_Text", note.noteText)

        startActivity(newNoteIntent)
    }
    fun serviceControl (){
        val saveSettings = SaveSettings(this)
        val isSerChecked = saveSettings.loadSettings()
        if (isSerChecked) {
            startService(Intent(applicationContext,ShakeSensorService::class.java))
        }else {
            stopService(Intent(applicationContext,ShakeSensorService::class.java))
        }

    }
}
