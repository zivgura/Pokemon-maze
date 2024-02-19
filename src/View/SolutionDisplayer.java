package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;



public class SolutionDisplayer extends Canvas /*implements Observer*/ {

    private StringProperty solutionImagePath = new SimpleStringProperty();
    private Solution solution;
    private Maze maze;

    public String getSolutionImagePath() {
        return solutionImagePath.get();
    }

    public StringProperty solutionImagePathProperty() {
        return solutionImagePath;
    }

    public void setSolutionMaze(Maze maze){
        this.maze=maze;
    }

    public void setSolutionImagePath(String solutionImagePath) {
        this.solutionImagePath.set(solutionImagePath);
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution, Maze maze) {
        this.maze=maze;
        this.solution = solution;
        redraw();
    }

    public void clearSolution(){
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    public void redraw() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / maze.getMaze().length;
        double cellWidth = canvasWidth / maze.getMaze()[0].length;
        try {
            Image solutionImage = new Image(new FileInputStream(solutionImagePath.get()));
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            for (int i = 0; i < maze.getMaze().length; i++) {
                for (int j = 0; j < maze.getMaze()[i].length; j++) {
                    if (solution.getSolutionPath().contains(new MazeState(new Position(i,j)))&& !maze.getGoalPosition().equals(new Position(i,j))) {
                        gc.drawImage(solutionImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        } catch (FileNotFoundException e) {

        }
    }

//    @Override
//    public void update(Observable o, Object arg) {
//
//    }
}
