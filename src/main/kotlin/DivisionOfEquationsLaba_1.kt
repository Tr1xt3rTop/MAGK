typealias IAE = IllegalArgumentException

data class Solution(val quotient: DoubleArray, val remainder: DoubleArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Solution

        if (!quotient.contentEquals(other.quotient)) return false
        if (!remainder.contentEquals(other.remainder)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quotient.contentHashCode()
        result = 31 * result + remainder.contentHashCode()
        return result
    }
}

fun polyDegree(p: DoubleArray): Int {
    for (i in p.size - 1 downTo 0) {
        if (p[i] != 0.0) return i
    }
    return Int.MIN_VALUE
}

fun polyShiftRight(p: DoubleArray, places: Int): DoubleArray {
    if (places <= 0) return p
    val pd = polyDegree(p)
    if (pd + places >= p.size) {
        throw IAE("Количество мест для смещения слишком велико")
    }
    val d = p.copyOf()
    for (i in pd downTo 0) {
        d[i + places] = d[i]
        d[i] = 0.0
    }
    return d
}

fun polyMultiply(p: DoubleArray, m: Double) {
    for (i in p.indices) p[i] *= m
}

fun polySubtract(p: DoubleArray, s: DoubleArray) {
    for (i in p.indices) p[i] -= s[i]
}

fun polyLongDiv(n: DoubleArray, d: DoubleArray): Solution {
    if (n.size != d.size) {
        throw IAE("Векторы числителя и знаменателя должны иметь одинаковый размер")
    }
    var nd = polyDegree(n)
    val dd = polyDegree(d)
    if (dd < 0) {
        throw IAE("Делитель должен иметь хотя бы один нулевой коэффициент")
    }
    if (nd < dd) {
        throw IAE("Степень делителя не может быть больше степени числителя.\n")
    }
    val n2 = n.copyOf()
    val q = DoubleArray(n.size)  // all elements zero by default
    while (nd >= dd) {
        val d2 = polyShiftRight(d, nd - dd)
        q[nd - dd] = n2[nd] / d2[nd]
        polyMultiply(d2, q[nd - dd])
        polySubtract(n2, d2)
        nd = polyDegree(n2)
    }
    return Solution(q, n2)
}

fun polyShow(p: DoubleArray) {
    val pd = polyDegree(p)
    for (i in pd downTo 0) {
        val coeff = p[i]
        if (coeff == 0.0) continue
        print (when {
            coeff ==  1.0  -> if (i < pd) " + $coeff" else ""
            coeff == -1.0  -> if (i < pd) " - ${-coeff}" else ""
            coeff <   0.0  -> if (i < pd) " - ${-coeff}" else "$coeff"
            else           -> if (i < pd) " + $coeff" else "$coeff"
        })
        if (i > 1) print("x^$i")
        else if (i == 1) print("x")
    }
    println()
}

fun equationToList(equation: String): DoubleArray {
    val maxdegree = ((Regex("(?<=\\^)\\d+").find(equation, 0))!!.value)
    val ArrayOfZeros = Array<Int>(maxdegree.toInt()+1,{0})

    var test = (Regex("(?<=\\^)\\d+").findAll(equation, 0).map{ it.groupValues[0] }.toList())

    if (Regex("x(?!.*\\^)").containsMatchIn(equation)) test += "1"
    if (Regex("\\+\\d+\$|-\\d+\$").containsMatchIn(equation)) test += "0"

    val test2 = Regex("\\d+(?=\\^)|\\d+(?=x)|\\+\\d+|-\\d+").findAll(equation.replace("(?<!\\d)x".toRegex() ,"1"), 0).map{ it.groupValues[0] }.toList()


    for (i in 0 until  test2.size) {
        ArrayOfZeros[maxdegree.toInt()-test[i].toInt()] = test2[i].toInt()
    }

    return (ArrayOfZeros.reversedArray()).map { it.toDouble() }.toDoubleArray()

}

fun main() {

    val text1 = "x^5+3x^4+x^3+x^2+3x+1"
    val text2 = "x^4+2x^3+x+2"

    val n = equationToList(text1)

    var d = equationToList(text2)

    if (d.size<n.size) for (i in 1..(n.size-d.size)) d += 0.0


    print("Числитель   : ")
    polyShow(n)
    print("Делитель    : ")
    polyShow(d)
    println("-------------------------------------")
    val (q, r) = polyLongDiv(n, d)
    print("Частное     : ")
    polyShow(q)
    print("Остаток     : ")
    polyShow(r)

}