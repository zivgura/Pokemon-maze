package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;


public class MyViewController extends Observable implements Observer,IView {

    /*fxml fields*/
    public MenuBar menuBar;
    public Button startButton;
    public MenuItem newButton;
    public MenuItem saveButton;
    public MenuItem propertiesButton;
    public ComboBox mazeGenerator_choiceBox;
    public ComboBox searchAlgorithm_choiceBox;
    public ComboBox threadPool_choiceBox;
    public Button okButton;
    public Hyperlink Nadav_hyperLink;
    public Hyperlink Ziv_hyperLink;
    public Button muteButton;

    /*fields*/
    protected MyViewModel viewModel;
    protected Stage mainStage;
    protected Scene mainScene;
    protected boolean isChanged;
    public MediaPlayer mediaPlayer;


    public void initialize(MyViewModel viewModel,Stage primaryStage,Scene mainScene) {
        isChanged=false;
        this.viewModel=viewModel;
        this.mainStage=primaryStage;
        this.mainScene=mainScene;

    }

    /*Getters and Setters*/
    public MyViewModel getViewModel() {
        return viewModel;
    }
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel=viewModel;
    }
    public Button getStartButton() {
        return startButton;
    }
    public void setStartButton(Button startButton) {
        this.startButton = startButton;
    }

    /*Overrides*/

    /**
     * responsible on setting the second scene of the app, where the maze is
     * @throws IOException if there is a problem in the fxml file of this scene
     */
    @Override
    public void ShowNew() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent File_New = fxmlLoader.load(getClass().getResource("File_New.fxml").openStream());
        Scene newScene = new Scene(File_New);
        mainStage.setScene(newScene);
        File_NewController newController = fxmlLoader.getController();
        this.addObserver(newController);
        newController.setViewModel(viewModel);
        newController.setViewController(this);
        viewModel.addObserver(newController);
        mainStage.setResizable(true);
        newController.initialize(mainStage,newScene);

        setMuteButton(newController);
        newScene.getStylesheets().add("View/pokemon2.css");
        mainStage.show();

    }

    /**
     * responsible on setting the "Properties" window of the app, where the data from the configuration is.
     * @throws IOException if there is a problem in the fxml file of this scene
     */
    @Override
    public void ShowProperties()throws  IOException{
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent File_Properties = fxmlLoader.load(getClass().getResource("File_Properties.fxml").openStream());
        Scene propertiesScene = new Scene(File_Properties);
        Stage propertiesStage = new Stage();
        propertiesStage.setScene(propertiesScene);
        propertiesStage.setTitle("Properties");
        propertiesScene.getStylesheets().add("View/pokemon2.css");
        propertiesStage.getIcons().add(new Image(new FileInputStream("resources/Images/icon1.png")));

//        mazeGenerator_choiceBox.setPromptText(getMazeGenerationType().toString());
//        searchAlgorithm_choiceBox.setPromptText(getSearchingAlgorithmType().toString());
//        threadPool_choiceBox.setPromptText(String.valueOf(getThreadPoolSize()));
        propertiesStage.show();

        //mazeGenerator_choiceBox.setValue("MyMazeGenerator");
    }

    /**
     * responsible on setting the "About" window of the app, where the programmers' data is.
     * @throws IOException if there is a problem in the fxml file of this scene
     * */
    @Override
    public void ShowAbout() throws  IOException{
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent File_About = fxmlLoader.load(getClass().getResource("File_About.fxml").openStream());
        Scene aboutScene = new Scene(File_About);
        Stage AboutStage = new Stage();
        AboutStage.setTitle("About");
        AboutStage.setScene(aboutScene);
        aboutScene.getStylesheets().add("View/pokemon2.css");
        AboutStage.getIcons().add(new Image(new FileInputStream("resources/Images/icon2.png")));
        AboutStage.show();


    }

    /**
     * responsible on setting the "Help" window of the app, where the instructions are
     * @throws IOException if there is a problem in the fxml file of this scene
     */
    @Override
    public void ShowHelp() throws IOException{
        Parent File_Help = FXMLLoader.load(getClass().getResource("File_Help.fxml"));
        Scene helpScene = new Scene(File_Help);
        Stage HelpStage = new Stage();
        HelpStage.setTitle("Help");
        HelpStage.setScene(helpScene);
        HelpStage.setResizable(false);
        helpScene.getStylesheets().add("View/pokemon2.css");
        HelpStage.getIcons().add(new Image(new FileInputStream("resources/Images/icon4.png")));
        HelpStage.show();


    }

    /**
     * responsible on setting the "Exit" alert of the app, and make sure for an proper exit process
     */
    @Override
    public void Exit(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        alert.getButtonTypes().setAll(yes,no);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yes) {
            viewModel.getModel().stopServers();
            Platform.exit();
            System.exit(0);
        }
        else{

        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==viewModel)
        {
        }
    }

    /**
     * responsible on setting the "Load" option of the app.
     * @throws IOException if there is a problem in the fxml file of this scene
     */
    @Override
    public void fileLoad() throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.byteArray"));
        File loadFile = fc.showOpenDialog(menuBar.getScene().getWindow());
        if(loadFile==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("No file chosen");

            Optional<ButtonType> result = alert.showAndWait();
        }
        else {
            viewModel.LoadMaze(loadFile);
            isChanged = true;
            ShowNew();

            setChanged();
            notifyObservers();
        }

    }

    /*Help functions*/

    public void relocate(Scene scene){
        scene.widthProperty().addListener( (v,oldValue,newValue) -> {
            startButton.setLayoutX(newValue.doubleValue()*(4/5.0));
        });
        scene.heightProperty().addListener( (v,oldValue,newValue) -> {
            startButton.setLayoutY(newValue.doubleValue()*(3/4.0));
        });
    }

    public void CloseWindow() {
        ((Stage)okButton.getScene().getWindow()).close();
    }



    /*Pictures*/

    public void showNadavsPicture() {
        try {
            showPicture("resources/Images/Nadav.JPG/", "Nadav Chapnik");
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Image not exists");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    public void showZivsPicture(){
        try {
            showPicture("resources/Images/Ziv.JPG/","Ziv Gura");
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Image not exists");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    private void showPicture(String picturePath, String title) throws FileNotFoundException {
        FileInputStream input = new FileInputStream(picturePath);
        Image img = new Image(input);
        ImageView imageView=new ImageView(img);
        BorderPane root = new BorderPane();
        imageView.setFitHeight(300);
        imageView.setFitWidth(250);
        root.setCenter(imageView);
        Scene scene = new Scene(root,300,300);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle(title);
        scene.getStylesheets().add("View/pokemon2.css");
        stage.show();
    }

    /* Music */

    public void play(String musicFilePath){
        Media sound = new Media(new File(musicFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public void playClickSound(){
        AudioClip clip = new AudioClip(new File("resources/Music/clickSound.mp3").toURI().toString());
        clip.play(1);
    }

    public void switchMusicState() throws FileNotFoundException {
        if (mediaPlayer.isMute()) {
            ImageView speaker = new ImageView(new Image(new FileInputStream("resources/Images/mute.png")));
            speaker.setFitWidth(20);
            speaker.setFitHeight(20);
            muteButton.setGraphic(speaker);
            muteButton.setTooltip(new Tooltip("Mute music"));
            mediaPlayer.setMute(false);
        }
        else {
            ImageView speaker = new ImageView(new Image(new FileInputStream("resources/Images/unmute.png")));
            speaker.setFitWidth(20);
            speaker.setFitHeight(20);
            muteButton.setGraphic(speaker);
            muteButton.setTooltip(new Tooltip("Unmute music"));
            mediaPlayer.setMute(true);
        }
    }

    public void pauseMusic(){
        //mediaPlayer.pause();
        mediaPlayer.setMute(true);
    }

    public void resumeMusic(){
        //mediaPlayer.play();
        mediaPlayer.setMute(false);
    }

    protected void setMuteButton(File_NewController newController) throws FileNotFoundException {
        ImageView speaker;
        if(mediaPlayer!=null && mediaPlayer.isMute()) {
            speaker = new ImageView(new Image(new FileInputStream("resources/Images/unmute.png")));
            speaker.setFitWidth(20);
            speaker.setFitHeight(20);
            newController.newMuteButton.setTooltip(new Tooltip("Unmute music"));
        }
        else{
            speaker = new ImageView(new Image(new FileInputStream("resources/Images/mute.png")));
            speaker.setFitWidth(20);
            speaker.setFitHeight(20);
            newController.newMuteButton.setTooltip(new Tooltip("Mute music"));
        }
        newController.newMuteButton.setGraphic(speaker);
    }


}
