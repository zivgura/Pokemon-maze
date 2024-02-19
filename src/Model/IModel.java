package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {
    void generateMaze(int height, int width);
    Maze getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    Solution getSolution();
    void moveCharacter(KeyCode code);
    void solveMaze();
    void startServers();
    void stopServers();
    void SaveMaze(File saveFile);
    void LoadMaze(File loadFile);

}
