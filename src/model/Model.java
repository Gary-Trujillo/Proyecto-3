package model;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import util.Direction;
import view.View;

/**
 * Clase de modelo principal como parte del patrón de diseño MVC.
 *
 * autor Gary Trujillo
 */
public final class Model {

    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int SCALE = 15;
    private final int GROWTH_SPURT = 8;

    private final int MAX_INDEX_X = WIDTH / SCALE;
    private final int MAX_INDEX_Y = HEIGHT / SCALE;

    private int squaresToGrow;
    private int applesEaten = 0;
    private Direction direction = Direction.UP;
    private int difficulty;

    private final View view;
    private final Point apple = new Point();
    private final Random random = new Random();
    private final Deque<Point> snakeBody = new ArrayDeque<>();
    private final Set<Point> occupiedPositions = new LinkedHashSet();
    private Clip gameOverSound, eatAppleSound, gameMusicSound;

    private final String[] difficulties = {"n00b", "quick", "crazy"};

    private final String HIGH_SCORE_N00B = "High Score " + difficulties[0] + ": ";
    private final String HIGH_SCORE_SPEED = "High Score " + difficulties[1] + ": ";
    private final String HIGH_SCORE_CRAZY = "High Score " + difficulties[2] + ": ";
    private final String GAMES_PLAYED = "Games Played: ";
    private final String APPLES_EATEN = "Apples Eaten: ";

    private final String[] dataID = {HIGH_SCORE_N00B, HIGH_SCORE_SPEED,
                                     HIGH_SCORE_CRAZY, GAMES_PLAYED, APPLES_EATEN};

    private final int[] data = new int[dataID.length];

    private final int TOTAL_GAMES_PLAYED_LOC = 3;
    private final int TOTAL_APPLES_EATEN_LOC = 4;

    public Model() {
        loadData();
        occupiedPositions.add(apple);
        view = new View(WIDTH, HEIGHT, SCALE, snakeBody, apple);
        initSounds();
    }

    /**
     * Genera una serpiente en el centro de la cuadrícula que se mueve hacia arriba. Considerar
      * inicializando la posición y dirección de la serpiente al azar.
     */
    private void generateSnakeAtCenter() {
        int x = WIDTH / 2;
        int y = HEIGHT / 2;
        snakeBody.add(new Point(x, y));
        occupiedPositions.add(snakeBody.getFirst());
        squaresToGrow += GROWTH_SPURT;
    }

    /**
     * Genera una posición de manzana al azar y evita colisiones.
      *
      * Originalmente intenté usar HashSet.contains() para verificar colisiones, pero
      * no estaba atrapando colisiones.
     */
    private void generateApple() {

        int x = 0;
        int y = 0;
        boolean spaceIsOccupied;
        do {
            spaceIsOccupied = false;
            x = random.nextInt((int) MAX_INDEX_X) * SCALE;
            y = random.nextInt((int) MAX_INDEX_Y) * SCALE;
            for (Point point : occupiedPositions) {
                if (point.getX() == x && point.getY() == y) {
                    System.out.println("wtf");
                    spaceIsOccupied = true;
                }
            }
        } while (spaceIsOccupied);
        apple.setLocation(x, y);
    }

