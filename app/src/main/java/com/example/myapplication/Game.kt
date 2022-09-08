package com.example.myapplication


class Game(amountOfPlayers: Int, amountOfPairs: Int) {

    val board = createBoard(amountOfPairs)
    private val players = createPlayers(amountOfPlayers)
    var playerPlayingIndex = 0
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
        return card1.textRepresentation == card2.textRepresentation // should be done via the card itself but for now leaving it like this
    }



    // create an easy board just as an example
    private fun createBoard(amountOfPairs: Int): List<List<Field>> {
        val cards: MutableSet<Card> = mutableSetOf()
        for (i in 1..amountOfPairs) { // starting from 1 because we are showing this to the user
            cards.add(TextCard(i.toString()))
            cards.add(TextCard(i.toString()))
        }
        val shuffledCards = cards.shuffled()
        var shuffledCardsIndex = 0
        val board: MutableList<MutableList<Field>> = mutableListOf()
        val dimensionToBeginWith = floorSqrt(shuffledCards.size)
        for (i in 0 until dimensionToBeginWith) {
            board.add(mutableListOf())
            for (j in 0 until dimensionToBeginWith) {
                board[i].add(Field(shuffledCards[shuffledCardsIndex]))
                shuffledCardsIndex ++
            }
        }
        // one extra row for the rest of the cards
        board.add(mutableListOf())
        while (shuffledCardsIndex < shuffledCards.size) {
            board[dimensionToBeginWith].add(Field(shuffledCards[shuffledCardsIndex]))
            shuffledCardsIndex ++
        }
        return board
    }

    // TODO put this is utils
    // Returns floor of square root of x
    private fun floorSqrt(x: Int): Int {
        // Base cases
        if (x == 0 || x == 1) return x

        // Starting from 1, try all numbers until
        // i*i is greater than or equal to x.
        var i = 1
        var result = 1
        while (result <= x) {
            i++
            result = i * i
        }
        return i - 1
    }

    private fun createPlayers(amountOfPlayers: Int): List<Player> {
        val list = mutableListOf<Player>()
        for (i in 1..amountOfPlayers) {
            list.add(Player("Player$i"))
        }
        return list
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