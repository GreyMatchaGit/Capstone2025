package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.utils.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;

public class LinearProbingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private final int value;
    private final int size;
    private int iteration;

    public LinearProbingStrategy(ArrayList<ArrayNode> array, int value) {
        this.array = array;
        this.value = value;
        size = array.size();
        iteration = 0;
    }

    @Override
    public int handleCollision(int index) {

        /*
         * Paramdam: Index is initially the result from the compression function.
         * Return: FINISHED if it found the index to insert the value in.
         * Else, Iteration increments by 1.
         */

        if(array.get(index).getNumber() == EMPTY
                || array.get(index).getNumber() == SENTINEL) {
            array.get(index).setNumber(value);
            return FINISHED;
        }
        iteration++;
        return (index + iteration) % size;
    }
}
