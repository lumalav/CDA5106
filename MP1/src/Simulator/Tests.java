package Simulator;

import java.util.*;

/**
 * LRU and PLRU tests
 * TODO: Optimal tests
 */
public class Tests {
    public static void TestLRU() throws Exception {
        LRUPolicy policy = new LRUPolicy(null);

        Set set = new Set(null, 4, 0);

        set.Allocate("A", new Operation(ProcessorRequest.Read, "A", 0));
        Block b = set.Allocate("B", new Operation(ProcessorRequest.Read, "B", 1));
        set.Allocate("C", new Operation(ProcessorRequest.Read, "C", 2));
        Block d = set.Allocate("D", new Operation(ProcessorRequest.Read, "D", 3));
        
        //hit D twice

        set.Counter++;
        d.Counter = set.Counter;
        d.Dirty = true;

        set.Counter++;
        d.Counter = set.Counter;

        //hit b 

        set.Counter++;
        b.Counter = set.Counter;
        b.Dirty = true;

        //hit d

        set.Counter++;
        d.Counter = set.Counter;

        policy.MinHeap.clear();
        policy.MinHeap.addAll(Arrays.asList(set.Blocks));
        Block result = policy.MinHeap.poll(); //A
        set.Reallocate(result.Column, "E", new Operation(ProcessorRequest.Read, "E", 4));

        for(Block block: set.Blocks) {
            System.out.println(block.toString() + "\t" + (block.Dirty ? "\t" : "") + "(" + block.Counter + ")");
        }
    }

    public static void TestPLRU() throws Exception {
        PLRUTree t = new PLRUTree(4);
        t.Hit(1);
        t.Hit(2);
        t.Hit(0);
        t.Hit(3);
        int lRU = t.GetLRU();
        System.out.println("LRU => " + lRU); //prints 1
        t.Hit(1);
        t.PrintFlipBits();
    }
}
