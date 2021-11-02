package Simulator;

public class PLRUPolicy extends ReplacementPolicy {

    PLRUTree[][] Trees;
    public PLRUPolicy(Arguments arguments) {
        super(arguments);
        this.Trees = new PLRUTree[arguments.Caches.size() + 1][];
    }

    private void InitIfNeeded(int index) {
        if (this.Trees[this.Cache.Level] == null) {
            this.Trees[this.Cache.Level] = new PLRUTree[this.Cache.SetCount];
        }

        if (this.Trees[this.Cache.Level][index] == null) {
            this.Trees[this.Cache.Level][index] = new PLRUTree(this.Cache.Arguments.Associativity);
        } 
    }

    @Override
    protected void Hit(int index, int column) {
        InitIfNeeded(index);
        this.Trees[this.Cache.Level][index].Hit(column);
    }

    @Override
    protected void Allocate(int index, int column) {
        InitIfNeeded(index);
        this.Trees[this.Cache.Level][index].Hit(column);
    }

    @Override
    protected String ReplacementPolicyName() {
        return "Pseudo-LRU";
    }

    @Override
    protected Block EvictAndReplace(int index, String tag, Operation op) {
        InitIfNeeded(index);
        int column = this.Trees[this.Cache.Level][index].GetLRU();
        Block result = this.Cache.Sets[index].Blocks[column];
        this.WriteBack = result.Dirty;
        Block r2 = this.Cache.Sets[result.Index].Reallocate(result.Column, tag, op);
        this.Trees[this.Cache.Level][index].Hit(result.Column);
        return r2;
    }

    @Override
    protected void Evict(int index, Operation op) {
        int column = this.Trees[this.Cache.Level][index].GetLRU();
        Block result = this.Cache.Sets[index].Blocks[column];
        this.WriteBack = result.Dirty;
        this.Cache.Sets[result.Index].Deallocate(result.Column, result.Tag);
    }
}