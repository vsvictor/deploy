package ic.pdf


import com.itextpdf.text.Document
import ic.stream.ByteSequence
import ic.text.Text
import java.io.StringReader
import com.itextpdf.text.html.simpleparser.HTMLWorker
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.PageSize
import ic.stream.ByteBuffer


fun htmlToPdf (html: Text) : ByteSequence {

	val doc = Document(PageSize.A4)
	val byteBuffer = ByteBuffer()
	PdfWriter.getInstance(doc, byteBuffer.outputStream)
	doc.open()
	val hw = HTMLWorker(doc)
	hw.parse(StringReader(html.stringValue))
	doc.close()
	return byteBuffer

}