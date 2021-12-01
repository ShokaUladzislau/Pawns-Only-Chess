package chess

import kotlin.system.exitProcess

class Player(val name: String, val color: String)

class GameState(
    var moveNumber: Int,
    val player1: Player,
    val player2: Player,
    val board: Array<CharArray>,
    var lastMove: String,
    val coordinates: Map<Char, Int> = mapOf(
        'a' to 1,
        '1' to 1,
        'b' to 2,
        '2' to 2,
        'c' to 3,
        '3' to 3,
        'd' to 4,
        '4' to 4,
        'e' to 5,
        '5' to 5,
        'f' to 6,
        '6' to 6,
        'g' to 7,
        '7' to 7,
        'h' to 8,
        '8' to 8
    )
)

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val player1 = Player(readLine().toString(), "white")
    println("Second Player's name:")
    val player2 = Player(readLine().toString(), "black")
    val game = GameState(1, player1, player2, makeBoard(player1, player2), "x0y0")

    printBoard(game.board)
    while (true) {
        playGame(game)
    }
}

fun playGame(game: GameState) {
    val isTurnOk = if (game.moveNumber % 2 != 0) makeTurn(game, game.player1) else makeTurn(game, game.player2)
    checkWinCondition(game)
    if (isTurnOk) game.moveNumber++
}

fun makeTurn(game: GameState, player: Player): Boolean {
    val oppositePlayer = if (player == game.player1) game.player2 else game.player1
    val direction = if (player == game.player1) -1 else 1

    if (countPlayerPawns(game, player) == countPlayerStalematePawns(game, player, oppositePlayer, direction)) exit("Stalemate!")
    println("${player.name}'s turn:")

    val input = readLine().toString()
    val regex = "[a-hA-H][1-8][a-hA-H][1-8]".toRegex()

    when {
        (input == "exit") -> {
            exit("")
        }
        (input.matches(regex)) -> {
            val startColumn = game.coordinates[input[0]]!! - 1
            val startRow = game.coordinates[input[1]]!!
            val finishColumn = game.coordinates[input[2]]!! - 1
            val finishRow = game.coordinates[input[3]]!!

            val coordinates = mapOf(
                "startRow" to startRow,
                "startColumn" to startColumn,
                "finishRow" to finishRow,
                "finishColumn" to finishColumn
            )

            when {
                (checkForEnPassant(5, -1, player, game, coordinates, game.lastMove[1] - game.lastMove[3])) -> {
                    makeEnPassantMove(game, coordinates, input, -1)
                    return true
                }
                (checkForEnPassant(5, 1, player, game, coordinates, game.lastMove[1] - game.lastMove[3])) -> {
                    makeEnPassantMove(game, coordinates, input, 1)
                    return true
                }
                (checkForEnPassant(4, -1, player, game, coordinates, game.lastMove[3] - game.lastMove[1])) -> {
                    makeEnPassantMove(game, coordinates, input, -1)
                    return true
                }
                (checkForEnPassant(4, 1, player, game, coordinates, game.lastMove[3] - game.lastMove[1])) -> {
                    makeEnPassantMove(game, coordinates, input, 1)
                    return true
                }
                (game.board[game.board.size - startRow][startColumn] == player.color.first().uppercaseChar() &&
                        game.board[game.board.size - finishRow][finishColumn] == oppositePlayer.color.first().uppercaseChar() &&
                        startColumn == finishColumn - 1 ||
                        game.board[game.board.size - startRow][startColumn] == player.color.first().uppercaseChar() &&
                        game.board[game.board.size - finishRow][finishColumn] == oppositePlayer.color.first().uppercaseChar() &&
                        startColumn == finishColumn + 1
                        ) -> {
                    makeStraightMove(input, game, coordinates)
                    return true
                }
                (game.board[game.board.size - startRow][startColumn] == player.color.first().uppercaseChar() &&
                        game.board[game.board.size - finishRow][finishColumn] == oppositePlayer.color.first().uppercaseChar()) &&
                        startColumn == finishColumn ||
                        player == game.player1 && startRow > 2 && finishRow - startRow > 1 ||
                        player == game.player2 && startRow < 7 && startRow - finishRow > 1 ||
                        input.take(2) == input.takeLast(2) ||
                        startColumn != finishColumn ||
                        player == game.player1 && startRow > finishRow ||
                        player == game.player2 && startRow < finishRow ||
                        player == game.player1 && finishRow - startRow > 2 ||
                        player == game.player2 && startRow - finishRow > 2
                -> {
                    println("Invalid Input")
                    return false
                }
                (game.board[game.board.size - startRow][startColumn] != player.color.first().uppercaseChar()) -> {
                    println("No ${player.color} pawn at ${input.take(2)}")
                    return false
                }
                else -> {
                    makeStraightMove(input, game, coordinates)
                    return true
                }
            }
        }
        (!input.matches(regex)) -> {
            println("Invalid Input")
            return false
        }
    }
    return false
}

fun makeStraightMove(input: String, game: GameState, coordinates: Map<String, Int>) {
    game.board[game.board.size - coordinates.getValue("finishRow")][coordinates.getValue("finishColumn")] =
        game.board[game.board.size - coordinates.getValue("startRow")][coordinates.getValue("startColumn")]
    game.board[game.board.size - coordinates.getValue("startRow")][coordinates.getValue("startColumn")] = ' '
    game.lastMove = input

    printBoard(game.board)
}

