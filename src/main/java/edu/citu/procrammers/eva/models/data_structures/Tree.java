package edu.citu.procrammers.eva.models.data_structures;


import edu.citu.procrammers.eva.controllers.Algorithm;
import edu.citu.procrammers.eva.utils.visuals.AnimationManager;

public abstract class Tree extends Algorithm {
    Node root;
    int size;

    public Tree(AnimationManager am, double canvasWidth, double canvasHeight) {
        super(am, canvasWidth, canvasHeight);
    }

    public abstract Node insert(int num);
    public abstract int remove(int num);
}
