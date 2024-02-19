package View;
import ViewModel.MyViewModel;
import java.io.IOException;


public interface IView {
    void ShowNew() throws IOException;
    void fileLoad() throws IOException;
    void ShowProperties() throws  IOException;
    void ShowHelp() throws IOException;
    void ShowAbout() throws  IOException;
    void Exit();
    void setViewModel(MyViewModel viewModel);

}
