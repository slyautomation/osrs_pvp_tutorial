package net.runelite.client.plugins.pvptools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.util.regex.Pattern;
import lombok.Setter;
import net.runelite.client.ui.overlay.RenderableEntity;

@Setter
public class TextComponent implements RenderableEntity
{
	private static final String COL_TAG_REGEX = "(<col=([0-9a-fA-F]){2,6}>)";
	private static final Pattern COL_TAG_PATTERN_W_LOOKAHEAD = Pattern.compile("(?=" + COL_TAG_REGEX + ")");
	private static final Pattern COL_TAG_PATTERN = Pattern.compile(COL_TAG_REGEX);

	private String text;
	private Point position = new Point();
	private Color color = Color.WHITE;
	private Color borderColor = Color.BLACK;

	public static String textWithoutColTags(String text)
	{
		return COL_TAG_PATTERN.matcher(text).replaceAll("");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final FontMetrics fontMetrics = graphics.getFontMetrics();

		if (COL_TAG_PATTERN.matcher(text).find())
		{
			final String[] parts = COL_TAG_PATTERN_W_LOOKAHEAD.split(text);
			int x = position.x;

			for (String textSplitOnCol : parts)
			{
				final String textWithoutCol = textWithoutColTags(textSplitOnCol);
				final String colColor = textSplitOnCol.substring(textSplitOnCol.indexOf('=') + 1, textSplitOnCol.indexOf('>'));

				renderText(graphics, x, position.y, textWithoutCol, Color.decode("#" + colColor), borderColor);

				x += fontMetrics.stringWidth(textWithoutCol);
			}
		}
		else
		{
			renderText(graphics, position.x, position.y, text, color, borderColor);
		}
		return new Dimension(fontMetrics.stringWidth(text), fontMetrics.getHeight());
	}

	private void renderText(Graphics2D graphics, int x, int y, String text, Color color, Color border)
	{
		// remember previous composite
		Composite originalComposite = graphics.getComposite();

		// create a vector of the text
		GlyphVector vector = graphics.getFont().createGlyphVector(graphics.getFontRenderContext(), text);

		// compute the text shape
		Shape stroke = vector.getOutline(x + 1, y + 1);
		Shape shape = vector.getOutline(x, y);

		// draw text border
		graphics.setColor(border);
		graphics.fill(stroke);

		// replace the pixels instead of overlaying
		graphics.setComposite(AlphaComposite.Src);

		// draw actual text
		graphics.setColor(color);
		graphics.fill(shape);

		// reset composite to original
		graphics.setComposite(originalComposite);
	}

}