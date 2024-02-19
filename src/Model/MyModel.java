package Model;


import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Observable;

public class MyModel extends Observable implements IModel {

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Maze maze;

    private int characterPositionRow;
    private int characterPositionColumn;

    private Solution solution;

    public MyModel(){
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer= new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServers(){
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }


    @Override
    public void generateMaze(int height, int width) {
        CommunicateWithServer_MazeGenerating(height,width);
        characterPositionRow=this.maze.getStartPosition().getRowIndex();
        characterPositionColumn=this.maze.getStartPosition().getColumnIndex();
        solution=null;
        setChanged();
        notifyObservers();
    }

    private void CommunicateWithServer_MazeGenerating(int height, int width) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{height, width};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        //compute size
                        byte[] decompressedMaze = new byte[height*width+13 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        Maze mazeFromServer = new Maze(decompressedMaze);
                        setMaze(mazeFromServer);

                    } catch (Exception e) {

                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {

        }
    }

//    private int computeDecompredMazeSize(int height,int width){
//        int numberOfCellsForRows= Math.round(height/127);
//        int numberOfCellsForColumns= Math.round(width/127);
//        int sum;
//        return sum;
//    }

    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                    } catch (Exception e) {

                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {

        }
    }

    //Character
    @Override
    public void moveCharacter(KeyCode movement) {
        //Movement theMovement=Movement.START;
        switch (movement) {
            case NUMPAD8:
            case UP: {
                if ((characterPositionRow - 1) >= 0 && maze.getMaze()[characterPositionRow - 1][characterPositionColumn] == 0) {
                    characterPositionRow--;
                    //theMovement = Movement.UP;
                }
                setChanged();
                notifyObservers("UP");
                break;
            }
            case NUMPAD2:
            case DOWN:{
                if ( characterPositionRow + 1 < maze.getMaze().length && maze.getMaze()[characterPositionRow + 1][characterPositionColumn] == 0) {
                    characterPositionRow++;
                }
                setChanged();
                notifyObservers("DOWN");
                break;
            }
            case NUMPAD6:
            case RIGHT:{
                if (characterPositionColumn + 1 < maze.getMaze()[0].length && maze.getMaze()[characterPositionRow][characterPositionColumn + 1] == 0) {
                    characterPositionColumn++;
                }
                setChanged();
                notifyObservers("RIGHT");
                break;
            }
            case NUMPAD4:
            case LEFT: {
                if (characterPositionColumn - 1 >= 0 && maze.getMaze()[characterPositionRow][characterPositionColumn - 1] == 0){
                    characterPositionColumn--;
                }
                setChanged();
                notifyObservers("LEFT");
                break;
            }
            case NUMPAD7:{//left up
                if (characterPositionRow - 1 >= 0 && characterPositionColumn - 1 >= 0 && (maze.getMaze()[characterPositionRow - 1][characterPositionColumn] == 0 || maze.getMaze()[characterPositionRow][characterPositionColumn - 1] == 0)&& maze.getMaze()[characterPositionRow - 1][characterPositionColumn-1] == 0) {
                    characterPositionRow--;
                    characterPositionColumn--;
                }
                setChanged();
                notifyObservers("UPLEFT");
                break;
            }
            case NUMPAD9:{//right up
                if (characterPositionRow - 1 >= 0 && characterPositionColumn + 1 < maze.getMaze()[0].length && (maze.getMaze()[characterPositionRow - 1][characterPositionColumn] == 0 || maze.getMaze()[characterPositionRow][characterPositionColumn + 1] == 0)&& maze.getMaze()[characterPositionRow - 1][characterPositionColumn+1] == 0) {
                    characterPositionRow--;
                    characterPositionColumn++;
                }
                setChanged();
                notifyObservers("UPRIGHT");
                break;
            }
            case NUMPAD3:{//right down
                if ( characterPositionRow + 1 < maze.getMaze().length&& characterPositionColumn + 1 < maze.getMaze()[0].length && (maze.getMaze()[characterPositionRow + 1][characterPositionColumn] == 0 || maze.getMaze()[characterPositionRow][characterPositionColumn + 1] == 0)&& maze.getMaze()[characterPositionRow + 1][characterPositionColumn+1] == 0) {
                    characterPositionRow++;
                    characterPositionColumn++;
                }
                setChanged();
                notifyObservers("DOWNRIGHT");
                break;
            }
            case NUMPAD1:{//left down
                if (characterPositionColumn - 1 >=0 && characterPositionRow + 1 < maze.getMaze().length && (maze.getMaze()[characterPositionRow + 1][characterPositionColumn] == 0 || maze.getMaze()[characterPositionRow][characterPositionColumn - 1] == 0)&& maze.getMaze()[characterPositionRow + 1][characterPositionColumn-1] == 0) {
                    characterPositionRow++;
                    characterPositionColumn--;
                }
                setChanged();
                notifyObservers("DOWNLEFT");
                break;
            }
            case HOME:
            case NUMPAD5: {
                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
                setChanged();
                notifyObservers("START");
                break;
            }
        }

    }

    public void solveMaze(){
        CommunicateWithServer_SolveSearchProblem();
        setChanged();
        notifyObservers();
    }

    public Maze getMaze() {
        return maze;
    }

    public Solution getSolution() {
        return solution;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    @Override
    public void SaveMaze(File saveFile) {

        if(saveFile!=null){
            try{
                byte[] mazeByteArray = maze.toByteArray();
                byte[] finalByteArray = new byte[mazeByteArray.length + 2];
                for(int i = 0; i < mazeByteArray.length; i++){
                    finalByteArray[i] = mazeByteArray[i];
                }
                finalByteArray[mazeByteArray.length] = (byte)characterPositionRow;
                finalByteArray[mazeByteArray.length+1] = (byte)characterPositionColumn;
                saveFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(saveFile);
                fos.write(finalByteArray);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }


    @Override
    public void LoadMaze(File loadFile) {
        if(loadFile!=null){
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(loadFile.getPath()));
                Position currentPotision = new Position(bytes[bytes.length-2],bytes[bytes.length-1]);
                byte[] mazeByteArray = new byte[bytes.length - 2];
                for(int i = 0; i < mazeByteArray.length; i++){
                    mazeByteArray[i] = bytes[i];
                }
                Maze loadedMaze = new Maze(mazeByteArray);
                setMaze(loadedMaze);
                characterPositionRow=currentPotision.getRowIndex();
                characterPositionColumn=currentPotision.getColumnIndex();
//                setChanged();
//                notifyObservers();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
