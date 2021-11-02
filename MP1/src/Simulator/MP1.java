package Simulator;
import java.text.DecimalFormat;

/**
 * Main class. Takes the arguments, calls the corresponding functions and prints the results
 */
public class MP1 {

    private static Arguments _arguments;
    public static void main(String[] args) throws Exception {
        _arguments = new Arguments(args)
                           .Parse()
                           .Run();
        PrintResults();
    }

    /**
     * Prints the results from the simulation
     */
    private static void PrintResults() {

        DecimalFormat dec = new DecimalFormat("#0.000000");

        System.out.println(_arguments.toString());
        
        for(Cache cache : _arguments.Caches) {
            System.out.println(cache.toString());
        }

        StringBuilder builder = new StringBuilder();

        builder.append("===== Simulation results (raw) =====\n");

        char currentLetter = 'a';
        for(Cache cache : _arguments.Caches) {
            builder.append(cache.Level == 1 ? currentLetter : ++currentLetter).append(". number of L").append(cache.Level).append(" reads:\t\t").append(cache.Reads).append("\n");
            builder.append(++currentLetter).append(". number of L").append(cache.Level).append(" read misses:\t").append(cache.ReadMisses).append("\n");
            builder.append(++currentLetter).append(". number of L").append(cache.Level).append(" writes:\t\t").append(cache.Writes).append("\n");
            builder.append(++currentLetter).append(". number of L").append(cache.Level).append(" write misses:\t").append(cache.WriteMisses).append("\n");
            builder.append(++currentLetter).append(". L").append(cache.Level).append(" miss rate:\t\t").append(dec.format(cache.GetMissRate())).append("\n");
            builder.append(++currentLetter).append(". number of L").append(cache.Level).append(" writebacks:\t").append(cache.WriteBacks).append("\n");
        }

        if (_arguments.Caches.size() == 1) {
            builder.append(++currentLetter).append(". number of L2 reads:\t\t0\n");
            builder.append(++currentLetter).append(". number of L2 read misses:\t0\n");
            builder.append(++currentLetter).append(". number of L2 writes:\t\t0\n");
            builder.append(++currentLetter).append(". number of L2 write misses:\t0\n");
            builder.append(++currentLetter).append(". L2 miss rate:\t\t0\n");
            builder.append(++currentLetter).append(". number of L2 writebacks:\t0\n");
        }

        builder.append(++currentLetter).append(". total memory traffic:\t").append(_arguments.ReplacementPolicy.GetMemoryTraffic()).append("\n");

        builder.setLength(builder.length() - 1);

        System.out.println(builder.toString());
    }
}
