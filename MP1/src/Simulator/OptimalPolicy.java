package Simulator;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class OptimalPolicy extends ReplacementPolicy {

    public OptimalPolicy(Arguments arguments) {
        super(arguments);
        this.Latch = new CountDownLatch(1);
        this.OperationsPreprocessorThread = new OperationsPreprocessor(this.Latch, arguments);
        Thread thread = new Thread(this.OperationsPreprocessorThread);
        thread.start();
    }

    @Override
    protected void Hit(int index, int column) {
    }

    @Override
    protected void Allocate(int index, int column) {
    }

    @Override
    protected String ReplacementPolicyName() {
        return "Optimal";
    }

    private Block GetBlockToEvict(int index, Operation op) {
        try {
            this.Latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (this.OperationsTable == null) {
            this.OperationsTable = this.OperationsPreprocessorThread.OperationsTable;
        }

        HashSet<Block> hash = new HashSet<>();
        for(int i = op.Index + 1; i < this.Arguments.Operations.size() && hash.size() < this.Cache.Arguments.Associativity - 1; i++) {
            Operation next = this.Arguments.Operations.get(i);

            Hashtable<Operation, Tuple<Integer, String>> table = this.OperationsTable.get(this.Cache.Level - 1);

            Tuple<Integer, String> found = table.get(next);

            if(index == found.Item1) {
                Tuple<Boolean, Block> block = this.Cache.Sets[index].TryGetBlock(found.Item2);

                if (block.Item1) {
                    hash.add(block.Item2);
                }
            }
        }
        
        if (hash.size() > 0) {
            for(Block result : this.Cache.Sets[index].Blocks) {
                if(hash.add(result)) {
                    this.WriteBack = result.Dirty;
                    return result;
                }
            }
        }

        //if nothing found on the future. Evict first one
        Block result = this.Cache.Sets[index].Blocks[0];
        this.WriteBack = result.Dirty;
        return result;
    }

    @Override
    protected Block EvictAndReplace(int index, String tag, Operation op) {
        Block result = GetBlockToEvict(index, op);
        this.WriteBack = result.Dirty;
        return this.Cache.Sets[result.Index].Reallocate(result.Column, tag, op);
    }

    @Override
    protected void Evict(int index, Operation op) {
        Block result = GetBlockToEvict(index, op);
        this.WriteBack = result.Dirty;
        this.Cache.Sets[result.Index].Deallocate(index, result.Tag);
    }
}

class OperationsPreprocessor implements Runnable {

    public ArrayList<Hashtable<Operation, Tuple<Integer, String>>> OperationsTable;
    private CountDownLatch Latch, Latch2;
    private Arguments Arguments;
    public OperationsPreprocessor(CountDownLatch latch, Arguments arguments) {
        this.Latch = latch;
        this.Latch2 = arguments.Latch;
        this.Arguments = arguments;
    }

    @Override
    public void run() {
        try {
            PrepareOperations();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.Latch.countDown();
    }

    private void PrepareOperations() throws InterruptedException {
        this.OperationsTable = new ArrayList<>(this.Arguments.Caches.size());
        for(int i = 0; i < this.Arguments.Caches.size(); i++) {

            Cache cache = this.Arguments.Caches.get(i);

            Hashtable<Operation, Tuple<Integer, String>> table = new Hashtable<>();

            this.Latch2.await();

            for(Operation op : this.Arguments.Operations) {
                
                int index = cache.GetIndex(op);
                String tag = cache.GetTag(op);

                table.put(op, new Tuple<Integer,String>(index, tag));
            }

            this.OperationsTable.add(table);
        }

        this.Latch.countDown();
    }
}