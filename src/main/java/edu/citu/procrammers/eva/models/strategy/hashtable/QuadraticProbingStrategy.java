package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.utils.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;

public class QuadraticProbingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private int value;
    private int size;
    private int iteration;
    private int originalIndex;
    private String buttonId;

    public QuadraticProbingStrategy(ArrayList<ArrayNode> array, int value, int originalIndex, String buttonId) {
        this.array = array;
        this.value = value;
        size = array.size();
        this.originalIndex = originalIndex;
        iteration = 0;
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

        iteration++;
        if (iteration >= array.size() * 2) {
            return ERROR;
        }

        return (originalIndex + iteration * iteration) % size;
    }
}
