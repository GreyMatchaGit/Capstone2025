package edu.citu.procrammers.eva.models.strategy.hashtable;

import edu.citu.procrammers.eva.utils.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;

public class QuadraticProbingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private int value;
    private int size;
    private int iteration;

    public QuadraticProbingStrategy(ArrayList<ArrayNode> array, int value) {
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

        int currentIndex = (index + iteration * iteration) % size;
        if(array.get(currentIndex).getNumber() == EMPTY
                || array.get(currentIndex).getNumber() == SENTINEL) {
            array.get(currentIndex).setNumber(value);
            return FINISHED;
        }

        iteration++;
        return index;
    }
}
