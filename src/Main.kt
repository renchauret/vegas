import model.*

fun main() {
    println("Welcome to Las Vegas!")
    println("How many human players are there?")
    val humanPlayerCount = readlnOrNull()?.toIntOrNull() ?: throw Throwable("Invalid player count")
    println("How many random bot players are there?")
    val botPlayerCount = readlnOrNull()?.toIntOrNull() ?: throw Throwable("Invalid player count")
//    val humanPlayerCount = 0
//    val botPlayerCount = 3
    val totalPlayerCount = humanPlayerCount + botPlayerCount
    val mats = Mats(totalPlayerCount)
    val humanPlayers: Array<Player> = Array(humanPlayerCount) { Human("p${it + 1}") }
    val botPlayers: Array<Player> = Array(botPlayerCount) { RandomBot(it + 1) }
    val players = humanPlayers + botPlayers
    val MAXROUNDS = 4

    fun playRound(roundNumber: Int) {
        println("Round $roundNumber of $MAXROUNDS")
        fun playCycle() {
            players.forEachIndexed { index, player ->
                var diceSet = false
                fun setDice(dieNumber: DieNumber, diceToAdd: Int, removeDice: (diceToRemove: Int) -> Unit) {
                    if (diceSet || diceToAdd > player.getDiceRemaining()) {
                        return
                    }
                    removeDice(diceToAdd)
                    mats.addDice(index, dieNumber, diceToAdd)
                    diceSet = true
                }

                println(mats)
                player.playTurn(::setDice)
                // if (!diceSet) throw Throwable("Player must set dice")
            }
        }
        while (players.any { it.getDiceRemaining() > 0 }) {
            playCycle()
            println(players.joinToString("\n"))
        }
        println(mats)
        mats.setScores { playerIndex, score ->
            players[playerIndex].addMoney(score)
        }
        println(players.joinToString("\n"))
        players.forEach { it.reset() }
        println()
    }

    (1..4).forEach {
        playRound(it)
    }
    val winner = players.maxByOrNull { it.getMoney() }
    println("The winner is ${winner?.getName()} with $${winner?.getMoney()}!")
}