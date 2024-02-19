package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class File_NewController extends MyViewController implements Observer {

    /*fxml fields*/
    public TextField txt_rows;
    public TextField txt_columns;
    public Button button_generateMaze;
    public Button button_solveMaze;
    public Pane centerPane;
    public CharacterDisplayer canvas_characterDisplayer;
    public MazeDisplayer canvas_mazeDisplayer;
    public SolutionDisplayer canvas_solutionDisplayer;
    public ScrollPane scrollPane;
    public Button returnToNormalSize_button;
    public Button newMuteButton;

    /*fields*/
    private MyViewController viewController;
    private Stage mainStage;
    private Scene newScene;

    /*Getters and Setters*/
    public void setViewController(MyViewController viewController) {
        this.viewController = viewController;
    }

    public void setTxt_rows(int rows) {
        this.txt_rows.setText(Integer.toString(rows));
    }

    public void setTxt_columns(int columns) {
        this.txt_columns.setText(Integer.toString(columns));
    }

    /*Overrides*/
    public void initialize(Stage primaryStage, Scene newScene) {
        this.mainStage=primaryStage;
        this.newScene=newScene;
        newButton.setDisable(true);
        applyTextConstrains();

    }

    @Override
    public void update(Observable o, Object arg) {
        if(o== viewModel || (o==viewController&&viewController.isChanged))
        {
            displayMaze(viewModel.getModel().getMaze());
            displayCharacter(viewModel.getModel().getCharacterPositionRow(), viewModel.getModel().getCharacterPositionColumn(),(String)arg);
            if (viewModel.getModel().getSolution()!=null) {
                displaySolution(viewModel.getModel().getSolution());
            }
            else{
                canvas_solutionDisplayer.setSolutionMaze(viewModel.getModel().getMaze());
            }
            button_generateMaze.setDisable(false);
            if(viewModel.getModel().getMaze()!=null)
                button_solveMaze.setDisable(false);
            canvas_characterDisplayer.toFront();
            if(viewController.isChanged) { // maze loaded
                viewController.isChanged = false;
                saveButton.setDisable(false);
                setTxt_rows(viewModel.getModel().getMaze().getMaze().length);
                setTxt_columns(viewModel.getModel().getMaze().getMaze()[0].length);
                canvas_solutionDisplayer.clearSolution();
                requestFocus();
            }
            if(viewModel.getModel().getCharacterPositionRow()==viewModel.getModel().getMaze().getGoalPosition().getRowIndex() &&
                    viewModel.getModel().getCharacterPositionColumn()==viewModel.getModel().getMaze().getGoalPosition().getColumnIndex()){
                gameOver();
            }
            zoomOnScroll();
        }
    }

    /*functionality*/

    /**
     * Displaying the maze on the screen.
     * @param maze is the maze generated in server
     */
    private void displayMaze(Maze maze) {
        canvas_mazeDisplayer.setMaze(maze);
    }

    /**
     * Displaying the character on the maze.
     * @param rowIndex current character row position
     * @param columnIndex current character columns position
     * @param movement current character movement direction
     */
    private void displayCharacter(int rowIndex, int columnIndex, String movement) {
        if(movement!=null)
            canvas_characterDisplayer.setCharacterImagePath(movement);
        canvas_characterDisplayer.setCharacterPosition(rowIndex,columnIndex, viewModel.getModel().getMaze());
    }

    /**
     * Displaying the solution on the maze.
     * @param solution is the solution generated in server
     */
    private void displaySolution(Solution solution){
        canvas_solutionDisplayer.setSolution(solution, viewModel.getModel().getMaze());
    }


    /**
     * Responsible on sending a request to the generation server.
     * @throws Exception from the generation server
     */
    public void generateMaze() throws Exception {
        canvas_solutionDisplayer.clearSolution();
        int height = Integer.valueOf(txt_rows.getText());
        int width = Integer.valueOf(txt_columns.getText());
        button_generateMaze.setDisable(true);
        canvas_mazeDisplayer.randomizedEndPosition();
        viewModel.generateMaze(height , width);
        resize();
        saveButton.setDisable(false);
        requestFocus();

    }

    /**
     * Responsible on sending a request to the solving server, and switching the text on the button
     */
    public void solveMaze() {
        if(button_solveMaze.getText().equals("Solve Maze")) {
            viewModel.solveMaze();
        }
        else if(button_solveMaze.getText().equals("Hide Solution"))
            canvas_solutionDisplayer.clearSolution();
        switchSolveButton();
    }

    /**
     * Responsible on setting the "Save" option of the app.
     * the files will be saved in format .byteArray
     */
    public void fileSave() {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.byteArray"));
        fc.setInitialFileName("PokemonMaze");
        File saveFile = fc.showSaveDialog(menuBar.getScene().getWindow());
        viewModel.SaveMaze(saveFile);
        //isChanged = false;

    }

    /*Help functions*/

    public void requestFocus() {
        canvas_mazeDisplayer.requestFocus();
    }

    /**
     * Applying text constrains on the rows and columns text fields.
     */
    private void applyTextConstrains(){
        txt_rows.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")||newValue.equals("0"))
            {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("illegal argument");
                alert.setContentText("insert number between 3-127");
                Optional<ButtonType> result = alert.showAndWait();
                txt_rows.setText(oldValue);
            }
            else if (!newValue.matches("\\d{0,3}?") || (Integer.valueOf(newValue)>127)) {
                txt_rows.setText(oldValue);
            }
        });
        txt_columns.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")||newValue.equals("0")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("illegal argument");
                alert.setContentText("insert number between 3-127");
                Optional<ButtonType> result = alert.showAndWait();
                txt_columns.setText(oldValue);
            }
            else if (!newValue.matches("\\d{0,3}?")|| Integer.valueOf(newValue)>127) {
                txt_columns.setText(oldValue);
            }
        });
    }

    private void switchSolveButton() {
        if(button_solveMaze.getText().equals("Solve Maze")){
            button_solveMaze.setText("Hide Solution");
        }
        else if(button_solveMaze.getText().equals("Hide Solution")) {
            button_solveMaze.setText("Solve Maze");
        }

    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    /**
     * Enables to zoom in and out using CTRL+Mouse scroll
     */
    private void zoomOnScroll(){
        //KeyCodeCombination ctrlScroll = new KeyCodeCombination(KeyCode.CONTROL, MouseEvent.)
        centerPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                centerPane.setOnScroll(scrollEvent -> {
                    double zoomFactor = 1.05;
                    if (scrollEvent.getDeltaY() < 0)
                        zoomFactor = 0.95;
                    centerPane.setScaleX(centerPane.getScaleX() * zoomFactor);
                    centerPane.setScaleY(centerPane.getScaleY() * zoomFactor);
                    returnToNormalSize_button.setDisable(false);
                });

            }
            else
                KeyPressed(event);

        });
        centerPane.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                centerPane.setOnScroll(scrollEvent -> {});
            }

        });
    }

    public void returnToNormalSize(){
        centerPane.setScaleX(scrollPane.getScaleX());
        centerPane.setScaleY(scrollPane.getScaleY());
        returnToNormalSize_button.setDisable(true);
        canvas_characterDisplayer.requestFocus();
    }

    /**
     * Enables the maze to resize when the window resized.
     */
    private void resize(){
        scrollPane.widthProperty().addListener( (v,oldValue,newValue) -> {
            canvas_mazeDisplayer.setWidth(newValue.doubleValue());
            canvas_mazeDisplayer.redraw();
            canvas_characterDisplayer.setWidth(newValue.doubleValue());
            canvas_characterDisplayer.redraw();
            canvas_solutionDisplayer.setWidth(newValue.doubleValue());
            if(viewModel.getModel().getSolution()!=null&& button_solveMaze.getText().equals("Hide Solution")) {
                canvas_solutionDisplayer.redraw();
            }
        });
        scrollPane.heightProperty().addListener( (v,oldValue,newValue) ->{
            canvas_mazeDisplayer.setHeight(newValue.doubleValue());
            canvas_mazeDisplayer.redraw();
            canvas_characterDisplayer.setHeight(newValue.doubleValue());
            canvas_characterDisplayer.redraw();
            canvas_solutionDisplayer.setHeight(newValue.doubleValue());
            if(viewModel.getModel().getSolution()!=null && button_solveMaze.getText().equals("Hide Solution")) {
                canvas_solutionDisplayer.redraw();
            }
        });
    }

    /**
     * Switches icons and tooltips on mute button
     * @throws FileNotFoundException if the images not found in resources package
     */
    public void switchMusicState() throws FileNotFoundException {
        if (viewController.mediaPlayer!=null && viewController.mediaPlayer.isMute()) {
            ImageView speaker = new ImageView(new Image(new FileInputStream("resources/Images/mute.png")));
            speaker.setFitWidth(20);
            speaker.setFitHeight(20);
            newMuteButton.setGraphic(speaker);
            newMuteButton.setTooltip(new Tooltip("Mute music"));
            viewController.mediaPlayer.setMute(false);
        }
        else {
            ImageView speaker = new ImageView(new Image(new FileInputStream("resources/Images/unmute.png")));
            speaker.setFitWidth(20);
            speaker.setFitHeight(20);
            newMuteButton.setGraphic(speaker);
            newMuteButton.setTooltip(new Tooltip("Unmute music"));
            viewController.mediaPlayer.setMute(true);
        }
    }

    /**
     * Notifies the player that the game is over and displays options to do next.
     */
    private void gameOver() {
        viewController.pauseMusic();
        AudioClip clip = new AudioClip(new File("resources/Music/winningSong.mp3").toURI().toString());
        clip.play(1);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congrats!");
        alert.setHeaderText("You've caught them All!");
        alert.setContentText("what you want to do next?");
        ButtonType playAgain = new ButtonType("Play Again");
        ButtonType exit = new ButtonType("Exit");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(playAgain,exit,cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == playAgain){
            try {
                viewController.switchMusicState();
                playAgain();
                //viewController.ShowNew();
            } catch (Exception e) {

            }
        } else if (result.get() == exit) {
            Exit();
        }
        else{
            try {
                viewController.switchMusicState();
            } catch (FileNotFoundException e) {

            }
        }
    }

    private void playAgain() throws FileNotFoundException {
        canvas_solutionDisplayer.clearSolution();
        canvas_characterDisplayer.clearCharacter();
        canvas_mazeDisplayer.clearMaze();
        button_solveMaze.setDisable(true);
        returnToNormalSize();
        setMuteButton(this);
        canvas_mazeDisplayer.setMaze(null);
        canvas_characterDisplayer.setMaze(null);
        mainStage.setScene(newScene);
    }
}