fun makeEnPassantMove(game: GameState, coordinates: Map<String, Int>, input: String, direction: Int) {
    game.board[game.board.size - coordinates.getValue("finishRow")][coordinates.getValue("finishColumn")] =
        game.board[game.board.size - coordinates.getValue("startRow")][coordinates.getValue("startColumn")]
    game.board[game.board.size - coordinates.getValue("startRow")][coordinates.getValue("startColumn")] = ' '
    game.board[game.board.size - coordinates.getValue("startRow")][coordinates.getValue("startColumn") + direction] = ' '
    game.lastMove = input

    printBoard(game.board)
}

fun makeBoard(player1: Player, player2: Player): Array<CharArray> {
    val board = Array(8) { CharArray(8) }
    for (i in board.indices) {
        for (j in board[i].indices) {
            when (i) {
                1 -> board[i][j] = player2.color.first().uppercaseChar()
                6 -> board[i][j] = player1.color.first().uppercaseChar()
                else -> board[i][j] = ' '
            }
        }
    }
    return board
}

fun printBoard(board: Array<CharArray>) {
    val gameBoard = Array(18) { CharArray(35) }
    for (i in gameBoard.indices) {
        for (j in gameBoard[i].indices) {
            when {
                ((i == gameBoard.lastIndex)) -> gameBoard[i] = "    a   b   c   d   e   f   g   h  ".toCharArray()
                (i % 2 == 0 || i == 0) -> gameBoard[i] = "  +---+---+---+---+---+---+---+---+".toCharArray()
                (i % 2 != 0) -> {
                    when {
                        (j == 0) -> gameBoard[i][j] = Char((gameBoard.size - i) / 2 + 48)
                        (j % 4 == 0 && j >= 2) -> gameBoard[i][j] = board[(i - 1) / 2][(j / 4) - 1]
                        ((j - 2) % 4 == 0) -> gameBoard[i][j] = '|'
                        else -> gameBoard[i][j] = ' '
                    }
                }
            }
        }
    }
    gameBoard.forEach { println(it) }
}

fun checkWinCondition(game: GameState) {
    for (i in game.board.indices) {
        for (j in game.board[i].indices) {
            when {
                (countPlayerPawns(game, game.player2) == 0) -> exit("White Wins!")
                (i == 0 && game.board[i][j] == game.player1.color.first().uppercaseChar()) -> exit("White Wins!")
                (countPlayerPawns(game, game.player1) == 0) -> exit("Black Wins!")
                (i == game.board.size - 1 && game.board[i][j] == game.player2.color.first()
                    .uppercaseChar()) -> exit("Black Wins!")
            }
        }
    }
}

fun checkForEnPassant(rowForEnPassant: Int, direction: Int, player: Player, game: GameState, coordinates: Map<String, Int>, difBetweenCells: Int): Boolean {
    return player == game.player1 &&
            coordinates.getValue("startRow") == rowForEnPassant &&
            coordinates.getValue("startColumn") in 2..6 &&
            game.board[game.board.size - coordinates.getValue("startRow")][coordinates.getValue("startColumn") + direction] == game.player2.color.first().uppercaseChar() &&
            coordinates.getValue("finishColumn") == coordinates.getValue("startColumn") + direction &&
            difBetweenCells == 2 &&
            coordinates.getValue("finishColumn") == game.coordinates[game.lastMove[2]]!! + direction &&
            coordinates.getValue("finishRow") == game.coordinates[game.lastMove[3]]!! + 1
}

fun countPlayerStalematePawns(game: GameState, player: Player, oppositePlayer: Player, direction: Int): Int {
    var stalematePawns = 0

    for (i in game.board.indices) {
        for (j in game.board[i].indices) {
            when {
                (j == 0 &&
                        game.board[i][j] == player.color.first().uppercaseChar() &&
                        game.board[i + direction][j] == oppositePlayer.color.first().uppercaseChar() &&
                        game.board[i + direction][j + 1] == ' ') -> {
                    stalematePawns++
                }
                (j == game.board.size - 1 &&
                        game.board[i][j] == player.color.first().uppercaseChar() &&
                        game.board[i + direction][j] == oppositePlayer.color.first().uppercaseChar() &&
                        game.board[i + direction][j - 1] == ' ') -> {
                    stalematePawns++
                }
                (j in 2..6 &&
                        game.board[i][j] == player.color.first().uppercaseChar() &&
                        game.board[i + direction][j] == oppositePlayer.color.first().uppercaseChar() &&
                        game.board[i + direction][j - 1] == ' ' &&
                        game.board[i + direction][j + 1] == ' ') -> {
                    stalematePawns++
                }
            }
        }
    }
    return stalematePawns
}

fun countPlayerPawns(game: GameState, player: Player): Int {
    var pawns = 0

    for (i in game.board.indices) {
        for (j in game.board[i].indices) {
            if (game.board[i][j] == player.color.first().uppercaseChar()) {
                pawns++
            }
        }
    }
    return pawns
}

fun exit(reason: String) {
    println("$reason\nBye!")
    exitProcess(0)
}