package ic.serial.stream


import ic.interfaces.getter.SafeGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED


internal object NullStreamSerializer : SafeGetter1<DeclaredClassWithContent<ByteSequence>, Any?, NotSupported> {

	override fun get (obj: Any?) : DeclaredClassWithContent<ByteSequence> {
		if (obj != null && obj != Unit) throw NOT_SUPPORTED
		return DeclaredClassWithContent(
			null,
			ByteSequence.EMPTY
		)
	}

}