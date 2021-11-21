package Simulator;

import java.util.ArrayList;

/**
 * Gshare branch prediction policy
 */
public class GsharePolicy extends PredictionPolicy {

    private int _mask,_gbhr;

    public GsharePolicy(Arguments arguments) {
        super(arguments);
        this.Type = PredictionPolicyType.Gshare;
        int size = (int)Math.pow(2, arguments.PolicyArguments[0]);
        this._mask = size - 1;
        this.BranchPredictor = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            this.BranchPredictor.add(new SmithCounter(3));
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
        if (this.Arguments.PolicyArguments[1] < 1) {
            int index = address >> 2;
            index = index & this._mask;
            return index;
        }

        int sub = GetLastNBits(address, this.Arguments.PolicyArguments[0] + 2) >> 2;
        int a = sub >> this.Arguments.PolicyArguments[1];
        int b = GetLastNBits(sub, this.Arguments.PolicyArguments[1]) ^ this._gbhr;
        return a << this.Arguments.PolicyArguments[1] | b;
    }

    /**
     * Updates the global history register based on the actual outcome
     * @param value
     */
    private void UpdateGhbr(int value){
        if(this.Arguments.PolicyArguments[1] > 0) {
            this._gbhr = this._gbhr >> 1;
            this._gbhr = this._gbhr | (value << (this.Arguments.PolicyArguments[1] - 1));
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
}
