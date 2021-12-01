package Simulator;

import java.util.ArrayList;

/**
 * Gshare branch prediction policy
 */
public class GsharePolicy extends PredictionPolicy {

    private int _mask,_gbhr,_m1,_n;

    public GsharePolicy(Arguments arguments, boolean mainPolicy) {
        super(arguments);
        this.Type = PredictionPolicyType.Gshare;

        if(mainPolicy) {
            this._m1 = arguments.PolicyArguments[0];
            this._n = arguments.PolicyArguments[1];
        } else{
            this._m1 = arguments.PolicyArguments[1];
            this._n = arguments.PolicyArguments[2];
        }

        int size = (int)Math.pow(2, this._m1);
        this._mask = size - 1;
        this.BranchPredictor = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            this.BranchPredictor.add(new SmithCounter(3,-1));
        }
    }

    /**
     * Gets the last n bits from a given unsigned number
     * @param number
     * @param bits
     * @return
     */
    private int GetLastNBits(int number, int bits) {
        int mask = ((1 << bits) -1);
        return number & mask;
    }

    /**
     * Retrieves the index from the history table using the address
     * and the global history register
     * @param address
     * @return
     */
    private int GetIndex(int address) {
        if (this._n < 1) {
            int index = address >> 2;
            index = index & this._mask;
            return index;
        }

        int sub = GetLastNBits(address, this._m1 + 2) >> 2;
        int a = sub >> this._n;
        int b = GetLastNBits(sub, this._n) ^ this._gbhr;
        return a << this._n | b;
    }

    /**
     * Updates the global history register based on the actual outcome
     * @param value
     */
    public void UpdateGhbr(int value){
        if(this._n > 0) {
            this._gbhr = this._gbhr >> 1;
            this._gbhr = this._gbhr | (value << (this._n - 1));
        }
    }

    /***
     * Makes a prediction based on the information of the address 
     * that is contained on the history table 
     * and the global history register
     */
    @Override
    protected boolean Predict(Branch b) {
        int index = GetIndex(b.GetParsedAddress());
        Action prediction = this.BranchPredictor.get(index).Predict();
        this.BranchPredictor.get(index).Update(b.Actual);
        UpdateGhbr(b.Actual == Action.Taken ? 1: 0);
        return prediction == b.Actual;
    }

    protected Action PerformPrediction(Branch b) {
        int index = GetIndex(b.GetParsedAddress());
        return this.BranchPredictor.get(index).Predict();
    }

    @Override
    protected String GetName() {
        return this.Type.toString().toUpperCase();
    }
}
