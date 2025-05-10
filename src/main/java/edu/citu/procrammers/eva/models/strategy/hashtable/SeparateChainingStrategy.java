package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.controllers.HashTableController;
import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import edu.citu.procrammers.eva.utils.Constant;
import javafx.animation.SequentialTransition;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.FINISHED;

public class SeparateChainingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private int value;
    private String buttonId;

    public SeparateChainingStrategy(ArrayList<ArrayNode> array, int value, String buttonId) {
        this.array = array;
        this.value = value;
        this.buttonId = buttonId;
    }

    @Override
    public int getIterations() {
        return 0;
    }

    @Override
    public int getPrevious(int index) {
        return 0;
    }

    @Override
    public int getNext(int index) {
        return 0;
    }

    @Override
    public int handleCollision(int index) {
        switch (buttonId) {
            case "btnAdd":
                array.get(index).addToBucket(value);
                break;
            case "btnRemove":
                array.get(index).removeFromBucket(value);
                break;
            case "btnSearch":
                SequentialTransition st = new SequentialTransition();
                array.get(index).searchBucket(value, st);

                st.setOnFinished( event -> {
                    HashTableController.unhighlightNode(array.get(index), 400, Constant.Color.DEFAULTR).play();
                });
                st.play();
                break;
            default:
                throw new IllegalArgumentException("Invalid button id: " + buttonId);
        }

        return FINISHED;
    }
}
