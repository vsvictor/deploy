package ic.serial.stream


import ic.interfaces.getter.Selector1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteOutput
import ic.stream.ByteSequence
import ic.stream.JoinByteSequence
import ic.text.Charset


private val selector = Selector1.Final<DeclaredClassWithContent<ByteSequence>, Any>(
	NullStreamSerializer,
	StringStreamSerializer,
	IntegerStreamSerializer,
	LongStreamSerializer,
	BooleanStreamSerializer,
	ByteStreamSerializer,
	StreamSerializableStreamSerializer,
	TextStreamSerializer,
	ThrowableStreamSerializer,
	ListStreamSerializer,
	PairStreamSerializer
)

fun serializeToByteSequence (inferredClass: Class<*>, obj: Any?) : ByteSequence {
	val declaredClassWithContent = selector(obj)
	assert (inferredClass == declaredClassWithContent.declaredClass)
	return declaredClassWithContent.content
}

fun serializeToStream (output: ByteOutput, inferredClass: Class<*>, obj: Any?) {
	output.write(serializeToByteSequence(inferredClass, obj))
}

fun serializeToByteSequence (obj: Any?) : ByteSequence {
	val declaredClassWithContent = selector(obj)
	val declaredClass = declaredClassWithContent.declaredClass
	return JoinByteSequence(
		Charset.UTF_8.stringToByteSequence(
			if (declaredClass == null) {
				"null"
			} else {
				declaredClass.name
			} + ' '
		),
		declaredClassWithContent.content
	)
}

fun serializeToStream (output: ByteOutput, obj: Any?) {
	output.write(serializeToByteSequence(obj))
}