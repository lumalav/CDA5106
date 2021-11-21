package Simulator;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/***
 * Reads the file and retrieves the branches and their outcomes
 */
public class TraceFileReader {
    
    public static ArrayList<Branch> GetBranches(Arguments arguments) throws IOException {
        BufferedReader br = null;
        Scanner sc = null;
        ArrayList<Branch> branches = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(arguments.TraceFile), 1000 * 8192);
            String thisLine;
            int index = -1;
            while ((thisLine = br.readLine()) != null) {

                if (thisLine == null || thisLine.isEmpty() || thisLine.length() < 1) 
                    continue;

                sc = new Scanner(thisLine);
                String hexAddress = null;
                Action action = null;

                while (sc.hasNextLine()) {
                    hexAddress = sc.next();
                    action = Action.FromString(sc.next());
                }

                branches.add(new Branch(hexAddress, action, ++index));
            }
        } catch (Exception exception) {
            System.out.println("Something happened while reading the trace file");
            exception.printStackTrace();
            throw exception;
        } finally {
            if (br != null) br.close();          
            if (sc != null) sc.close();
        }

        return branches;
    }
}
