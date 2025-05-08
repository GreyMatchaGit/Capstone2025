package edu.citu.procrammers.eva.models;

import edu.citu.procrammers.eva.models.data_structures.Node;

public interface BinaryTreeIterator {
    boolean hasNext();
    Node next();
}
