package edu.citu.procrammers.eva.utils.visuals;

public abstract class Command {
    public abstract void execute(Runnable onFinished);
    public abstract void undo(Runnable onUndo);
}
