package ic


import ic.text.SplitString
import ic.util.reflect.getClassByName


internal fun getMainClass() : Class<out App> = getClassByName(
	SplitString(
		System.getProperty("sun.java.command"),
		' '
	).first
)