package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayout: LinearLayout = findViewById(R.id.linearlayout)

        val context = this

        val buttonDefaultGame = findViewById<Button>(R.id.startDefaultGame)
        buttonDefaultGame.setOnClickListener {
            defaultGame(linearLayout, context)
        }

        val button = findViewById<Button>(R.id.startGame)
        button.setOnClickListener {
            val amountOfPlayersView = findViewById<EditText>(R.id.amountOfPlayers)
            val amountOfPairsView = findViewById<EditText>(R.id.amountOfPairs)
            val amountOfPlayers = amountOfPlayersView.text.toString().toInt()
            val amountOfPairs = amountOfPairsView.text.toString().toInt()
            if (amountOfPlayers > 1 && amountOfPairs > 1) {
                button.visibility = View.GONE
                buttonDefaultGame.visibility = View.GONE
                amountOfPairsView.visibility = View.GONE
                amountOfPlayersView.visibility = View.GONE
                findViewById<TextView>(R.id.howManyPlayers).visibility = View.GONE
                findViewById<TextView>(R.id.howManyPairs).visibility = View.GONE
                val game = BasicGame(amountOfPlayers,amountOfPairs)
                startGame(linearLayout, context, game)
            }
        }
    }

    private fun defaultGame(linearLayout: LinearLayout, context: MainActivity) {
        val game = BasicGame(3,8)
        startGame(linearLayout, context, game)


        val button = findViewById<Button>(R.id.startGame)
        val buttonDefaultGame = findViewById<Button>(R.id.startDefaultGame)

        val amountOfPlayersView = findViewById<EditText>(R.id.amountOfPlayers)
        val amountOfPairsView = findViewById<EditText>(R.id.amountOfPairs)
        button.visibility = View.GONE
        buttonDefaultGame.visibility = View.GONE
        amountOfPairsView.visibility = View.GONE
        amountOfPlayersView.visibility = View.GONE
        findViewById<TextView>(R.id.howManyPlayers).visibility = View.GONE
        findViewById<TextView>(R.id.howManyPairs).visibility = View.GONE
    }

    private fun startGame(linearLayout: LinearLayout, context: MainActivity, game: Game) {
        title = game.playerPlaying.name


        val tableParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.WRAP_CONTENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        val rowParams: TableRow.LayoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val tableLayout = TableLayout(context)
        tableLayout.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        for (i in 0 until game.board.size) {
            val tableRow = TableRow(context)
            tableRow.layoutParams = tableParams // TableLayout is the parent view
            tableLayout.addView(tableRow)
            for (j in 0 until game.board[i].size) {
                val textView = TextView(context)
                initialBoardSetup(textView)
                val field = game.board[i][j]
                userAction(game, field, textView, context) // This is where the logic of the game happens.
                textView.layoutParams = rowParams // TableRow is the parent view
                tableRow.addView(textView)
            }
        }
        linearLayout.addView(tableLayout)
    }

    private fun initialBoardSetup(
        textView: TextView,
    ) {
        textView.text = "X"
        textView.textSize = 100f
    }

    private fun userAction(game: Game, field: Field, textView: TextView, context: MainActivity) {
        textView.setOnClickListener {
            val card = field.card
            val peeking = game.playerPlaying.peeking
            when (field.cardState) {
                CardState.HIDDEN -> {
                    when (peeking.size) {
                        0 -> { // No fields were selected yet so we can show the card
                            textView.text = card.textRepresentation
                            field.cardState = CardState.PEEKING
                            peeking.add(field)
                        }
                        1 -> { // 1 field is already selected.
                            val selectedField = peeking[0]
                            // We need to check if this selected card is the first to be selected
                            // or if it is in the state where it should be turned over because two cards
                            // were already selected and one was turned over already.
                            if (selectedField.cardState == CardState.TURN_OVER) {
                                toast("Turn the other card over as well 😡", context)
                                return@setOnClickListener
                            }
                            // If it was the first card then we can also show this second card
                            // and check if it matches
                            textView.text = card.textRepresentation
                            if (game.isMatch(selectedField.card, card)) {
                                peeking.removeLast().cardState = CardState.MATCHED
                                field.cardState = CardState.MATCHED
                                game.playerPlaying.score++
                                if (game.isGameOver()) { // After each match we check if the game is over
                                    var winners = ""
                                    for (winner in game.winner()) {
                                        winners = winner.name + ", " + winners
                                    }
                                    toast("Game Over 😎 $winners are/is the winner 🎉}", context)
                                } else {
                                    toast("It's a match! You get to try again 😉", context)
                                }
                            } else { // If it was not a match the player has to turn the cards over to continue
                                selectedField.cardState = CardState.TURN_OVER
                                field.cardState = CardState.TURN_OVER
                                peeking.add(field)
                                // here we would need a view model action
                                // or some kind of reactive state to turn over the other card automatically.
                                // I chose not to get into that - not enough time... Would have been interesting!
                                // It also makes sense to have the user do this because they have to
                                // memorize the positions anyway (although we could just put a timer for this)
                                toast("No match 😥 Please turn over the 2 cards", context)
                            }
                        }
                        2 -> toast("You already tried 2 cards, turn them over!", context)
                    }
                }
                CardState.TURN_OVER -> {
                    peeking.remove(field)
                    field.cardState = CardState.HIDDEN
                    textView.text = "X"
                    if (peeking.isEmpty()) { // If no fields are peeking then we can move to the next player
                        game.nextTurn()
                        title = game.playerPlaying.name
                        toast("It's ${game.playerPlaying.name}'s turn, please pass them the phone", context)
                    }
                }
                // This happens when a player tries to turn a card over before peeking at two cards
                CardState.PEEKING -> toast("Please reveal another card first", context)
                // This happens when a player tries to turn over a card that was already matched
                CardState.MATCHED -> toast("This card was already matched 🙂", context)
            }
        }
    }

    private fun toast(text: String, context: MainActivity) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }
}