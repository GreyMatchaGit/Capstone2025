package edu.citu.procrammers.eva.models.strategy.hashtable;

public interface CollisionStrategy {
    /**
     * @Param: Index is initially the result of the compression function.
     * @Return: FINISHED if it found the index to insert the value in.
     * Otherwise, return the next index to check for collision.
     */
    int getIterations();
    int handleCollision(int index);
}
