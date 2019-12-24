package d2.polpharma.services.images;


import ic.color.Color;
import ic.mimetypes.MimeType;
import ic.network.SocketAddress;
import ic.network.http.HttpRequest;
import ic.network.http.HttpResponse;
import ic.network.http.HttpRoute;
import ic.stream.ByteBuffer;
import ic.stream.ByteSequence;
import ic.text.Charset;
import ic.throwables.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import static ic.Assets.resources;
import static ic.math.MathKt.*;


public class WeeklyReportImageGenerator extends HttpRoute {


	private static final String[] MONTH_NAMES = new String[] {
		"січні",
		"лютому",
		"березні",
		"квітні",
		"травні",
		"червні",
		"липні",
		"серпні",
		"вересні",
		"жовтні",
		"листопаді",
		"грудні"
	};

	private static final int MIN_Y = 200;
	private static final int MAX_Y = 384;

	private static final int GRAPH_WIDTH = 64;

	private static final int INNER_RADIUS 	= 60;
	private static final int OUTER_RADIUS 	= 66;

	private static final int SHINING_THICKNESS = 64;

	private static final int ROW_HEIGHT = 89;


	private static void drawPixel(BufferedImage image, int x, int y, float alpha) {
		final Color background = Color.fromArgb(image.getRGB(x, y));
		if (alpha < 0) alpha = 0;
		alpha *= alpha;
		alpha *= .25F;
		image.setRGB(
			x, y,
			new Color.Constant(
				background.getRed() 	+ alpha * .75F,
				background.getGreen() 	+ alpha,
				background.getBlue() 	+ alpha
			).toArgb()
		);
	}

