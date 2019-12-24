package ic.throwables


import ic.stream.ByteBuffer
import ic.text.Charset
import ic.text.Text
import java.io.PrintStream


val Throwable.stackTraceAsText : Text get() {
	val byteBuffer = ByteBuffer()
	printStackTrace(PrintStream(byteBuffer.outputStream))
	return Charset.UTF_8.bytesToText(byteBuffer)
}