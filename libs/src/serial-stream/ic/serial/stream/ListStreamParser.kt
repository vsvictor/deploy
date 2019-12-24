package ic.serial.stream


import ic.interfaces.getter.SafeGetter4
import ic.stream.ByteInput
import ic.struct.list.List
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.util.Arrays.createArray
import ic.util.Bytes.bytesToInt


object ListStreamParser : SafeGetter4<Any?, ByteInput, Class<*>?, List<Class<*>>, List<Any?>, NotSupported> {

	override fun get (input: ByteInput, declaredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>): Any? {
		if (declaredClass != List::class.java) throw NOT_SUPPORTED
		val length = bytesToInt(
			input.byte,
			input.byte,
			input.byte,
			input.byte
		)
		val array = createArray<Any?>(length)
		for (i in 0 until array.size) {
			array[i] = parseFromStream(input)
		}
		return List.FromArray(array)
	}

}