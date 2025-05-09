package edu.citu.procrammers.eva.utils.visuals;


import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ChangeElementCommand extends Command {
    String newElement;
    String prevElement;
    Text text;
    public ChangeElementCommand(String newElement, Node n) {
        this.newElement = newElement;
        StackPane sp = (StackPane) n;
        text = ((Text) sp.getChildren().getLast());
        prevElement = text.getText();
    }

    @Override
    public void execute(Runnable onFinished) {
        text.setText(newElement);
        onFinished.run();
    }

    @Override
    public void undo(Runnable onUndo) {
        text.setText(prevElement);
        onUndo.run();
    }
}
