@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteBuffer
import ic.stream.ByteSequence


internal object StreamSerializableStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, StreamSerializable>() {

	override val type get() = StreamSerializable::class.java

	override fun implementGet (obj: StreamSerializable) : DeclaredClassWithContent<ByteSequence> {
		val content = ByteBuffer()
		obj.serialize(content)
		content.close()
		return DeclaredClassWithContent(
			obj.classToDeclare,
			content
		)
	}

}