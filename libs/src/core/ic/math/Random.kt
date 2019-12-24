package ic.math


class Random {


	private val random: java.util.Random


	val int: Int
		get() = random.nextInt()

	val long: Long
		get() = random.nextLong()


	fun getInt(limit: Int): Int {
		return random.nextInt(limit)
	}

	fun getLong(from: Long, to: Long): Long { // TODO: Reimplement
		return from + random.nextInt((to - from).toInt())
	}

	fun getFloat(before: Float): Float {
		return random.nextFloat() * before
	}

	fun getBoolean(probability: Float): Boolean {
		return random.nextFloat() < probability
	}


	constructor(seed: Long) {
		random = java.util.Random(seed)
	}

	private constructor() {
		random = java.util.Random()
	}

	companion object {
		val INSTANCE = Random()
	}


}
