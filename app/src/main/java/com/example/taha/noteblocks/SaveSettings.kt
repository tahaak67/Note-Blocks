package com.example.taha.noteblocks

import android.content.Context
import android.content.SharedPreferences

class SaveSettings{
    var context: Context?=null
    var sharedRef:SharedPreferences?=null
    constructor(context:Context){
        this.context=context
        sharedRef=context.getSharedPreferences("myRef",Context.MODE_PRIVATE)
    }
    fun saveSettings(id:Boolean){
        val editor=sharedRef!!.edit()
        editor.putBoolean("id",id)
        editor.apply()
    }
    fun loadSettings():Boolean{
        id=sharedRef!!.getBoolean("id",false)
        return id
    }
    companion object{
        var id = false
    }
}