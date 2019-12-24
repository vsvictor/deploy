package d2.polpharma.services.images;


import ic.mimetypes.MimeType;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.HttpRoute;
import ic.stream.ByteBuffer;
import ic.stream.ByteSequence;
import ic.text.Charset;
import ic.throwables.IOException;
import ic.throwables.NotExists;
import ic.throwables.NotSupported;
import ic.throwables.UnableToParse;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import static ic.Assets.resources;
import static ic.math.MathKt.TAU_FLOAT;
import static ic.math.MathKt.atan;


public class MonthlyReportImageGenerator extends HttpRoute {

	private static final String[] MONTH_NAMES = new String[] {
		"січень",
		"лютий",
		"березень",
		"квітень",
		"травень",
		"червень",
		"липень",
		"серпень",
		"вересень",
		"жовтень",
		"листопад",
		"грудень"
	};

	private static final int CENTER_X 		= 190;
	private static final int CENTER_Y 		= 261;
	private static final int OUTER_RADIUS 	= 134;
	private static final int INNER_RADIUS 	= 127;

	private static final int ROW_HEIGHT = 42;

	@Override public HttpResponse implementRoute(SocketAddress socketAddress, HttpRequest request, boolean root) throws NotSupported, UnableToParse {

		if (!request.path.startsWith("report_monthly_")) throw new NotSupported();

		if (!request.path.endsWith(".png")) throw new NotSupported();

		final JSONObject dataJson = new JSONObject(
			Charset.DEFAULT_HTTP.decodeUrl(
				request.path.substring(
					"report_monthly_".length(),
					request.path.length() - ".png".length()
				)
			)
		);

		final int month = dataJson.getInt("month");

		final int planned 		= dataJson.getInt("planned");
		final int actual 		= dataJson.getInt("actual");
		final int percentage 	= dataJson.getInt("percentage");

		final JSONArray teamJson = dataJson.optJSONArray("team");

		if (teamJson == null) {

			final String templateImageName;
			final int indicatorColor;
			final int percentageNumberColor; {
				if (percentage <= 90) {
					templateImageName 		= "report_monthly_bad";
					indicatorColor 			= 0xFFD80000;
					percentageNumberColor 	= 0xFF510B0B;
				} else
				if (percentage <= 100) {
					templateImageName 		= "report_monthly_medium";
					indicatorColor 			= 0xFF7AFEFF;
					percentageNumberColor 	= 0xFF1B5A5E;
				}
				else {
					templateImageName 		= "report_monthly_good";
					indicatorColor 			= 0xFF8DFB83;
					percentageNumberColor 	= 0xFF2A7249;
				}
			}

			final BufferedImage image; try {
				image = ImageIO.read(resources.getInput(templateImageName + ".png").toInputStream());
			} catch (java.io.IOException e) { throw new IOException.Runtime(e);
			} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

			final Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 48));
				graphics.setColor(Color.WHITE);
			} {
				graphics.drawString("Твій звіт за " + MONTH_NAMES[month], 64, 64);
			}

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 48));
				graphics.setColor(Color.WHITE);
			} {
				graphics.drawString(planned + " $", 360, 232);
				graphics.drawString(actual + " $", 360, 360);
			}

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 84));
				graphics.setColor(new Color(percentageNumberColor));
				final int x; {
					if (percentage < 10) 	x = 134; else
					if (percentage < 100)	x = 116;
					else 					x = 90;
				}
				graphics.drawString(percentage + "%", x, 290);
			}

			for (int y = CENTER_Y - OUTER_RADIUS; y < CENTER_Y + OUTER_RADIUS; y++) for (int x = CENTER_X - OUTER_RADIUS; x < CENTER_X + OUTER_RADIUS; x++) {

				final int dx = x - CENTER_X;
				final int dy = y - CENTER_Y;

				if (dx * dx + dy * dy < INNER_RADIUS * INNER_RADIUS) continue;
				if (dx * dx + dy * dy > OUTER_RADIUS * OUTER_RADIUS) continue;

				if (atan(-dy, dx, 0f) > TAU_FLOAT * percentage / 100) continue;

				image.setRGB(x, y, indicatorColor);

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

		} else {

			final String templateImageName;
			final int indicatorColor; {
				if (percentage <= 90) {
					templateImageName = "report_monthly_team_bad";
					indicatorColor = 0xFFCD3438;
				} else
				if (percentage <= 100) {
					templateImageName = "report_monthly_team_medium";
					indicatorColor = 0xFF25C4C3;
				}
				else {
					templateImageName = "report_monthly_team_good";
					indicatorColor = 0xFF3A9B66;
				}
			}

			final BufferedImage backgroundImage;
			final BufferedImage tableHeadImage;
			final BufferedImage oddRowImage;
			final BufferedImage signatureImage; try {
				backgroundImage 	= ImageIO.read(resources.getInput(templateImageName + ".png").toInputStream());
				tableHeadImage 		= ImageIO.read(resources.getInput("report_monthly_table_head.png").toInputStream());
				oddRowImage 		= ImageIO.read(resources.getInput("report_monthly_row_odd.png").toInputStream());
				signatureImage 		= ImageIO.read(resources.getInput("report_signature.png").toInputStream());
			} catch (java.io.IOException e) { throw new IOException.Runtime(e);
			} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

			final int imageHeight; {
				int value = 220 + ROW_HEIGHT * teamJson.length();
				if (value < 480) value = 480;
				imageHeight = value;
			}

			final BufferedImage image = new BufferedImage(backgroundImage.getWidth(), imageHeight, BufferedImage.TYPE_INT_ARGB);

			final Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			graphics.drawImage(
				backgroundImage,
				0,
				(imageHeight - backgroundImage.getHeight()) / 2,
				null
			);

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 48));
				graphics.setColor(Color.WHITE);
			} {
				graphics.drawString("Звіт твоєї команди за " + MONTH_NAMES[month], 64, 72);
			}

			graphics.drawImage(tableHeadImage, 	54, 107, 	null);

			for (int i = 0; i < teamJson.length(); i++) { final JSONObject teamItemJson = teamJson.getJSONObject(i);

				if (i % 2 == 1) {
					graphics.drawImage(
						oddRowImage,
						54,
						154 + ROW_HEIGHT * i,
						null
					);
				}

				{
					graphics.setFont(new Font("Roboto", Font.BOLD, 24));
					graphics.setColor(new Color(0xFFFFFFFF));
				} {
					graphics.drawString(teamItemJson.getString("surname"), 			64, 	184 + ROW_HEIGHT * i);
					graphics.drawString(teamItemJson.getString("name"), 			234, 	184 + ROW_HEIGHT * i);
					graphics.drawString(teamItemJson.getInt("percentage") + "%", 	407, 	184 + ROW_HEIGHT * i);
				}

			}

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 16));
				graphics.setColor(new Color(0xFFFFFFFF));
			} {
				graphics.drawString("TOTAL", 286, 182 + ROW_HEIGHT * teamJson.length());
			}

			for (int y = 154 + ROW_HEIGHT * teamJson.length(); y < 154 + ROW_HEIGHT * teamJson.length() + ROW_HEIGHT; y++) for (int x = 370; x < 503; x++) {
				image.setRGB(x, y, indicatorColor);
			}

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 24));
				graphics.setColor(new Color(0xFFFFFFFF));
			} {
				graphics.drawString(percentage + "%", 406, 184 + ROW_HEIGHT * teamJson.length());
			}

			graphics.drawImage(signatureImage, image.getWidth() - 128, imageHeight - 48, null);

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

}
