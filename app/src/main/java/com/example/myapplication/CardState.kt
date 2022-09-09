package com.example.myapplication

enum class CardState {
    HIDDEN,
    MATCHED,
    PEEKING,
    TURN_OVER // This state means the card needs to be turned over because the player's turn is over ;)
}