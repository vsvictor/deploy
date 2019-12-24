package ic.stream


import ic.interfaces.closeable.Closeable
import ic.interfaces.putter.Putter
import ic.interfaces.writer.Writer
import ic.throwables.End
import ic.throwables.IOException

import java.io.OutputStream

import ic.util.Unsigned.byteToInt
import ic.util.Unsigned.intToByte


interface ByteOutput : Putter<Byte>, Closeable, Writer<ByteSequence> {


	override fun put(b: Byte)

	@JvmDefault
	override fun put (value: Byte?) {
		put(value as Byte)
	}

	@JvmDefault
	override fun write(byteSequence: ByteSequence) {
		try {
			val input = byteSequence.iterator
			while (true) {
				put(input.byte)
			}
		} catch (end: End) {
		}

	}

	@JvmDefault
	fun write(byteArray: ByteArray) {
		for (b in byteArray) put(b)
	}

	@JvmDefault
	val outputStream : OutputStream get() {
		return object : OutputStream() {
			override fun write(i: Int) {
				put(intToByte(i))
			}

			override fun write(byteArray: ByteArray) {
				this@ByteOutput.write(byteArray)
			}

			override fun close() {
				this@ByteOutput.close()
			}
		}
	}


	open class FromOutputStream (override val outputStream: OutputStream) : ByteOutput {

		override fun put(b: Byte) {
			try {
				outputStream.write(byteToInt(b))
			} catch (e: java.io.IOException) {
				throw IOException.Runtime(e)
			}

		}

		override fun write(byteArray: ByteArray) {
			try {
				outputStream.write(byteArray)
			} catch (e: java.io.IOException) {
				throw IOException.Runtime(e)
			}

		}

		override fun write(byteSequence: ByteSequence) {
			write(byteSequence.toByteArray())
		}

		override fun close() {
			try {
				outputStream.close()
			} catch (e: java.io.IOException) {
				throw IOException.Runtime(e)
			}

		}

	}


}
