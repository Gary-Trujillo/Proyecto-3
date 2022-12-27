package view;

import controller.Controller;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Pasa keyPress y otras acciones desde la vista al controlador.
 * 
 * autor Gary Trujillo
 */
public class ViewListener implements KeyListener {

    boolean isGameOver;
    Controller controller = new Controller();

    public ViewListener() {
        isGameOver = false;
    }

    @Override
    public void keyPressed(KeyEvent key) {
        //System.out.println(key.toString());
        controller.directionInput(key);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void setGameOver(boolean isGameOver) {
        controller.setGameOver(isGameOver);
    }

    public void setNewGame(boolean isNewGame) {
        controller.setNewGame(isNewGame);
    }
    
    public void setChoosingDifficulty(boolean isChoosingDifficulty) {
        controller.setChoosingDifficulty(isChoosingDifficulty);
    }

}
