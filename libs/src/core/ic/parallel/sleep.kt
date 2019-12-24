package ic.parallel

import ic.interfaces.condition.Condition
import java.util.*


fun sleep(millis: Long) {
	try {
		java.lang.Thread.sleep(millis)
	} catch (e: InterruptedException) {
	}

}

fun sleepWhile(condition: Condition) {
	while (condition.boolean) sleep(256)
}

fun sleepTill(condition: Condition) {
	sleepWhile(Condition.Not(condition))
}

fun sleepTill(date: Date) {
	val delay = date.time - System.currentTimeMillis()
	if (delay <= 0) return
	sleep(delay)
}