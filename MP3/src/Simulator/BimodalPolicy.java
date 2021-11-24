package Simulator;

import java.util.ArrayList;

/**
 * Bimodal branch prediction policy
 */
public class BimodalPolicy extends PredictionPolicy {
    private int _mask, _m2;

    public BimodalPolicy(Arguments arguments, boolean mainPolicy) {
        super(arguments);
        this.Type = PredictionPolicyType.Bimodal;
        this._m2 = mainPolicy ? arguments.PolicyArguments[0] : arguments.PolicyArguments[3];
        int size = (int)Math.pow(2, _m2);
        this._mask = size - 1;
        this.BranchPredictor = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            this.BranchPredictor.add(new SmithCounter(3,-1));
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

    protected Action PerformPrediction(Branch b) {
        int index = GetIndex(b.GetParsedAddress());
        return this.BranchPredictor.get(index).Predict();
    }

    @Override
    protected String GetName() {
        return this.Type.toString().toUpperCase();
    }
}
