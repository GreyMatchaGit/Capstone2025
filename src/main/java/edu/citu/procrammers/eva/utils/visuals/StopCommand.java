package edu.citu.procrammers.eva.utils.visuals;

public class StopCommand extends Command {
    public StopCommand() {}

    @Override
    public void execute(Runnable onFinished) {

    }

    @Override
    public void undo(Runnable onUndo) {

    }

    @Override
    public String toString() {
        return "StopCommand";
    }
}
