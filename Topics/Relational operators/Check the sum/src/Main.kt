fun main() {
    // put your code here
    val scanner = java.util.Scanner(System.`in`)
    val a = scanner.nextInt()
    val b = scanner.nextInt()
    val c = scanner.nextInt()
    println(20 in listOf(a + b, a + c, b + c))
}
