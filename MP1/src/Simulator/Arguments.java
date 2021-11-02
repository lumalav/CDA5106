package Simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Arguments {
    public File TraceFile;
    private String[] _arguments;
    public Cache Cache;
    public ReplacementPolicy ReplacementPolicy;
    public InclusionProperty InclusionProperty;
    private int _blockSize;
    public ArrayList<Cache> Caches;
    public ArrayList<Operation> Operations;
    public CountDownLatch Latch;
    
    public Arguments(String[] args) {
        this._arguments = args;
    }

    public Arguments() {
    }

    public Arguments Load(int blockSize, int cacheSize, int associativity, 
                                          int cacheSize2, int associativity2, 
                                          int replacementPolicy, int inclusionProperty) throws Exception {
        this._blockSize = blockSize;
        Caches = new ArrayList<>();

        if(cacheSize > 0) {
            Caches.add(new Cache(new CacheArguments(this, 1, blockSize, cacheSize, associativity)));
        }
        if(cacheSize2 > 0) {
            Caches.add(new Cache(new CacheArguments(this, 2, blockSize, cacheSize2, associativity2)));
        }

        if (Caches.isEmpty()) 
            throw new Exception("At least one Cache must be configured!");

        this.Cache = null;

        Cache previous = Caches.get(0);

        for(int k = 1; k < Caches.size(); k++) {
            Cache next = Caches.get(k);
            next.Parent = previous;
            previous.Next = next;
            previous = next;
        }

        while(previous.Parent != null) 
            previous = previous.Parent;

        this.Cache = previous;
        this.ReplacementPolicy = GetNewPolicy(ReplacementPolicyType.FromInteger(replacementPolicy));
        this.InclusionProperty = InclusionProperty.FromInteger(inclusionProperty);

        return this;
    }

    public Arguments Parse() throws Exception {
        try {

           if(this._arguments.length < 1) 
                throw new Exception("This constructor is only used if the arguments are provided in a String[]");
           
           _blockSize = Integer.parseUnsignedInt(_arguments[0]);
           Caches = new ArrayList<>();
           int i, j;
           for (i = 1, j = 1; i < _arguments.length - 3; i=i+2, j++) {
               int size = Integer.parseUnsignedInt(_arguments[i]);
               
               if(size == 0) {
                   i = i+2;
                   break;
               }
            
               int assoc = Integer.parseUnsignedInt(_arguments[i+1]);
               Caches.add(new Cache(new CacheArguments(this, j, _blockSize, size, assoc)));
           }

           if (Caches.isEmpty()) 
                throw new Exception("At least one Cache must be configured!");

           Cache previous = Caches.get(0);

           for(int k = 1; k < Caches.size(); k++) {
               Cache next = Caches.get(k);
               next.Parent = previous;
               previous.Next = next;
               previous = next;
           }

           while(previous.Parent != null) 
                previous = previous.Parent;

           this.Cache = previous;
           this.ReplacementPolicy = GetNewPolicy(ReplacementPolicyType.FromInteger(Integer.parseUnsignedInt(_arguments[i])));
           this.InclusionProperty = InclusionProperty.FromInteger(Integer.parseUnsignedInt(_arguments[++i]));
           this.TraceFile = new File(_arguments[++i]);

           if (!this.TraceFile.exists() || !this.TraceFile.isFile()) 
                throw new FileNotFoundException();

        } catch(NumberFormatException exception) {
            System.out.println("There was a problem with one of the arguments: " + exception.getMessage());
            throw exception;
        } catch(FileNotFoundException exception) {
            System.out.println("There was a problem with one of the arguments: " + exception.getMessage());
            throw exception;
        }
        return this;
    }
    
    private ReplacementPolicy GetNewPolicy(ReplacementPolicyType value) {
        switch(value) {
            case LRU:
                return new LRUPolicy(this);
            case PLRU:
                return new PLRUPolicy(this);
            default:
                this.Latch = new CountDownLatch(1);
                return new OptimalPolicy(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("===== Simulator configuration =====\n");
        builder.append("BLOCKSIZE:\t\t").append(this._blockSize).append("\n");

        for(int i = 0; i < this.Caches.size(); i++) {
            Cache cache = this.Caches.get(i);
            builder.append("L").append(cache.Level).append("_SIZE:\t\t").append(cache.Arguments.CacheSize).append("\n");
            builder.append("L").append(cache.Level).append("_ASSOC:\t\t").append(cache.Arguments.Associativity).append("\n");
        }

        if (this.Caches.size() == 1) {
            builder.append("L2_SIZE:\t\t0\n");
            builder.append("L2_ASSOC:\t\t0\n");
        }

        builder.append("REPLACEMENT POLICY:\t").append(this.ReplacementPolicy.ReplacementPolicyName()).append("\n");
        builder.append("INCLUSION PROPERTY:\t").append(InclusionProperty.ToString(this.InclusionProperty)).append("\n");
        builder.append("trace_file:\t\t").append(this.TraceFile.getName());
        return builder.toString();
    }

    public Arguments Run() throws Exception {
        return DoRun();
    }

    public Arguments Run(boolean print) throws Exception {
        if(print) {
            System.out.println(this);
        }

        return DoRun();
    }

    private Arguments DoRun() throws Exception {
        this.Operations = TraceFileReader.GetOperations(this);

        for(Operation op : this.Operations) {
            this.ReplacementPolicy.Execute(this.Cache, op);
        }
        return this;
    }
}
