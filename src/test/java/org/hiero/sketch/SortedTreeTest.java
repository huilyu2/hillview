package org.hiero.sketch;

import org.junit.Test;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class SortedTreeTest {
    private final SortedMap<Integer, Integer> myMap = new TreeMap<Integer, Integer>();
    private final Random rn = new Random();

    @Test
    public void testSortedTree() {
        for (int i = 1; i < 50; i++)
            this.myMap.put(this.rn.nextInt(10000), i);
        //for (Object j : myMap.keySet()) out.println("Key " + j + ", Value: " + myMap.get(j));
    }
}
