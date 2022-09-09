package com.example.myapplication

class BasicGame(private val amountOfPlayers: Int, private val amountOfPairs: Int) : Game() {

    // Create
    override fun createPlayers(): List<Player> {
        val list = mutableListOf<Player>()
        for (i in 1..amountOfPlayers) {
            list.add(Player("Player$i"))
        }
        return list
    }

    // Create an easy board just as an example
    override fun createBoard(): List<List<Field>> {
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

    // Returns floor of square root of x to make it easier to create a board
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
}
