package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.visuals.AnimationManager;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    protected AnimationManager am;
//	am.addListener("AnimationStarted", this, this.disableUI);
//	am.addListener("AnimationEnded", this, this.enableUI);
//	am.addListener("AnimationUndo", this, this.undo);
	public double canvasWidth;
	public double canvasHeight;


    public Algorithm(AnimationManager am, double canvasWidth, double canvasHeight) {
        this.am = am;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }


}
