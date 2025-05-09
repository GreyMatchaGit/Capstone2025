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

    public QuadraticProbingStrategy(ArrayList<ArrayNode> array, int value, int originalIndex) {
        this.array = array;
        this.value = value;
        size = array.size();
        this.originalIndex = originalIndex;
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
        return (originalIndex + iteration * iteration) % size;
    }
}
