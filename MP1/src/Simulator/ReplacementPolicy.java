package Simulator;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public abstract class ReplacementPolicy {

    protected OperationsPreprocessor OperationsPreprocessorThread;
    protected Arguments Arguments;
    protected Cache Cache;
    protected boolean WriteBack;
    protected CountDownLatch Latch;
    protected ArrayList<Hashtable<Operation, Tuple<Integer, String>>> OperationsTable;
    
    public ReplacementPolicy(Arguments arguments) {
        this.Arguments = arguments;
    }

    protected abstract void Hit(int index, int column);
    protected abstract void Allocate(int index, int column);
    protected abstract Block EvictAndReplace(int index, String tag, Operation op);
    protected abstract void Evict(int index, Operation op);
    protected abstract String ReplacementPolicyName();

    public Tuple<Execution, Block> Execute(Cache cache, Operation op) throws Exception {

        this.Cache = cache;

        if(this.Cache == null) {
            return null;
        }

        CountOperation(op, ArithmeticOperation.Add);

        int index = this.Cache.GetIndex(op);
        String tag = this.Cache.GetTag(op);

        Tuple<Execution, Block> r1 = Execute(op, tag, index);

        if(r1.Item1 == Execution.Hit) {
            return r1;
        }

        CountMiss(op, ArithmeticOperation.Add);

        //save the address and operation for future use
        String originalAddress = op.HexAddress;
        ProcessorRequest originalOperation = op.Operation;

        if(this.WriteBack) { //evicted and dirty
            CountWriteBack(op, ArithmeticOperation.Add);
            this.WriteBack = false;
            if (this.Cache.Next != null) {
                //write evicted address in the next cache
                op = op.OverrideOperation(op, r1.Item2.Address, ProcessorRequest.Write);
                Tuple<Execution, Block> r2 = Execute(this.Cache.Next, op);

                this.Cache = this.Cache.Parent;

                if(r2.Item1 == Execution.Evicted && this.Cache.Arguments.Arguments.InclusionProperty == InclusionProperty.Inclusive) {
                    op = op.OverrideOperation(op, r2.Item2.Address, ProcessorRequest.Write);
                    int parentIndex = this.Cache.GetIndex(op);
                    this.Evict(parentIndex, op);
                    this.Cache.ExtraWork++;
                    this.Cache.Next.WriteMisses--;

                    if(originalOperation == ProcessorRequest.Read) {
                        this.Cache.Next.ReadMisses++;
                        this.Cache.Next.ReadMisses++;
                        this.Cache.ReadMisses++;
                        this.Cache.Next.Reads++;
                    } 
                    else 
                    {
                        this.Cache.WriteBacks--;
                        this.Cache.Next.Writes--;
                    }
                }
            }
        }

        if (this.Cache.Next == null) {
            return r1;
        }

        //we fail to find the original address in the current cache
        //continue with the next one
        op = op.OverrideOperation(op, originalAddress, ProcessorRequest.Read);

        return Execute(this.Cache.Next, op);
    }


    public void CountWriteBack(Operation op, ArithmeticOperation a) {
        if(a == ArithmeticOperation.Add) {
            this.Cache.WriteBacks++;
            return;
        }

        this.Cache.WriteBacks--;
    } 

    private void CountOperation(Operation op, ArithmeticOperation a) {
        if(a == ArithmeticOperation.Add) {
            if(op.Operation == ProcessorRequest.Read) {
                this.Cache.Reads++;
                return;
            } 
            this.Cache.Writes++;
        } else {
            if(op.Operation == ProcessorRequest.Read) {
                this.Cache.Reads--;
                return;
            } 
            this.Cache.Writes--;
        }
    }

    private void CountMiss(Operation op, ArithmeticOperation a) {
        if(a == ArithmeticOperation.Add) {
            if(op.Operation == ProcessorRequest.Read) {
                this.Cache.ReadMisses++;
                return;
            } 

            this.Cache.WriteMisses++;
        } else {
            if(op.Operation == ProcessorRequest.Read) {
                this.Cache.ReadMisses--;
                return;
            } 

            this.Cache.WriteMisses--;
        }
    }

    private Tuple<Execution, Block> Execute(Operation op, String tag, int index) throws Exception {

        Tuple<Boolean, Block> block = this.Cache.Sets[index].TryGetBlock(tag);
        Tuple<Execution, Block> result;

        if (block.Item1) {
            if(op.Operation == ProcessorRequest.Write) {
                this.Cache.Sets[index].Blocks[block.Item2.Column].Dirty = true;
            }
            this.Hit(index, block.Item2.Column);
            result = new Tuple<Execution,Block>(Execution.Hit, block.Item2);
        } else if(!this.Cache.Sets[index].IsFull()) {          
            Block allocated = this.Cache.Sets[index].Allocate(tag, op);
            this.Allocate(index, allocated.Column);
            result = new Tuple<Execution,Block>(Execution.Allocated, allocated);
        } else {
            Block evicted = this.EvictAndReplace(index, tag, op);
            result = new Tuple<Execution,Block>(Execution.Evicted, evicted);
        }

        return result;
    }

    public int GetMemoryTraffic() {
        Cache firstCache = this.Arguments.Cache;

        if(firstCache.Next == null) {
            return firstCache.GetMemoryTraffic();
        }

        Cache next = firstCache.Next;
        while(next.Next != null) {
            next = next.Next;
        }

        int memoryTraffic = next.GetMemoryTraffic();

        return firstCache.Arguments.Arguments.InclusionProperty == InclusionProperty.NonInclusive 
                ? memoryTraffic : memoryTraffic + firstCache.ExtraWork;
    }
}
