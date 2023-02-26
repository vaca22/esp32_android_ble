package com.example.smart_xe_gimble.xe

object Ascii {
    /* renamed from: A */
    const val f30327A: Byte = 23

    /* renamed from: B */
    const val f30328B: Byte = 24

    /* renamed from: C */
    const val f30329C: Byte = 25

    /* renamed from: D */
    const val f30330D: Byte = 26

    /* renamed from: E */
    const val f30331E: Byte = 27

    /* renamed from: F */
    const val f30332F: Byte = 28

    /* renamed from: G */
    const val f30333G: Byte = 29

    /* renamed from: H */
    const val f30334H: Byte = 30

    /* renamed from: I */
    const val f30335I: Byte = 31

    /* renamed from: J */
    const val f30336J: Byte = 32

    /* renamed from: K */
    const val f30337K: Byte = 32

    /* renamed from: L */
    const val f30338L = Byte.MAX_VALUE

    /* renamed from: M */
    const val f30339M = 0.toChar()

    /* renamed from: N */
    const val f30340N = 127.toChar()

    /* renamed from: O */
    private const val f30341O = ' '

    /* renamed from: a */
    const val f30342a: Byte = 0

    /* renamed from: b */
    const val f30343b: Byte = 1

    /* renamed from: c */
    const val f30344c: Byte = 2

    /* renamed from: d */
    const val f30345d: Byte = 3

    /* renamed from: e */
    const val f30346e: Byte = 4

    /* renamed from: f */
    const val f30347f: Byte = 5

    /* renamed from: g */
    const val f30348g: Byte = 6

    /* renamed from: h */
    const val f30349h: Byte = 7

    /* renamed from: i */
    const val f30350i: Byte = 8

    /* renamed from: j */
    const val f30351j: Byte = 9

    /* renamed from: k */
    const val f30352k: Byte = 10

    /* renamed from: l */
    const val f30353l: Byte = 10

    /* renamed from: m */
    const val f30354m: Byte = 11

    /* renamed from: n */
    const val f30355n: Byte = 12

    /* renamed from: o */
    const val f30356o: Byte = 13

    /* renamed from: p */
    const val f30357p: Byte = 14

    /* renamed from: q */
    const val f30358q: Byte = 15

    /* renamed from: r */
    const val f30359r: Byte = 16

    /* renamed from: s */
    const val f30360s: Byte = 17

    /* renamed from: t */
    const val f30361t: Byte = 17

    /* renamed from: u */
    const val f30362u: Byte = 18

    /* renamed from: v */
    const val f30363v: Byte = 19

    /* renamed from: w */
    const val f30364w: Byte = 19

    /* renamed from: x */
    const val f30365x: Byte = 20

    /* renamed from: y */
    const val f30366y: Byte = 21

    /* renamed from: z */
    const val f30367z: Byte = 22

    private fun Ascii() {}

    /* renamed from: a */
    fun m26264a(charSequence: CharSequence, charSequence2: CharSequence): Boolean {
        var m26263b: Int
        val length = charSequence.length
        if (charSequence === charSequence2) {
            return true
        }
        if (length != charSequence2.length) {
            return false
        }
        for (i in 0 until length) {
            val charAt = charSequence[i]
            val charAt2 = charSequence2[i]
            if (charAt != charAt2 && (m26263b(charAt).also {
                    m26263b = it
                } >= 26 || m26263b != m26263b(charAt2))) {
                return false
            }
        }
        return true
    }

    /* renamed from: b */
    private fun m26263b(c: Char): Int {
        return ((c.code or f30341O.code) - 97).toChar().toInt()
    }

    /* renamed from: c */
    fun m26262c(c: Char): Boolean {
        return c >= 'a' && c <= 'z'
    }

    /* renamed from: d */
    fun m26261d(c: Char): Boolean {
        return c >= 'A' && c <= 'Z'
    }

    /* renamed from: e */
    fun m26260e(c: Char): Char {
        return if (m26261d(c)) (c.code xor f30341O.code).toChar() else c
    }

    /* renamed from: f */
    fun m26259f(charSequence: CharSequence): String? {
        if (charSequence is String) {
            return m26258g(charSequence)
        }
        val length = charSequence.length
        val cArr = CharArray(length)
        for (i in 0 until length) {
            cArr[i] = m26260e(charSequence[i])
        }
        return String(cArr)
    }

    /* renamed from: g */
    fun m26258g(str: String): String? {
        val length = str.length
        var i = 0
        while (i < length) {
            if (m26261d(str[i])) {
                val charArray = str.toCharArray()
                while (i < length) {
                    val c = charArray[i]
                    if (m26261d(c)) {
                        charArray[i] = (c.code xor f30341O.code).toChar()
                    }
                    i++
                }
                return String(charArray)
            }
            i++
        }
        return str
    }

    /* renamed from: h */
    fun m26257h(c: Char): Char {
        return if (m26262c(c)) (c.code xor f30341O.code).toChar() else c
    }

    /* renamed from: i */
    fun m26256i(charSequence: CharSequence): String? {
        if (charSequence is String) {
            return m26255j(charSequence)
        }
        val length = charSequence.length
        val cArr = CharArray(length)
        for (i in 0 until length) {
            cArr[i] = m26257h(charSequence[i])
        }
        return String(cArr)
    }

    /* renamed from: j */
    fun m26255j(str: String): String? {
        val length = str.length
        var i = 0
        while (i < length) {
            if (m26262c(str[i])) {
                val charArray = str.toCharArray()
                while (i < length) {
                    val c = charArray[i]
                    if (m26262c(c)) {
                        charArray[i] = (c.code xor f30341O.code).toChar()
                    }
                    i++
                }
                return String(charArray)
            }
            i++
        }
        return str
    }

    /* renamed from: k */
    fun m26254k(charSequence: CharSequence, i: Int, str: String): String? {
        val length = i - str.length

        val length2 = charSequence.length
        var str2: String? = charSequence.toString()
        if (length2 <= i) {
            val charSequence2 = charSequence.toString()
            val length3 = charSequence2.length
            str2 = charSequence2
            if (length3 <= i) {
                return charSequence2
            }
        }
        val sb = StringBuilder(i)
        sb.append(str2 as CharSequence?, 0, length)
        sb.append(str)
        return sb.toString()
    }

}