package Simulator;

/**
 * This class represents the cache. 
 * It takes the arguments and instantiates and array of sets.
 * It also holds the information on how to extract the Index, the tag, and the offset bits from a particular address
 */
public class Cache {
    private int _tagBits, _indexBits, _offsetBits;
    public int SetCount, Reads, ReadMisses, WriteBacks, Writes, WriteMisses, ExtraWork; 
    public Set[] Sets;
    public Cache Parent = null;
    public Cache Next = null;
    public int Level;
    public CacheArguments Arguments;

    /**
     * Constructor
     * @param arguments
     */
    public Cache(CacheArguments arguments) {
        this.Arguments = arguments;
        this.SetCount = arguments.CacheSize/(arguments.BlockSize*arguments.Associativity);
        this._indexBits = Helpers.Log2(this.SetCount);
        this._offsetBits = Helpers.Log2(arguments.BlockSize);
        this._tagBits = 32-this._indexBits-this._offsetBits;
        this.Sets = new Set[this.SetCount];
        this.Level = arguments.Level;
        for(int i = 0; i < this.SetCount; i++) {
            this.Sets[i] = new Set(this, arguments.Associativity, i);
        }
    }

    /**
     * Returns the tag from the given hex address operation
     * @param operation 
     * @return
     */
    public String GetTag(Operation operation) {
        String binary = operation.GetBinaryAddress();
        String tag = binary.substring(0, this._tagBits);
        return Integer.toString(Integer.parseInt(tag, 2), 16);
    }

    /**
     * Returns the indes from the given hex address operation
     * @param operation
     * @return
     */
    public int GetIndex(Operation operation) {
        String binary = operation.GetBinaryAddress();
        String index = binary.substring(this._tagBits, this._tagBits + this._indexBits);
        return Integer.parseInt(index, 2);
    }

    /**
     * Returns the miss rate of the cache
     * @return
     */
    public double GetMissRate() {

        int all = this.Writes + this.Reads;

        if(all == 0 || this.Reads == 0) {
            return 0;
        }

        if (this.Level < 2) {
            return (this.WriteMisses + this.ReadMisses) / (double) all;
        }

        return this.ReadMisses / (double) this.Reads;
    }


    /**
     * Returns the memory traffic of the cache
     * @return
     */
    public int GetMemoryTraffic() {
        return this.ReadMisses + this.WriteMisses + this.WriteBacks;
    }

    /**
     * Returns the average access time of the cache.
     * @param value cacti value is required
     * @return
     */
    public double GetAAT(double value) {
        double den = this.Reads + this.Writes;
        if(den == 0) {
            return 0;
        }

        double num = den * value + (this.ReadMisses + this.WriteMisses) * 100;

        return num / den;
    }

    /**
     * Prints the contents of the cache
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("===== L").append(this.Level).append(" contents =====\n");
        for(Set set: this.Sets) {
            builder.append(set.toString());
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}