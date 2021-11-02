package Simulator;

import java.util.*;

/**
 * LRU policy. Keeps track of the usage of a block within a set and chooses the LRU for eviction.
 * A min heap is used to speedup the process
 */
public class LRUPolicy extends ReplacementPolicy {

    public PriorityQueue<Block> MinHeap;

    public LRUPolicy(Arguments arguments) {
        super(arguments);
        this.MinHeap = new PriorityQueue<>(new Comparator<Block>() {
            @Override
            public int compare(Block b1, Block b2) {
                return b2.Counter > b1.Counter ? -1 : b2.Counter == b1.Counter ? 0 : 1;
            }
        });
    }

    /**
     * Every hit updates the counter of the set and overrides the block counter
     */
    @Override
    protected void Hit(int index, int column) {
        this.Cache.Sets[index].Counter++;
        this.Cache.Sets[index].Blocks[column].Counter = this.Cache.Sets[index].Counter;
    }

    @Override
    protected void Allocate(int index, int column) {
    }

    @Override
    protected String ReplacementPolicyName() {
        return "LRU";
    }

    /**
     * Chooses the block to evict and the new one takes its place
     */
    @Override
    protected Block EvictAndReplace(int index, String tag, Operation op) {
        Block result = GetBlockToEvict(index);
        this.WriteBack = result.Dirty;
        return this.Cache.Sets[result.Index].Reallocate(result.Column, tag, op);
    }

    /**
     * Chooses the block to evict and empties the space
     */
    @Override
    protected void Evict(int index, Operation op) {
        Block result = GetBlockToEvict(index);
        this.WriteBack = result.Dirty;
        this.Cache.Sets[result.Index].Deallocate(result.Column, result.Tag);
    }

    /**
     * Returns the block that needs to be evicted
     * @param index
     * @return
     */
    private Block GetBlockToEvict(int index) {
        this.MinHeap.clear();
        this.MinHeap.addAll(Arrays.asList(this.Cache.Sets[index].Blocks));
        return this.MinHeap.poll();
    }
}
