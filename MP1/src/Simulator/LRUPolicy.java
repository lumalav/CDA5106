package Simulator;

import java.util.*;

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

    @Override
    protected Block EvictAndReplace(int index, String tag, Operation op) {
        this.MinHeap.clear();
        this.MinHeap.addAll(Arrays.asList(this.Cache.Sets[index].Blocks));
        Block result = this.MinHeap.poll();
        this.WriteBack = result.Dirty;
        return this.Cache.Sets[result.Index].Reallocate(result.Column, tag, op);
    }

    @Override
    protected void Evict(int index, Operation op) {
        this.MinHeap.clear();
        this.MinHeap.addAll(Arrays.asList(this.Cache.Sets[index].Blocks));
        Block result = this.MinHeap.poll();
        this.WriteBack = result.Dirty;
        this.Cache.Sets[result.Index].Deallocate(result.Column, result.Tag);
    }
}
