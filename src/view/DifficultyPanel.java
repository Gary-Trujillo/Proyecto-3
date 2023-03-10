package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import javax.swing.JPanel;

/**
 * Muestra las dificultades disponibles antes de comenzar el juego.
 *
 * autor Gary Trujillo
 */
public class DifficultyPanel extends JPanel {

    private final int width, height, scale;
    Graphics2D g2d;

    DifficultyPanel(int width, int height, int scale) {
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public Color getBackground() {
        return Color.black;
    }

    /**
     * Garantiza que la GUI esté pintada cuando la ventana se mueve u oculta.
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintTitle();
        paintDifficulties();
    }

    public void paintTitle() {
        g2d.setColor(Color.green);
        Font font = new Font("Monospaced", Font.PLAIN, width / 10);
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, "Difficulty");
        g2d.drawGlyphVector(gv,
                width / 2 - ((int) gv.getVisualBounds().getWidth() / 2),
                height * 2 / 5 - ((int) gv.getVisualBounds().getHeight() / 2));
    }

    public void paintDifficulties() {
        g2d.setColor(Color.red);
        Font font = new Font("Monospaced", Font.PLAIN, width / 25);
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, "1 - N00b");
        g2d.drawGlyphVector(gv,
                width / 2 - ((int) gv.getVisualBounds().getWidth() / 2),
                height * 5 / 10 - ((int) gv.getVisualBounds().getHeight() / 2));
        gv = font.createGlyphVector(frc, "2 - quick");
        g2d.drawGlyphVector(gv,
                width / 2 - ((int) gv.getVisualBounds().getWidth() / 2),
                height * 6 / 10 - ((int) gv.getVisualBounds().getHeight() / 2));
        gv = font.createGlyphVector(frc, "3 - Crazy");
        g2d.drawGlyphVector(gv,
                width / 2 - ((int) gv.getVisualBounds().getWidth() / 2),
                height * 7 / 10 - ((int) gv.getVisualBounds().getHeight() / 2));
    }

}
