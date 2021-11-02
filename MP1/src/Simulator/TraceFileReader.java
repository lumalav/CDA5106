package Simulator;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TraceFileReader {
    
    public static ArrayList<Operation> GetOperations(Arguments arguments) throws IOException {
        BufferedReader br = null;
        Scanner sc = null;
        ArrayList<Operation> operations = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(arguments.TraceFile), 1000 * 8192);
            String thisLine;
            int index = -1;
            while ((thisLine = br.readLine()) != null) {

                if (thisLine == null || thisLine.isEmpty() || thisLine.length() < 1) 
                    continue;

                sc = new Scanner(thisLine);
                
                ProcessorRequest processorRequest = null;
                String hexAddress = null;
                while (sc.hasNextLine()) {
                    processorRequest = ProcessorRequest.FromString(sc.next());
                    hexAddress = sc.next();
                }

                operations.add(new Operation(processorRequest, hexAddress, ++index));
            }
        } catch (Exception exception) {
            System.out.println("Something happened while reading the trace file");
            exception.printStackTrace();
            throw exception;
        } finally {
            if (br != null) br.close();          
            if (sc != null) sc.close();
            if (arguments.Latch != null) arguments.Latch.countDown();
        }

        return operations;
    }
}
