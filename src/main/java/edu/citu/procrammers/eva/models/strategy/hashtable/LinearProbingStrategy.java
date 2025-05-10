package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.utils.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;

public class LinearProbingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private final int value;
    private final int capacity;
    private final String buttonId;

    public LinearProbingStrategy(ArrayList<ArrayNode> array, int value, String buttonId) {
        this.array = array;
        this.value = value;
        capacity = array.size();
        this.buttonId = buttonId;
    }

    @Override
    public int handleCollision(int index) {

        /*
         * Param: Index is initially the result from the compression function.
         * Return: FINISHED if it found the index to insert the value in.
         * Else, Iteration increments by 1.
         */
        if (buttonId.equals("btnAdd")) {
            if((array.get(index).getNumber() == EMPTY || array.get(index).getNumber() == SENTINEL)) {
                return FINISHED;
            }
        }
        else if (value == array.get(index).getNumber()) {
                return FINISHED;
        }



        return (index + 1) % capacity;
    }
}
