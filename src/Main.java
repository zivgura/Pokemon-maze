import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.io.FileInputStream;
import java.util.Optional;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        /*First Scene*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("View/MyView.fxml").openStream());
        primaryStage.setTitle("Pokemon Maze");
        Scene mainScene = new Scene(root);
        mainScene.getStylesheets().add("View/pokemon.css");
        primaryStage.setScene(mainScene);

        MyViewController mainView = fxmlLoader.getController();

        mainView.initialize(viewModel,primaryStage,mainScene);

        mainView.setViewModel(viewModel);
        mainView.relocate(mainScene);
        setCloseEvent(primaryStage,model);
        primaryStage.setResizable(false);

        /*Icons*/
        primaryStage.getIcons().add(new Image(new FileInputStream("resources/Images/icon.png")));
        ImageView speaker = new ImageView(new Image(new FileInputStream("resources/Images/mute.png")));
        speaker.setFitWidth(20);
        speaker.setFitHeight(20);
        mainView.muteButton.setGraphic(speaker);

        mainView.play("resources/Music/themeSong.mp3");
        primaryStage.show();

    }

    private void setCloseEvent(Stage primaryStage, MyModel model) {
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                model.stopServers();
            } else {
                event.consume();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
