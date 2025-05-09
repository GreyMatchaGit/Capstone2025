package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.utils.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.FINISHED;

public class SeparateChainingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private int value;

    public SeparateChainingStrategy(ArrayList<ArrayNode> array, int value) {
        this.array = array;
        this.value = value;
    }

    @Override
    public int handleCollision(int index) {
        array.get(index).addToBucket(value);
        return FINISHED;
    }
}
