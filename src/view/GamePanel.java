package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Deque;
import java.util.Random;
import javax.swing.JPanel;

/**
 * Se muestra mientras se está jugando el juego. Responsable de la apariencia
  * del juego, además de dibujar la serpiente y la manzana. Existen métodos para una grilla
  * diseño y un diseño de puntos.
 *
 * autor Gary Trujillo
 */
public class GamePanel extends JPanel {

    private Deque<Point> snakeBody;
    private final Point apple;
    private final int width, height, scale;
    private int r = 0;
    private int g = 255;
    private int b = 0;
    private final Random random = new Random();
    private Color snakeColor = new Color(0, 255, 0);
    private Graphics2D g2d;

    GamePanel(int width, int height, int scale, Deque<Point> snakeBody, Point apple) {
        this.snakeBody = snakeBody;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.apple = apple;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public Color getBackground() {
        return Color.black;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    /**
     * Garantiza que la GUI esté pintada cuando la ventana se mueve u oculta.
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintDots();
        paintApple();
        paintSnake();

    }

    public void setSnakeBody(Deque<Point> snakeBody, Point apple) {
        this.snakeBody = snakeBody;
    }

    public void paintApple() {
        g2d.setStroke(new BasicStroke(1.5f));
        int xPos = (int) apple.getX();
        int yPos = (int) apple.getY();
        g2d.setColor(Color.green);
        g2d.drawLine(xPos + scale / 2, yPos, xPos + scale / 2, yPos - 1);
        g2d.drawLine(xPos + scale / 2, yPos, xPos + scale - 1, yPos - 2);
        g2d.setColor(Color.red);
        g2d.fillOval(xPos + 2, yPos + 2, scale - 4, scale - 3);
        g2d.fillOval(xPos + 4, yPos + 2, scale - 4, scale - 3);
    }

    public void paintSnake() {
        int xPos, yPos;
        for (Point position : snakeBody) {
            g2d.setColor(snakeColor);
            xPos = (int) position.getX();
            yPos = (int) position.getY();
            g2d.drawRoundRect(xPos + 2, yPos + 2, scale - 4, scale - 4, 2, 2);
        }
        System.out.println(snakeColor);
        r = 0;
        g = 255;
        b = 0;
    }

    public void paintDots() {
        g2d.setStroke(new BasicStroke(0.25f));
        g2d.setColor(Color.gray);
        for (int i = 0; i <= width / scale; i++) {
            for (int j = 0; j <= height / scale; j++) {
                if (i * scale == width) {
                    g2d.fillRect(i * scale - 1, j * scale, 1, 1);
                } else if (j * scale == height) {
                    g2d.fillRect(i * scale, j * scale - 1, 1, 1);
                } else {
                    g2d.fillRect(i * scale, j * scale, 1, 1);
                }
            }
        }
        g2d.fillRect(width - 1, height - 1, 1, 1);

    }

    public void paintGrid() {
        g2d.setStroke(new BasicStroke(0.25f));
        g2d.setColor(Color.gray);
        for (int i = 1; i < width / scale; i++) {
            g2d.drawLine(i * scale, 0, i * scale, height);
        }
        for (int i = 1; i < height / scale; i++) {
            g2d.drawLine(0, i * scale, width, i * scale);

        }
    }

}
