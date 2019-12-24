package d2.polpharma.services.images;


import ic.mimetypes.MimeType;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpRoute;
import ic.network.http.HttpResponse;
import ic.stream.ByteBuffer;
import ic.stream.ByteSequence;
import ic.throwables.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import static ic.Assets.resources;


public class AchievementsImageGenerator extends HttpRoute {

	@Override public HttpResponse implementRoute(SocketAddress socketAddress, HttpRequest request, boolean root) throws NotSupported, UnableToParse {

		if (!request.path.startsWith("achievements_")) throw new NotSupported();

		if (!request.path.endsWith(".png")) throw new NotSupported();

		final String numbers = request.path.substring(
			"achievements_".length(),
			request.path.length() - ".png".length()
		);

		final String templateImageName = "achievements";

		final BufferedImage image; try {
			image = ImageIO.read(resources.getInput(templateImageName + ".png").toInputStream());
		} catch (java.io.IOException e) { throw new IOException.Runtime(e);
		} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

		final Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);

		for (int i = 0; i < 20; i++) {

			final BufferedImage tileImage; try {
				tileImage = ImageIO.read(resources.getInput("achievement_tile_" + i + ".png").toInputStream());
			} catch (java.io.IOException e) { throw new IOException.Runtime(e);
			} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

			if (numbers.charAt(i) == '0') continue;

			graphics.drawImage(
				tileImage,
				1170 + i % 5 * 480,
				100 + i / 5 * 480,
				null
			);

		}

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
