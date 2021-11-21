package Simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Main engine class. 
 * 1) Parses the arguments
 * 2) Instantiates the simulator
 * 3) Runs the simulation
 */
public class Arguments {
    public File TraceFile;
    private String[] _arguments;
    public ArrayList<Branch> Branches;
    public PredictionPolicy PredictionPolicy;
    public int[] PolicyArguments;
    private boolean _reload;
    
    public Arguments(String[] args) {
        this._arguments = args;
    }

    public Arguments() {
    }

    /**
     * Argument parsing
     * @return
     * @throws Exception
     */
    public Arguments Parse() throws Exception {
        try {
           PredictionPolicyType policyType = PredictionPolicyType.FromString(_arguments[0]);

           if (_arguments.length < 3 ||
               policyType == PredictionPolicyType.Gshare && _arguments.length < 4) {
               throw new Exception("The number of arguments is wrong!");
           } 

           PolicyArguments = new int[_arguments.length - 2];

           for(int i = 1; i < _arguments.length - 1; i++) {
                PolicyArguments[i-1] = Integer.parseUnsignedInt(_arguments[i]);
           }

           PredictionPolicy = GetNewPolicy(policyType);

           this.TraceFile = new File(_arguments[_arguments.length - 1]);

           if (!this.TraceFile.exists() || !this.TraceFile.isFile()) 
                throw new FileNotFoundException();

           this._reload = true;

        } catch(NumberFormatException exception) {
            System.out.println("There was a problem with one of the arguments: " + exception.getMessage());
            throw exception;
        } catch(FileNotFoundException exception) {
            System.out.println("There was a problem with one of the arguments: " + exception.getMessage());
            throw exception;
        }
        return this;
    }

    public Arguments Load(PredictionPolicyType type, int arg1, int arg2, String file) throws Exception {
        try {
           PolicyArguments = arg2 == -1 ? new int[] {arg1} : new int[] {arg1, arg2};

           PredictionPolicy = GetNewPolicy(type);

           this._reload = this.TraceFile == null || !this.TraceFile.getName().equals(new File(file).getName());

           this.TraceFile = new File(file);

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
    
    /**
     * Creates the prediction policy instance
     * @param value
     * @return
     * @throws Exception
     */
    private PredictionPolicy GetNewPolicy(PredictionPolicyType value) throws Exception {
        switch(value) {
            case Smith:
                return new SmithPolicy(this);
            case Bimodal:
                return new BimodalPolicy(this);
            default:
                if (this.PolicyArguments[1] > this.PolicyArguments[0]) {
                    throw new Exception("n cannot be greater than m!");
                }
                return new GsharePolicy(this);
        }
    }

    /**
     * Runs the simulation
     * @return
     * @throws Exception
     */
    public Arguments Run() throws Exception {
        return DoRun();
    }

    private Arguments DoRun() throws Exception {
        if(this._reload) {
            this.Branches = TraceFileReader.GetBranches(this);
        }
        for(Branch b : this.Branches) {
            this.PredictionPolicy.Execute(b);
        }
        return this;
    }
}