    /**
     * Mueve la serpiente moviendo la posición de la cola un cuadrado de cuadrícula delante de la cabeza
      * en la dirección actual. La cola traducida se quita de la cola antes
      * estar en cola como el nuevo jefe. Si se come una manzana, la serpiente se extiende
      * al no desencolar la cola.
     */
    public void moveSnake() {

        int nextHeadX = (int) snakeBody.getFirst().getX();
        int nextHeadY = (int) snakeBody.getFirst().getY();

        switch (direction) {
            case UP:
                nextHeadY -= SCALE;
                break;
            case DOWN:
                nextHeadY += SCALE;
                break;
            case LEFT:
                nextHeadX -= SCALE;
                break;
            case RIGHT:
                nextHeadX += SCALE;
                break;
            default:
                break;
        }

        if (collided(nextHeadX, nextHeadY)) {
            stopMusic();
            playGameOverSound();
            data[difficulty] = Math.max(applesEaten, data[difficulty]);
            view.update(difficulties[difficulty], applesEaten, data[difficulty]);
            direction = Direction.UP; // consider making this random
            view.gameOver();
        }

        snakeBody.getLast().setLocation(nextHeadX, nextHeadY);
        if (ateApple()) {
            playEatAppleSound();
            snakeBody.addFirst(new Point(nextHeadX, nextHeadY));
            occupiedPositions.add(snakeBody.getFirst());
            generateApple();
            applesEaten++;
            data[TOTAL_APPLES_EATEN_LOC]++;
            squaresToGrow += GROWTH_SPURT - 1;
        } else if (squaresToGrow > 0) {
            snakeBody.addFirst(new Point(nextHeadX, nextHeadY));
            occupiedPositions.add(snakeBody.getFirst());
            squaresToGrow--;
        } else {
            snakeBody.addFirst(snakeBody.removeLast());
        }

        view.updateView(snakeBody, apple, difficulties[difficulty], data[difficulty], applesEaten);

    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    private boolean ateApple() {
        return snakeBody.getFirst().equals(apple);
    }

    /**
     * Comprueba la colisión de la serpiente con uno mismo y los bordes
     */
    private boolean collided(int nextHeadX, int nextHeadY) {
        boolean snakeCollision = snakeBody.contains(new Point(nextHeadX, nextHeadY));
        boolean leftEdgeCollision = nextHeadX < 0;
        boolean rightEdgeCollision = nextHeadX >= WIDTH;
        boolean topEdgeCollision = nextHeadY < 0;
        boolean bottomEdgeCollision = nextHeadY >= HEIGHT;
        return snakeCollision
                || leftEdgeCollision
                || rightEdgeCollision
                || topEdgeCollision
                || bottomEdgeCollision;
    }

    public void newGame() {
        view.newGame();
    }

    public void continueGame() {
        clearModel();
        generateSnakeAtCenter();
        generateApple();
        playMusic();
        view.updateView(snakeBody, apple, difficulties[difficulty], data[difficulty], applesEaten);
        view.continueGame();
        data[TOTAL_GAMES_PLAYED_LOC]++;
        saveData();
    }

    public void chooseDifficulty() {
        view.chooseDifficulty();
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void clearModel() {
        occupiedPositions.clear();
        snakeBody.clear();
        apple.setLocation(0, 0);
        applesEaten = 0;
    }

    private void initSounds() {
        try {
            URL url = this.getClass().getClassLoader().getResource("sound/gameOver.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(audioIn);

            url = this.getClass().getClassLoader().getResource("sound/eatApple.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            eatAppleSound = AudioSystem.getClip();
            eatAppleSound.open(audioIn);

            url = this.getClass().getClassLoader().getResource("sound/gameMusic.wav");
            audioIn = AudioSystem.getAudioInputStream(url);
            gameMusicSound = AudioSystem.getClip();
            gameMusicSound.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        }
    }

    public void pauseMusic() {
        if (gameMusicSound.isRunning()) {
            gameMusicSound.stop();
        } else {
            gameMusicSound.loop(100);
            gameMusicSound.start();
        }
    }

    public void playMusic() {
        gameMusicSound.setMicrosecondPosition(0);
        gameMusicSound.loop(100);
        gameMusicSound.start();
    }

    public void stopMusic() {
        gameMusicSound.stop();
    }

    public void playGameOverSound() {
        gameOverSound.setMicrosecondPosition(0);
        gameOverSound.start();
    }

    public void playEatAppleSound() {
        eatAppleSound.setMicrosecondPosition(0);
        eatAppleSound.start();
    }

    /**
     * Carga estadísticas del juego desde un archivo de texto. Si no existe ningún archivo de texto, se crea uno nuevo.
      *  created.
     *
     */
    public void loadData() {
        Path path = Paths.get("./SnakeData.txt");
        String line;
        int dataIndex = 0;
        try (InputStream in = Files.newInputStream(path)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                data[dataIndex] = Integer.parseInt(line.replaceAll(dataID[dataIndex], ""));
                dataIndex++;
            }
            reader.close();
        } catch (Exception ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Guarda las estadísticas del juego en un archivo de texto. *
     */
    public void saveData() {
        for (int datum : data) {
            System.out.println(datum);
        }
        Path path = Paths.get("./SnakeData.txt");
        try (OutputStream out = Files.newOutputStream(path)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            for (int i = 0; i < data.length; i++) {
                writer.write(dataID[i] + Integer.toString(data[i]));
                writer.newLine();
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void quit() {
        saveData();
        System.exit(0);
    }

}
