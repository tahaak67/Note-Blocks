package com.example.taha.noteblocks

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNote : AppCompatActivity() {
    var id: Int = 0
    var changed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        if (ShakeSensorService.isServiceRunning){
            stopService(Intent(applicationContext,ShakeSensorService::class.java))
            changed=true
        }
        try {

            //متغير للإحتفاظ ببيانات الملاحظة في حالة التعديل
            var bundle: Bundle = intent.extras
            id = bundle.getInt("ID", 0)
            titleText.setText(bundle.getString("Title"))
            noteText.setText(bundle.getString("Note_Text"))

        } catch (ex: Exception) {

        }
    }

    fun addNote(view: View) {
        var dbManager = DbManager(this)
        var values = ContentValues()
        values.put("Title", titleText.text.toString())
        values.put("Note_Text", noteText.text.toString())

        if (id == 0) {

            var ID = dbManager.insert(values)
            if (ID > 0) {
                Toast.makeText(this, "تم إضافة الملاحظة بنجاح", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "حدث خطأ: لم يتم إضافة الملاحظة", Toast.LENGTH_SHORT).show()
            }
        }else{
            val selectionArgs= arrayOf(id.toString())
            val ID = dbManager.edit(values,"ID=?",selectionArgs)
            if (ID > 0) {
                Toast.makeText(this, "تم تعديل الملاحظة بنجاح", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "حدث خطأ: لم يتم تعديل الملاحظة", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (changed){
            startService(Intent(applicationContext,ShakeSensorService::class.java))
        }
        finish()
    }
}
