package model

enum class DieNumber(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    companion object {
        fun of(value: Int): DieNumber = entries.first { it.value == value }

        fun min(): Int = ONE.value
        fun max(): Int = SIX.value
    }
}

abstract class Player {
    abstract fun getName(): String

    private var money: Int = 0
    fun getMoney(): Int = money
    fun addMoney(amount: Int) {
        money += amount
    }

    private val MAXDICE = 8
    private var diceRemaining = MAXDICE
    fun getDiceRemaining(): Int = diceRemaining
    private fun rollDice(): Array<Int> {
        fun rollDie(): Int = (DieNumber.min()..DieNumber.max()).random()
        return Array(diceRemaining) { rollDie() }
    }
    private fun removeDice(count: Int) {
        if (count > this.diceRemaining) {
            throw Throwable("Cannot remove more dice than player has")
        }
        this.diceRemaining -= count
    }

    fun reset() {
        diceRemaining = MAXDICE
    }

    abstract fun pickDie(diceCounts: Map<DieNumber, Int>): DieNumber
    fun playTurn(setDice: (DieNumber, Int, (Int) -> Unit) -> Unit) {
        println("${getName()}'s turn ($diceRemaining dice remaining)")
        if (diceRemaining == 0) {
            // throw Throwable("Cannot play turn with no dice remaining")
            println("Player ${getName()} has no dice remaining, skipping turn")
            return
        }
        val dice = rollDice()
        println("Rolled: ${dice.joinToString()}")
        val diceCounts = dice.sorted().groupingBy { DieNumber.of(it) }.eachCount()
        println("Count of each die number: $diceCounts")
        val chosenDieNumber = pickDie(diceCounts)
        val diceToAdd = diceCounts[chosenDieNumber]
            ?: throw Throwable("Must choose a dice number with at least one die")
        println("Player ${getName()} chooses $chosenDieNumber with $diceToAdd dice")
        setDice(chosenDieNumber, diceToAdd, ::removeDice)
    }

    override fun toString(): String = "${getName()} - $${getMoney()} - $diceRemaining dice"
}

class Human(
    private val name: String
): Player() {
    override fun getName(): String = name

    override fun pickDie(diceCounts: Map<DieNumber, Int>): DieNumber {
        println("Which die number would you like to set?")
        var dieNumber: DieNumber? = null
        while (dieNumber == null) {
            try {
                dieNumber = DieNumber.of(readlnOrNull()?.toIntOrNull() ?: throw Throwable("Invalid die number"))
                if (diceCounts[dieNumber] == null) {
                    dieNumber = null
                    throw Throwable("You must choose a die number that you rolled.")
                }
            } catch (e: Throwable) {
                println(e.message)
            }
        }
        return dieNumber
    }
}
class RandomBot(
    private val index: Int
): Player() {
    override fun getName(): String = "b$index"

    override fun pickDie(diceCounts: Map<DieNumber, Int>): DieNumber {
        return diceCounts.keys.random()
    }
}
