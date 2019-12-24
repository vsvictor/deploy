@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.stream.JoinByteSequence


internal object PairStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Pair<*, *>>() {

	override val type get() = Pair::class.java

	override fun implementGet (obj: Pair<*, *>) : DeclaredClassWithContent<ByteSequence> {
		synchronized (obj) {
			return DeclaredClassWithContent(
				Pair::class.java,
				JoinByteSequence(
					serializeToByteSequence(obj.first),
					serializeToByteSequence(obj.second)
				)
			)
		}
	}

}