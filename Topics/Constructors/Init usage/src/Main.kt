fun main() {
    val timerValue = readLine()!!.toInt()
    val timer = ByteTimer(timerValue)
    println(timer.time)
}

class ByteTimer(var time: Int) {
    init {
        if (time < -128) {
            this.time = -128
        } else if (time > 127) {
            this.time = 127
        }
    }
}
