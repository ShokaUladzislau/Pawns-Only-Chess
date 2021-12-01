fun main() {
    // put your code here
    val a = List(3) { readLine()!!.toInt() }
    val b = a.filter { it > 0 }
    println(b.size == 1)
}
