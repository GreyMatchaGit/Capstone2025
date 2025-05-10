package edu.citu.procrammers.eva.models.strategy.hashtable;


import edu.citu.procrammers.eva.models.data_structures.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;

public class LinearProbingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private final int value;
    private final int capacity;
    int iterations;
    private final String buttonId;

    public LinearProbingStrategy(ArrayList<ArrayNode> array, int value, String buttonId) {
        this.array = array;
        this.value = value;
        capacity = array.size();
        this.buttonId = buttonId;
        iterations = 0;
    }

    @Override
    public int getPrevious(int index) {
        return (index - 1) < 0 ? capacity-1 : index - 1;
    }

    @Override
    public int getNext(int index) {
        return (index + 1) % capacity;
    }

    @Override
    public int getIterations() {
        return iterations;
    }

    @Override
    public int handleCollision(int index) {

        /*
         * Param: Index is initially the result from the compression function.
         * Return: FINISHED if it found the index to insert the value in.
         * Else, Iteration increments by 1.
         */
        System.out.println("Current index; " + index);

        switch (buttonId) {
            case "btnAdd":
                if (array.get(index).getNumber() == EMPTY || array.get(index).getNumber() == SENTINEL) {
                    return FINISHED;
                }
                break;

            case "btnRemove":
            case "btnSearch":
                iterations++;
                if (value == array.get(index).getNumber() || iterations >= capacity || array.get(index).getNumber() == EMPTY) {
                    return FINISHED;
                }
                break;
        }

        return (index + 1) % capacity;
    }
}
