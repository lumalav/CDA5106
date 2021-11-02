package Simulator;

public class CacheArguments {
    public int Level, BlockSize, CacheSize, Associativity;
    public Arguments Arguments;
    public CacheArguments(Arguments arguments, int level,
                          int blockSize, 
                          int cacheSize, 
                          int associativity) {
                              this.Arguments = arguments;
                              this.Level = level;
                              this.BlockSize = blockSize;
                              this.CacheSize = cacheSize;
                              this.Associativity = associativity;
                          }
}
