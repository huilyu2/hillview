package org.hiero.sketch;
import org.hiero.sketch.spreadsheet.HeapTopK;
import org.junit.Test;
import java.util.Random;

/**
 * Tests for HashMap Implementation of TopK
 */
public class HeapTopKTest {
    private final int maxSize = 10;
    private final HeapTopK<Integer> myHeap = new HeapTopK<Integer>(this.maxSize, MyCompare.instance);

    @Test
    public void testHeapTopKZero() {
        final Random rn =new Random();
        for (int j =1; j <20; j++) {
            for (int i = 1; i < 1000; i++)
                this.myHeap.push(rn.nextInt(10000));
            //System.out.println(myHeap.getTopK().toString());
        }
    }

    @Test
    public void testHeapTopKTimed() {
        final Random rn = new Random();
        final long startTime = System.nanoTime();
        int inpSize = 1000;
        for (int j = 1; j < inpSize; j++)
            this.myHeap.push(rn.nextInt(inpSize));
        final long endTime = System.nanoTime();
        //System.out.format("Largest: %d%n", myHeap.getTopK().lastKey());
        //System.out.format("Time taken by HashMap: %d%n", (endTime - startTime) / 1000000);
    }
}
