package Simulator;

import java.util.*;

/**
 * Represents a set in the cache
 */
public class Set {
    public Block[] Blocks;
    public int Counter;
    public Cache Cache;
    public int Index;
    private Hashtable<String, Block> _blocksTable;

    /**
     * Constructor
     * @param cache
     * @param associativity
     * @param index
     */
    public Set(Cache cache, int associativity, int index) {
        this.Blocks = new Block[associativity];
        this.Cache = cache;
        this.Index = index;
        this._blocksTable = new Hashtable<>(associativity);
        this.Counter = -1;
    }

    /**
     * Checks if the set is full and a eviction needs to happen
     * @return
     */
    public boolean IsFull() {
        return this._blocksTable.size() == this.Blocks.length;
    }

    /**
     * Tries to find a block in the set
     * @param tag
     * @return
     */
    public Tuple<Boolean, Block> TryGetBlock(String tag){
        if(this._blocksTable.containsKey(tag)) {
            return new Tuple<Boolean,Block>(true, this._blocksTable.get(tag));
        }
        return new Tuple<Boolean,Block>(false, null);
    }

    /**
     * Takes a block, evicts it and a new block takes its place. It returns the evicted block
     * @param column
     * @param tag
     * @param op
     * @return
     */
    public Block Reallocate(int column, String tag, Operation op) {
        Block oldB = this.Blocks[column];
        this._blocksTable.remove(oldB.Tag);

        Block newB = new Block();
        newB.Column = oldB.Column;
        newB.Index = oldB.Index;
        newB.Tag = tag;
        newB.Address = op.HexAddress;
        newB.Dirty = op.Operation == ProcessorRequest.Write;
        newB.Counter = ++this.Counter;
        this._blocksTable.put(tag, newB);
        this.Blocks[column] = newB;

        return oldB;
    }

    /**
     * Adds a new block to the set
     * @param tag
     * @param op
     * @return
     * @throws Exception
     */
    public Block Allocate(String tag, Operation op) throws Exception {
        for (int i = 0; i < this.Blocks.length; i++) {
            if(this.Blocks[i] == null) {
                this.Blocks[i] = new Block();
                this.Blocks[i].Tag = tag;
                this.Blocks[i].Address = op.HexAddress;
                this.Blocks[i].Column = i;
                this.Blocks[i].Dirty = op.Operation == ProcessorRequest.Write;
                this.Blocks[i].Index = Index;
                this.Blocks[i].Counter = ++this.Counter;
                this._blocksTable.put(tag, this.Blocks[i]);
                return this.Blocks[i];
            }
        }

        throw new Exception("Could not find an empty block on the set!");
    }

    /**
     * Deletes a block from the set
     * @param index
     * @param tag
     */
    public void Deallocate(int index, String tag) {
        this._blocksTable.remove(tag);
        this.Blocks[index] = null;
    }

    /**
     * Prints the contents of the set
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Set\t").append(this.Index).append(":\t");
        for(Block block : this.Blocks) {
            if (block != null)
                builder.append(block.toString());
        }
        builder.append("\n");
        return builder.toString();
    }
}
