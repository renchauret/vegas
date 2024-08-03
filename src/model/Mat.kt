package model

import extensions.filterDuplicates

class Mat(playerCount: Int) {
    private val dice: Array<Int> = Array(playerCount) { 0 }
    private var dollars: List<Int> = mutableListOf()

    init {
        setDollars()
    }

    private fun setDollars() {
        dollars = mutableListOf()
        while (dollars.sum() < 5) {
            (dollars as MutableList<Int>).add((1..10).random())
        }
        dollars = dollars.sortedDescending()
    }

    fun addDice(player: Int, diceToAdd: Int) {
        dice[player] += diceToAdd
    }

    private fun calculateScores(): List<Pair<Int, Int>> {
        val scores = mutableListOf<Pair<Int, Int>>()
        dollars.forEach {
            val maxDice = dice.filterIndexed {
                index, _ -> scores.find { score -> score.first == index } == null
            }.filterDuplicates().maxOrNull()
            if (maxDice == null || maxDice <= 0) {
                return@forEach
            }
            val maxIndex = dice.indexOf(maxDice)
            scores.add(Pair(maxIndex, it))
        }
//        println(scores)
        return scores
    }

    fun setScores(setScore: (Int, Int) -> Unit) {
        calculateScores().forEach { (playerIndex, score) ->
            setScore(playerIndex, score)
        }
        reset()
    }

    private fun reset() {
        dice.fill(0)
        setDollars()
    }

    override fun toString(): String {
        return "${dice.joinToString(" ")}:\t[${dollars.joinToString(", $", "$")}]"
    }
}

class Mats(
    private val playerCount: Int
) {
    private val mats: Map<DieNumber, Mat> = createMats()

    private fun createMats(): Map<DieNumber, Mat> {
        return DieNumber.entries.associateWith { Mat(playerCount) }
    }

    fun addDice(player: Int, dieNumber: DieNumber, diceToAdd: Int) {
        mats[dieNumber]?.addDice(player, diceToAdd)
    }

    override fun toString(): String {
        val playerString = "Players:\t" + (1..playerCount).joinToString(" ") { "$it" }
        return playerString + "\n" + mats.entries.joinToString("\n") { (diceNumber, mat) ->
            "$diceNumber:\t\t$mat"
        }
    }

    fun setScores(setScore: (Int, Int) -> Unit) {
        println("Setting scores")
        mats.forEach { (_, mat) ->
            mat.setScores(setScore)
        }
    }
}
