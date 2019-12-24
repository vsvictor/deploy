package ic.network.icotp


import ic.struct.collection.Collection


class IcotpMode private constructor(
	@JvmField internal val byteValue: Byte
) {

	companion object {

		@JvmField val PLAIN 		= IcotpMode(0)
		@JvmField val HANDSHAKE 	= IcotpMode(1)

		private val icotpModes = Collection.Default<IcotpMode>(
			PLAIN,
			HANDSHAKE
		)

		@JvmStatic
		fun byByteValue (byteValue: Byte) = icotpModes.find { it.byteValue == byteValue }

	}

}