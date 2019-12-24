@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package ic.serial.stream


import ic.annotations.Overload
import ic.interfaces.getter.Selector4
import ic.stream.ByteInput
import ic.struct.list.List
import ic.text.Charset
import ic.throwables.UnableToParse


private val selector = object : Selector4<Any?, ByteInput, Class<*>?, List<Class<*>>, List<Any?>>() {

	override val selectGetters = List<(ByteInput, Class<*>?, List<Class<*>>, List<Any?>) -> Any?>(
		NullStreamParser,
		StringStreamParser,
		TextStreamParser,
		IntegerStreamParser,
		LongStreamParser,
		BooleanStreamParser,
		ByteStreamParser,
		StreamSerializableStreamParser,
		ThrowableStreamParser,
		ListStreamParser,
		PairStreamParser
	)

	override fun generateNotSupportedMessage(
		input: ByteInput, declaredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>
	) = "declaredClass: ${ declaredClass!!.name }"

}

@Throws(UnableToParse::class)
fun <Value> parseFromStream (input: ByteInput, argClasses: List<Class<*>>, args: List<Any?>) : Value {
	assert (argClasses.length == args.length)
	try {
		val declaredClass = run {
			val declaredClassName = Charset.UTF_8.bytesToString(input.safeReadTill(' '.toByte()))
			if (declaredClassName == "null") {
				null
			} else {
				Class.forName(declaredClassName)
			}
		}
		@Suppress("UNCHECKED_CAST")
		return selector(input, declaredClass, argClasses, args) as Value
	} catch (throwable : Throwable) { throw UnableToParse(throwable) }
}

@Throws(UnableToParse::class)
@Overload fun <Value> parseFromStream (input: ByteInput) = parseFromStream<Value>(input, List.Default(), List.Default())

@Throws(UnableToParse::class)
@Overload fun <Value> parseFromStream (
	input: ByteInput, inferredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>
) : Value {
	assert (argClasses.length == args.length)
	try {
		@Suppress("UNCHECKED_CAST")
		return selector(input, inferredClass, argClasses, args) as Value
	} catch (throwable : Throwable) { throw UnableToParse(throwable) }
}

@Throws(UnableToParse::class)
@Overload fun <Value> parseFromStream (input: ByteInput, inferredClass: Class<*>?)
	= parseFromStream<Value>(input, inferredClass, List(), List())
;