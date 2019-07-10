package com.example.taha.noteblocks

class Note{
    var noteID:Int?=null
    var noteTitle:String?=null
    var noteText:String?=null

    constructor(noteID:Int,noteTitle:String,noteText:String){
        this.noteID=noteID
        this.noteTitle=noteTitle
        this.noteText=noteText
    }
}