	private static void drawGraph(BufferedImage image, Graphics2D graphics, int left, int plan, int fact, int percentage) {
		final int max; {
			if (plan < fact) 	max = fact;
			else 				max = plan;
		}
		{
			final int maxX = left + GRAPH_WIDTH;
			final int minY = MIN_Y + (int) ((MAX_Y - MIN_Y) * (float) (max - plan) / max);
			for (int y = minY - SHINING_THICKNESS; y < MAX_Y + SHINING_THICKNESS; y++) for (int x = left - SHINING_THICKNESS; x < maxX + SHINING_THICKNESS; x++) {
				if (y < minY) {
					if (x < left) {
						int dx = x - left;
						int dy = y - minY;
						drawPixel(
							image, x, y,
							1 - sqrt((float) (dx * dx + dy * dy) / (SHINING_THICKNESS * SHINING_THICKNESS))
						);
					} else
					if (x > maxX) {
						int dx = x - maxX;
						int dy = y - minY;
						drawPixel(
							image, x, y,
							1 - sqrt((float) (dx * dx + dy * dy) / (SHINING_THICKNESS * SHINING_THICKNESS))
						);
					}
					else {
						drawPixel(
							image, x, y,
							1 - (float) (minY - y) / SHINING_THICKNESS
						);
					}
				} else
				if (y > MAX_Y){
					if (x < left) {
						int dx = x - left;
						int dy = y - MAX_Y;
						drawPixel(
							image, x, y,
							1 - sqrt((float) (dx * dx + dy * dy) / (SHINING_THICKNESS * SHINING_THICKNESS))
						);
					} else
					if (x > maxX) {
						int dx = x - maxX;
						int dy = y - MAX_Y;
						drawPixel(
							image, x, y,
							1 - sqrt((float) (dx * dx + dy * dy) / (SHINING_THICKNESS * SHINING_THICKNESS))
						);
					}
					else {
						drawPixel(
							image, x, y,
							1 - (float) (y - MAX_Y) / SHINING_THICKNESS
						);
					}
				}
				else {
					if (x < left) {
						drawPixel(
							image, x, y,
							1 - (float) (left - x) / SHINING_THICKNESS
						);
					} else
					if (x > maxX) {
						drawPixel(
							image, x, y,
							1 - (float) (x - maxX) / SHINING_THICKNESS
						);
					}
					else {
						image.setRGB(x, y, 0xFFFFFFFF);
					}
				}
			}
			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 30));
				graphics.setColor(java.awt.Color.WHITE);
			} {
				graphics.drawString(Integer.toString(plan), left, minY - 8);
			}
		}
		{
			final int factMinY = MIN_Y + (int) ((MAX_Y - MIN_Y) * (float) (max - fact) / max);
			for (int y = factMinY; y < MAX_Y; y++) for (int x = left + 70; x < left + 70 + GRAPH_WIDTH; x++) {
				image.setRGB(x, y, 0xFF83D0FB);
			}
			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 30));
				graphics.setColor(java.awt.Color.WHITE);
			} {
				graphics.drawString(Integer.toString(fact), left + 70, factMinY - 8);
			}
		}
		{
			final int centerX = left + 223;
			final int centerY = 308;
			final int indicatorColor;
			final int percentageNumberColor; {
				if (percentage <= 90) {
					indicatorColor 			= 0xFFD80000;
					percentageNumberColor 	= 0xFF510B0B;
				} else
				if (percentage <= 100) {
					indicatorColor 			= 0xFF7AFEFF;
					percentageNumberColor 	= 0xFF1B5A5E;
				}
				else {
					indicatorColor 			= 0xFF8DFB83;
					percentageNumberColor 	= 0xFF2A7249;
				}
			}
			for (int y = centerY - OUTER_RADIUS; y < centerY + OUTER_RADIUS; y++) for (int x = centerX - OUTER_RADIUS; x < centerX + OUTER_RADIUS; x++) {
				final int dx = x - centerX;
				final int dy = y - centerY;
				if (dx * dx + dy * dy < INNER_RADIUS * INNER_RADIUS) continue;
				if (dx * dx + dy * dy > OUTER_RADIUS * OUTER_RADIUS) continue;
				if (atan(-dy, dx, 0f) > TAU_FLOAT * percentage / 100) continue;
				image.setRGB(x, y, indicatorColor);
			}
			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 40));
				graphics.setColor(new java.awt.Color(percentageNumberColor));
			} {
				int x; {
					if (percentage < 100) 	x = centerX - 32;
					else 					x = centerX - 48;
				}
				graphics.drawString(percentage + "%", x, centerY + 16);
			}
		}
	}

	private void drawTeamGraph(BufferedImage image, Graphics2D graphics, int left, int top, int planned, int actual, int max, int rr) {

		if (max == 0) max = 100;

		final int barMaxWidth = 100;

		{
			final int barLength = barMaxWidth * planned / max;
			for (int y = top + 21; y < top + 40; y++) for (int x = left + 24; x < left + 24 + barLength; x++) {
				image.setRGB(x, y, 0xFFFFFFFF);
			}
			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 24));
				graphics.setColor(new java.awt.Color(0xFFFFFFFF));
			} {
				graphics.drawString(Integer.toString(planned), left + 32 + barLength, top + 39);
			}
		}

		{
			final int barLength = barMaxWidth * actual / max;
			for (int y = top + 51; y < top + 70; y++) for (int x = left + 24; x < left + 24 + barLength; x++) {
				image.setRGB(x, y, 0xFF83D0FB);
			}
			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 24));
				graphics.setColor(new java.awt.Color(0xFFFFFFFF));
			} {
				graphics.drawString(Integer.toString(actual), left + 32 + barLength, top + 69);
			}
		}

		{

			final int rrNumberColor; {
				if (rr <= 90) {
					rrNumberColor = 0xFF510B0B;
				} else
				if (rr <= 100) {
					rrNumberColor = 0xFF1B5A5E;
				}
				else {
					rrNumberColor = 0xFF2A7249;
				}
			}

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 24));
				graphics.setColor(new java.awt.Color(rrNumberColor));
			} {
				graphics.drawString(rr + "%", left + 196, top + 55);
			}

		}

	}

	@Override public HttpResponse implementRoute(SocketAddress socketAddress, HttpRequest request, boolean root) throws NotSupported, UnableToParse {

		if (!request.path.startsWith("report_weekly_")) throw new NotSupported();

		if (!request.path.endsWith(".png")) throw new NotSupported();

		final JSONObject dataJson = new JSONObject(
			Charset.DEFAULT_HTTP.decodeUrl(
				request.path.substring(
					"report_weekly_".length(),
					request.path.length() - ".png".length()
				)
			)
		);

		final String datePeriod = dataJson.getString("date_period");

		final JSONObject qualityJson = dataJson.getJSONObject("quality");
		final JSONObject quantityJson = dataJson.getJSONObject("quantity");

		final int qualityPlanned 	= qualityJson.getInt("planned");
		final int qualityActual 	= qualityJson.getInt("actual");
		final int qualityRr 		= qualityJson.getInt("rr");
		final int quantityPlanned 	= quantityJson.getInt("planned");
		final int quantityActual 	= quantityJson.getInt("actual");
		final int quantityRr 		= quantityJson.getInt("rr");

		final JSONArray teamJson = dataJson.optJSONArray("team");

		if (teamJson == null) {

			final String templateImageName = "report_weekly";

			final BufferedImage image; try {
				image = ImageIO.read(resources.getInput(templateImageName + ".png").toInputStream());
			} catch (java.io.IOException e) { throw new IOException.Runtime(e);
			} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

			final Graphics2D graphics = (Graphics2D) image.getGraphics();

			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 32));
				graphics.setColor(java.awt.Color.WHITE);
			} {
				graphics.drawString("Твоя візитна активність за " + datePeriod, 43, 64);
			}

			drawGraph(
				image, graphics,
				78,
				qualityPlanned,
				qualityActual,
				qualityRr
			);

			drawGraph(
				image, graphics,
				432,
				quantityPlanned,
				quantityActual,
				quantityRr
			);

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

			final BufferedImage backgroundImage;
			final BufferedImage tableHeadImage;
			final BufferedImage evenRowImage;
			final BufferedImage oddRowImage;
			final BufferedImage tableFootImage;
			final BufferedImage footerImage; try {
				backgroundImage = ImageIO.read(resources.getInput("report_weekly_team.png").toInputStream());
				tableHeadImage 	= ImageIO.read(resources.getInput("report_weekly_table_head.png").toInputStream());
				evenRowImage 	= ImageIO.read(resources.getInput("report_weekly_row_even.png").toInputStream());
				oddRowImage 	= ImageIO.read(resources.getInput("report_weekly_row_odd.png").toInputStream());
				tableFootImage 	= ImageIO.read(resources.getInput("report_weekly_table_foot.png").toInputStream());
				footerImage 	= ImageIO.read(resources.getInput("report_weekly_team_footer.png").toInputStream());
			} catch (java.io.IOException e) { throw new IOException.Runtime(e);
			} catch (NotExists notFound) 	{ throw new NotExists.Runtime(notFound); }

			final int imageHeight = 396 + ROW_HEIGHT * teamJson.length();

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
				graphics.setFont(new Font("Roboto", Font.BOLD, 40));
				graphics.setColor(new java.awt.Color(0xFFFFFFFF));
				graphics.drawString("Загальна візитна активність твоєї", 43, 64);
				graphics.drawString("команди за " + datePeriod, 43, 128);
			}

			graphics.drawImage(tableHeadImage, 	54, 193, 	null);

			{

				final int qualityMax, quantityMax; {
					int qualityMaxValue = 0;
					int quantityMaxValue = 0;
					for (int i = 0; i < teamJson.length(); i++) { final JSONObject teamItemJson = teamJson.getJSONObject(i);
						{ final int itemQualityPlanned = teamItemJson.getJSONObject("quality").getInt("planned");
							if (itemQualityPlanned > qualityMaxValue) qualityMaxValue = itemQualityPlanned;
						}
						{ final int itemQualityActual = teamItemJson.getJSONObject("quality").getInt("actual");
							if (itemQualityActual > qualityMaxValue) qualityMaxValue = itemQualityActual;
						}
						{ final int itemQuantityPlanned = teamItemJson.getJSONObject("quantity").getInt("planned");
							if (itemQuantityPlanned > quantityMaxValue) quantityMaxValue = itemQuantityPlanned;
						}
						{ final int itemQuantityActual = teamItemJson.getJSONObject("quantity").getInt("actual");
							if (itemQuantityActual > quantityMaxValue) quantityMaxValue = itemQuantityActual;
						}
					}
					qualityMax = qualityMaxValue;
					quantityMax = quantityMaxValue;
				}

				for (int i = 0; i < teamJson.length(); i++) { final JSONObject teamItemJson = teamJson.getJSONObject(i);

					graphics.drawImage(
						i % 2 == 0 ? evenRowImage : oddRowImage,
						54,
						240 + ROW_HEIGHT * i,
						null
					);

					{
						graphics.setFont(new Font("Roboto", Font.BOLD, 18));
						graphics.setColor(new java.awt.Color(0xFFFFFFFF));
					} {
						graphics.drawString(teamItemJson.getString("surname"), 	64, 	288 + ROW_HEIGHT * i);
						graphics.drawString(teamItemJson.getString("name"), 	205, 	288 + ROW_HEIGHT * i);
					}

					drawTeamGraph(
						image, graphics,
						296,
						240 + ROW_HEIGHT * i,
						teamItemJson.getJSONObject("quality").getInt("planned"),
						teamItemJson.getJSONObject("quality").getInt("actual"),
						qualityMax,
						teamItemJson.getJSONObject("quality").getInt("rr")
					);

					drawTeamGraph(
						image, graphics,
						583,
						240 + ROW_HEIGHT * i,
						teamItemJson.getJSONObject("quantity").getInt("planned"),
						teamItemJson.getJSONObject("quantity").getInt("actual"),
						quantityMax,
						teamItemJson.getJSONObject("quantity").getInt("rr")
					);

				}

			}

			{
				graphics.setFont(new Font("Roboto", Font.BOLD, 14));
				graphics.setColor(new java.awt.Color(0xFFFFFFFF));
			} {
				graphics.drawString("TOTAL", 228, 300 + ROW_HEIGHT * teamJson.length());
			}

			graphics.drawImage(tableFootImage, 296, 240 + ROW_HEIGHT * teamJson.length(), null);

			{

				final int qualityMax, quantityMax; {
					int qualityMaxValue = 0;
					int quantityMaxValue = 0;
					if (qualityPlanned > qualityMaxValue) qualityMaxValue = qualityPlanned;
					if (qualityActual > qualityMaxValue) qualityMaxValue = qualityActual;
					if (quantityPlanned > quantityMaxValue) quantityMaxValue = quantityPlanned;
					if (quantityActual > quantityMaxValue) quantityMaxValue = quantityActual;
					qualityMax = qualityMaxValue;
					quantityMax = quantityMaxValue;
				}

				drawTeamGraph(
					image, graphics,
					296,
					240 + ROW_HEIGHT * teamJson.length(),
					qualityPlanned,
					qualityActual,
					qualityMax,
					qualityRr
				);

				drawTeamGraph(
					image, graphics,
					583,
					240 + ROW_HEIGHT * teamJson.length(),
					quantityPlanned,
					quantityActual,
					quantityMax,
					quantityRr
				);

			}

			graphics.drawImage(footerImage, 321, imageHeight - 56, null);

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
