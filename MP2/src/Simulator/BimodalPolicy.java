package Simulator;

import java.util.ArrayList;

/**
 * Bimodal branch prediction policy
 */
public class BimodalPolicy extends PredictionPolicy {
    private int _mask;

    public BimodalPolicy(Arguments arguments) {
        super(arguments);
        this.Type = PredictionPolicyType.Bimodal;
        int size = (int)Math.pow(2, arguments.PolicyArguments[0]);
        this._mask = size - 1;
        this.BranchPredictor = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            this.BranchPredictor.add(new SmithCounter(3));
        }
    }

    /**
     * retrieves the index from the history table
     * @param address
     * @return
     */
    private int GetIndex(int address) {
        int index = address >> 2;
        index = index & this._mask;
        return index;
    }

    /**
     * Makes a prediction based on the information of the address 
     * that is in the history table
     */
    @Override
    protected boolean Predict(Branch b) {
        int index = GetIndex(b.GetParsedAddress());
        Action prediction = this.BranchPredictor.get(index).Predict();
        this.BranchPredictor.get(index).Update(b.Actual);
        return prediction == b.Actual;
    }
}
