package d2.polpharma.services.images;


import static ic.Assets.resources;
import ic.mimetypes.MimeType;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpRoute;
import ic.network.http.HttpResponse;
import ic.stream.ByteBuffer;
import ic.stream.ByteSequence;
import ic.throwables.IOException;
import ic.throwables.NotExists;
import ic.throwables.NotSupported;
import ic.throwables.UnableToParse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;


public class TestResultImageGenerator extends HttpRoute {

	@Override public HttpResponse implementRoute(SocketAddress socketAddress, HttpRequest request, boolean root) throws NotSupported, UnableToParse {

		if (!request.path.startsWith("test_result_score_")) throw new NotSupported();

		if (!request.path.endsWith(".png")) throw new NotSupported();

		final int score; try {
			score = Integer.parseInt(request.path.substring(
				"test_result_score_".length(),
				request.path.length() - ".png".length()
			));
		} catch (NumberFormatException numberFormatException) { throw new NotSupported(); }

		final String templateImageName; {
			if (score < 30) templateImageName = "test_result_bad"; 		else
			if (score < 70) templateImageName = "test_result_medium";
			else			templateImageName = "test_result_good";
		}

		final BufferedImage image; try {
			image = ImageIO.read(resources.getInput(templateImageName + ".png").toInputStream());
		} catch (java.io.IOException e) { throw new IOException.Runtime(e);
		} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

		final Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setFont(new Font("Roboto", Font.BOLD, 128));
		graphics.setColor(Color.WHITE);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawString(score + "%", 64, 224);

		return new HttpResponse() {
			@Override public MimeType getContentType() { return MimeType.PNG; }
			@Override public ByteSequence getBody() {
				final ByteBuffer byteBuffer = new ByteBuffer();
				try {
					ImageIO.write(image, "png", byteBuffer.getOutputStream());
				} catch (java.io.IOException e) { throw new IOException.Runtime(e); }
				return byteBuffer;
			}
		};

	}

}
