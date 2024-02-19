package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

public class CharacterDisplayer extends Canvas implements Observer {

    private StringProperty characterImagePath = new SimpleStringProperty();
    private int characterPositionRow;
    private int characterPositionColumn;

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    private Maze maze;

    public String getCharacterImagePath() {
        return characterImagePath.get();
    }

    public StringProperty characterImagePathProperty() {
        return characterImagePath;
    }

    public void setCharacterImagePath(String characterImageDirection) {
        this.characterImagePath.set("resources/Images/character"+characterImageDirection+".jpg");
    }

    public void setCharacterPosition(int rowIndex, int columnIndex, Maze maze) {
        this.maze=maze;
        characterPositionRow=rowIndex;
        characterPositionColumn=columnIndex;
        redraw();
    }

    public void clearCharacter(){
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getMaze().length;
            double cellWidth = canvasWidth / maze.getMaze()[0].length;
            try {
                Image characterImage = new Image(new FileInputStream(characterImagePath.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);

            } catch (FileNotFoundException e) {

            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
