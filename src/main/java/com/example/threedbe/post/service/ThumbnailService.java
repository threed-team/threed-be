package com.example.threedbe.post.service;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

	private static final int THUMBNAIL_WIDTH = 1200;
	private static final int THUMBNAIL_HEIGHT = 630;
	private static final Color BACKGROUND_COLOR = new Color(97, 76, 246);
	private static final Color TEXT_COLOR = Color.WHITE;
	private static final Font TITLE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 64);

	public BufferedImage createThumbnailImage(String title) {
		BufferedImage image = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		drawBackground(graphics);
		drawTitle(graphics, title);

		graphics.dispose();
		return image;
	}

	private void drawBackground(Graphics2D graphics) {
		graphics.setColor(BACKGROUND_COLOR);
		graphics.fillRect(0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
	}

	private void drawTitle(Graphics2D graphics, String title) {
		graphics.setColor(TEXT_COLOR);
		graphics.setFont(TITLE_FONT);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		FontMetrics fontMetrics = graphics.getFontMetrics();
		String displayTitle = getDisplayTitle(title);
		int titleWidth = fontMetrics.stringWidth(displayTitle);
		int titleHeight = fontMetrics.getHeight();

		int x = (ThumbnailService.THUMBNAIL_WIDTH - titleWidth) / 2;
		int y = (ThumbnailService.THUMBNAIL_HEIGHT - titleHeight) / 2 + fontMetrics.getAscent();

		graphics.drawString(displayTitle, x, y);
	}

	private String getDisplayTitle(String title) {
		if (title == null || title.trim().isEmpty()) {
			return "Untitled";
		}

		if (title.length() > 20) {
			return title.substring(0, 17) + "...";
		}

		return title;
	}

}
