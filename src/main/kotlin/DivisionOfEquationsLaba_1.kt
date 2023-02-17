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
        throw IAE("The number of places to be shifted is too large")
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
        throw IAE("Numerator and denominator vectors must have the same size")
    }
    var nd = polyDegree(n)
    val dd = polyDegree(d)
    if (dd < 0) {
        throw IAE("Divisor must have at least one one-zero coefficient")
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
            coeff ==  1.0  -> if (i < pd) " + $coeff " else ""
            coeff == -1.0  -> if (i < pd) " - " else "-"
            coeff <   0.0  -> if (i < pd) " - ${-coeff}" else "$coeff"
            else           -> if (i < pd) " + $coeff" else "$coeff"
        })
        if (i > 1) print("x^$i")
        else if (i == 1) print("x")
    }
    println()
}

fun main() {
    var text1 = readLine()?.replace("(?<!\\d)x".toRegex() ,"1")
    text1 = text1?.replace("x", "")
    val ar1 = text1?.split("\\^\\d?|(?<!\\^\\d)\\+".toRegex())?.reversed()?.toTypedArray()
    val n = ar1?.map { it.toDouble() }!!.toDoubleArray()

    var text2 = readLine()?.replace("(?<!\\d)x".toRegex() ,"1")
    text2 = text2?.replace("x", "")
    val ar2 = text2?.split("\\^\\d?|(?<!\\^\\d)\\+".toRegex())?.reversed()?.toTypedArray()
    var d = ar2?.map { it.toDouble() }!!.toDoubleArray()

    if (d.size<n.size) for (i in 1..(n.size-d.size)) d += 0.0

    println(d.joinToString())
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