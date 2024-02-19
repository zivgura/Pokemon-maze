package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class MazeDisplayer extends Canvas {

    private StringProperty wallImagePath = new SimpleStringProperty();
    private StringProperty roadImagePath = new SimpleStringProperty();
    private StringProperty endPositionImagePath = new SimpleStringProperty();

    private Maze maze;

    /*Getters and Setters*/
    public String getEndPositionImagePath() {
        return endPositionImagePath.get();
    }

    public StringProperty endPositionImagePathProperty() {
        return endPositionImagePath;
    }

    public void setEndPositionImagePath(String endPositionImagePathNumber) {
        this.endPositionImagePath.set("resources/Images/endPosition"+endPositionImagePathNumber+".jpg");
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        redraw();
    }

    public String getWallImagePath() {
        return wallImagePath.get();
    }

    public StringProperty wallImagePathProperty() {
        return wallImagePath;
    }

    public void setWallImagePath(String wallImagePath) {
        this.wallImagePath.set(wallImagePath);
    }

    public String getRoadImagePath() {
        return roadImagePath.get();
    }

    public StringProperty roadImagePathProperty() {
        return roadImagePath;
    }

    public void setRoadImagePath(String roadImagePath) {
        this.roadImagePath.set(roadImagePath);
    }

    public void randomizedEndPosition(){
        Random rnd = new Random();
        int i = rnd.nextInt(4);
        i++;
        setEndPositionImagePath(String.valueOf(i));
    }

    /*functionality*/
    public void clearMaze(){
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
                Image wallImage = new Image(new FileInputStream(wallImagePath.get()));
                Image roadImage = new Image(new FileInputStream(roadImagePath.get()));
                Image endPositionImage = new Image(new FileInputStream(endPositionImagePath.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.getMaze().length; i++) {
                    for (int j = 0; j < maze.getMaze()[i].length; j++) {
                        if (maze.getMaze()[i][j] == 1) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        else if (maze.getMaze()[i][j] == 0) {
                            if(maze.getGoalPosition().equals(new Position(i,j)))
                                gc.drawImage(endPositionImage, j * cellWidth, i * cellHeight, cellWidth , cellHeight);

                            else
                                gc.drawImage(roadImage, j * cellWidth, i * cellHeight, cellWidth , cellHeight);
                        }
                    }
                }

            } catch (FileNotFoundException e) {

            }
        }
    }



}
