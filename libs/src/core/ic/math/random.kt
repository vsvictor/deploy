package ic.math


fun randomInt (limit: Int) = Random.INSTANCE.getInt(limit)

fun randomLong() = Random.INSTANCE.long

val randomLong get() = randomLong()

