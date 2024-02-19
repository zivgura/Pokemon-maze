package ViewModel;

import Model.IModel;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {



    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }


    public IModel getModel() {
        return model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==model)
        {
            setChanged();
            notifyObservers(arg);
        }

    }

    public void generateMaze (int height ,int width) throws Exception {
        model.generateMaze(height,width);

    }

    public void moveCharacter(KeyCode code) {
        model.moveCharacter(code);
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public void SaveMaze(File saveFile) {
        model.SaveMaze(saveFile);
    }


    public void LoadMaze(File loadFile) {
        model.LoadMaze(loadFile);
    }
}
