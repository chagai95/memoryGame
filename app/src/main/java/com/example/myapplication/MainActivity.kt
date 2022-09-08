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
            testGame(linearLayout, context)
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
                val game = Game(amountOfPlayers,amountOfPairs)
                showGame(linearLayout, context, game)
            }
        }


    }

    private fun testGame(linearLayout: LinearLayout, context: MainActivity) {
        val game = Game(3,8)
        showGame(linearLayout, context, game)


        val button = findViewById<Button>(R.id.startGame)
        val buttonDefault = findViewById<Button>(R.id.startDefaultGame)

        val amountOfPlayersView = findViewById<EditText>(R.id.amountOfPlayers)
        val amountOfPairsView = findViewById<EditText>(R.id.amountOfPairs)
        button.visibility = View.GONE
        buttonDefault.visibility = View.GONE
        amountOfPairsView.visibility = View.GONE
        amountOfPlayersView.visibility = View.GONE
        findViewById<TextView>(R.id.howManyPlayers).visibility = View.GONE
        findViewById<TextView>(R.id.howManyPairs).visibility = View.GONE
    }

    private fun showGame(linearLayout: LinearLayout, context: MainActivity, game: Game) {
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
            ) // assuming the parent view is a LinearLayout


        for (i in 0 until game.board.size) {
            val tableRow = TableRow(context)
            tableRow.layoutParams = tableParams // TableLayout is the parent view
            tableLayout.addView(tableRow)
            for (j in 0 until game.board[i].size) {
                val field = game.board[i][j]
                val card = field.card
                val textView = TextView(context)
                textView.text = if (field.cardState == CardState.HIDDEN) {
                    "X"
                } else {
                    card.textRepresentation
                }
                textView.textSize = 100f
                textView.setOnClickListener {
                    val selected = game.playerPlaying.selected
                    when (game.board[i][j].cardState) {
                        CardState.HIDDEN -> {
                            when (selected.size) {
                                0 -> {
                                    textView.text = card.textRepresentation
                                    field.cardState = CardState.PEEKING
                                    selected.add(field)
                                }
                                1 -> {
                                    val selectedField = selected[0]
                                    if (selectedField.cardState == CardState.TURN_OVER) {
                                        Toast.makeText(
                                            context,
                                            "Turn the other card over as well 😡",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@setOnClickListener
                                    }
                                    textView.text = card.textRepresentation
                                    if (game.isMatch(selectedField.card, card)) {
                                        selected.removeLast().cardState = CardState.MATCHED
                                        field.cardState = CardState.MATCHED
                                        game.playerPlaying.score++
                                        if (game.isGameOver()) {
                                            var winners = ""
                                            for (winner in game.winner()) {
                                                winners = winner.name + ", " + winners
                                            }
                                            Toast.makeText(
                                                context,
                                                "Game Over 😎 $winners are/is the winner 🎉}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "It's a match! You get to try again 😉",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        selectedField.cardState = CardState.TURN_OVER
                                        field.cardState = CardState.TURN_OVER
                                        selected.add(field)
                                        // here we would need a view model action or some kind of reactive state to turn over the other card automatically.
                                        Toast.makeText(
                                            context,
                                            "No match 😥 Please turn over the 2 cards",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                2 -> Toast.makeText(
                                    context,
                                    "You already tried 2 cards, turn them over!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        CardState.TURN_OVER -> {
                            selected.remove(field)
                            field.cardState = CardState.HIDDEN
                            textView.text = "X"
                            if (selected.isEmpty()) {
                                game.nextTurn()
                                title = game.playerPlaying.name
                                Toast.makeText(
                                    context,
                                    "It's ${game.playerPlaying.name}'s turn, please pass them the phone",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        CardState.PEEKING -> Toast.makeText(
                            context,
                            "Please reveal another card first",
                            Toast.LENGTH_SHORT
                        ).show()
                        CardState.MATCHED -> Toast.makeText(
                            context,
                            "This card was already matched 🙂",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                textView.layoutParams = rowParams // TableRow is the parent view
                tableRow.addView(textView)
            }
        }

        linearLayout.addView(tableLayout)
    }
}