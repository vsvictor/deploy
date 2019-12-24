package ic.math


fun abs(a: Float): Float {
	return if (a < 0)
		-a
	else
		a
}

fun sqrt(a: Double): Double {
	return java.lang.Math.sqrt(a)
}

fun sqrt(a: Float): Float {
	return sqrt(a.toDouble()).toFloat()
}

@JvmField val PI_DOUBLE = java.lang.Math.PI

@JvmField val PI_FLOAT = PI_DOUBLE.toFloat()

@JvmField val TAU_DOUBLE = PI_DOUBLE * 2

@JvmField val TAU_FLOAT = TAU_DOUBLE.toFloat().toDouble()

fun sin(x: Double): Double {
	return java.lang.Math.sin(x)
}

fun sin(x: Float): Float {
	return sin(x.toDouble()).toFloat()
}

fun cos(x: Double): Double {
	return java.lang.Math.cos(x)
}

fun cos(x: Float): Float {
	return cos(x.toDouble()).toFloat()
}

fun atan(x: Double, y: Double): Double {
	return java.lang.Math.atan2(y, x)
}

fun atan(x: Double, y: Double, startingWith: Double): Double {
	var result = atan(x, y)
	while (result < startingWith) result += TAU_DOUBLE
	while (result > startingWith + TAU_DOUBLE) result -= TAU_DOUBLE
	return result
}

fun atan(x: Float, y: Float): Float {
	return atan(
		x.toDouble(),
		y.toDouble()
	).toFloat()
}

fun atan(x: Float, y: Float, startingWith: Float): Float {
	return atan(
		x.toDouble(),
		y.toDouble(),
		startingWith.toDouble()
	).toFloat()
}

fun getAngle(angle1: Double, angle2: Double): Double {
	var result = angle2 - angle1
	while (result < -PI_DOUBLE) result += TAU_DOUBLE
	while (result > PI_DOUBLE) result -= TAU_DOUBLE
	return result
}

fun getAngle(angle1: Float, angle2: Float): Float {
	return getAngle(
		angle1.toDouble(),
		angle2.toDouble()
	).toFloat()
}