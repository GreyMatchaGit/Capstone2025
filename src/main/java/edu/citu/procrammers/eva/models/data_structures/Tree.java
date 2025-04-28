package edu.citu.procrammers.eva.models.data_structures;


public abstract class Tree {
    Node root;
    int size;

    public abstract Node insert(int num);
    public abstract int remove(int num);
}
