fun main() {
        var a = readLine()!!.toInt()
        var b = readLine()!!.toInt()
        var c = readLine()!!.toInt()

        if (a != b && b != c && c != a) {
                print(true)
        } else {
               print(false)
        }
}
