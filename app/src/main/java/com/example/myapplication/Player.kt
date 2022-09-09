package com.example.myapplication

class Player(val name: String) {
    var score: Int = 0
    val peeking: MutableList<Field> = mutableListOf()
}