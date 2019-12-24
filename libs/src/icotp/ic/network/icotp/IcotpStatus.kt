package ic.network.icotp

import ic.struct.collection.Collection


class IcotpStatus private constructor(
	@JvmField internal val byteValue: Byte
) {

	companion object {

		@JvmField val SUCCESS 	= IcotpStatus(0)
		@JvmField val ERROR 	= IcotpStatus(1)

		private val icotpStatuses = Collection.Default<IcotpStatus>(
			SUCCESS,
			ERROR
		)

		@JvmStatic
		fun byByteValue (byteValue: Byte) = icotpStatuses.find { it.byteValue == byteValue }

	}

}