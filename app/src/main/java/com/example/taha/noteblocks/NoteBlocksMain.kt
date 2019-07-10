package com.example.taha.noteblocks

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import android.os.Vibrator
import kotlinx.android.synthetic.main.activity_note_blocks_main.*
import kotlinx.android.synthetic.main.card.view.*

class NoteBlocksMain : AppCompatActivity(), SensorEventListener {
    //
    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //لسنا بحاجة إليها الأن.
    }

    //متغيرات مبدئية لحساب التغير في قيمة حساسات الجهاز بالنسبة للمحاور الثلاثة
    var xold = 0.0
    var yold = 0.0
    var zold = 0.0
    var threadShould = 3000.0
    var oldtime: Long = 0

    //يتم إستدعاء هذه الدالة في كلما تغيرت قيم الحساسات
    override fun onSensorChanged(event: SensorEvent?) {

        //تخزين إحداثيات الجهاز الحالية
        var x = event!!.values[0]
        var y = event!!.values[1]
        var z = event!!.values[2]

        // قراءة الوقت الحالي من الجهاز
        var currentTime = System.currentTimeMillis()

        // إعطاء فرصة للجهاز قبل تشغيل الدالة مرة أخرى
        if ((currentTime - oldtime) > 100) {
            var timeDiff = currentTime - oldtime
            oldtime = currentTime

            // يتم تحديد ما إذا كانت سرعة حركة الجهاز كافية لتحديد الأمر عن طريق المعادلة
            var speed = Math.abs(x + y + z - xold - yold - zold) / timeDiff * 10000
            if (speed > threadShould) {
                var v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v.vibrate(3000)
                Toast.makeText(applicationContext, "سيتم تشغيل صفحة الملاحظة الجديدة", Toast.LENGTH_LONG).show()
                var newNoteIntent = Intent(this, AddNote::class.java)
                startActivity(newNoteIntent)
            }
        }
    }

    // إنشاء arraylist من نوع الكائن Note
    var noteList = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_blocks_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        //
        LoadQuery("%")
        //
        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        //

    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager!!.unregisterListener(this)
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

                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

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
                    var newNoteIntent = Intent(this, AddNote::class.java)
                    startActivity(newNoteIntent)
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
}
