package Simulator;

public class MP2 {

    private static Arguments _arguments;
    
    /**
     * Main class
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        _arguments = new Arguments(args)
                           .Parse()
                           .Run();

        System.out.println(_arguments.PredictionPolicy);
    }
}
