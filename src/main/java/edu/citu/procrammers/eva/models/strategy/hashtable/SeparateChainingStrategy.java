package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.utils.ArrayNode;

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
    public int handleCollision(int index) {
        switch (buttonId) {
            case "btnAdd":
                array.get(index).addToBucket(value);
                break;
            case "btnRemove":
                array.get(index).removeFromBucket(value);
                break;
            case "btnSearch":
                array.get(index).searchBucket(value, null);
                break;
            default:
                throw new IllegalArgumentException("Invalid button id: " + buttonId);
        }

        return FINISHED;
    }
}
