package com.example.myapplication


abstract class Game() {

    val board by lazy {
        createBoard() // not the best practice but everything else seemed worse - https://stackoverflow.com/a/50222496/9921564
        // The board could be extended so it might be a good idea to make it mutable, perhaps that's a better solution
    }

    abstract fun createBoard(): List<List<Field>>

    private val players by lazy {
        createPlayers() // same goes for this as for the board comment
    }

    abstract fun createPlayers(): List<Player>

    private var playerPlayingIndex = 0
    val playerPlaying: Player
        get() = players[playerPlayingIndex]

    fun nextTurn() {
        if (playerPlayingIndex == players.size - 1) {
            playerPlayingIndex = 0
        } else {
            playerPlayingIndex++
        }
    }

    fun isMatch(card1: Card, card2: Card): Boolean {
        return card1.textRepresentation == card2.textRepresentation // should be done via the card equal/hash method itself but for now leaving it like this
    }

    fun isGameOver(): Boolean {
        var isGameOver = true
        for (row in board) {
            isGameOver = isGameOver && row.all { field -> field.cardState == CardState.MATCHED }
        }
        return isGameOver
    }

    fun winner(): List<Player> {
        val listOfWinners = mutableListOf<Player>()
        val maxScore = players.maxByOrNull { player -> player.score }?.score
        for (player in players) {
            if (maxScore == player.score) {
                listOfWinners.add(player)
            }
        }
        return listOfWinners
    }
}