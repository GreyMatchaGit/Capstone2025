package edu.citu.procrammers.eva.models.strategy.hashtable;



import edu.citu.procrammers.eva.models.data_structures.ArrayNode;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;

public class QuadraticProbingStrategy implements CollisionStrategy {

    private ArrayList<ArrayNode> array;
    private int value;
    private int size;
    private int iterations;
    private int originalIndex;
    private String buttonId;
    boolean isStarted;

    public QuadraticProbingStrategy(ArrayList<ArrayNode> array, int value, int originalIndex, String buttonId) {
        this.array = array;
        this.value = value;
        size = array.size();
        this.originalIndex = originalIndex;
        iterations = 0;
        this.buttonId = buttonId;
        isStarted = false;
    }

    public int checkNext(int index) {
        return (originalIndex + iterations * iterations) % size;
    }

    public int checkPrevious() {
        return (originalIndex + iterations * iterations) % size;
    }

    @Override
    public int getPrevious(int index) {
        // TODO
        return 0;
    }

    @Override
    public int getNext(int index) {
        return (originalIndex + iterations * iterations) % size;
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

        switch (buttonId) {
            case "btnAdd":
                if (array.get(index).getNumber() == EMPTY || array.get(index).getNumber() == SENTINEL) {
                    return FINISHED;
                }
                break;

            case "btnRemove":
            case "btnSearch":
                iterations++;
                if (value == array.get(index).getNumber() || (originalIndex == index && isStarted) || array.get(index).getNumber() == EMPTY) {
                    return FINISHED;
                }
                break;
        }

        iterations++;
        if (originalIndex == index && isStarted) {
            return ERROR;
        }
        isStarted = true;
        return (originalIndex + iterations * iterations) % size;
    }
}
