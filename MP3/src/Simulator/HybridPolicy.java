package Simulator;

import java.util.ArrayList;

public class HybridPolicy extends PredictionPolicy {

    private BimodalPolicy _bPolicy;
    private GsharePolicy _gPolicy;
    private int _k, _mask;

    public HybridPolicy(Arguments arguments) {
        super(arguments);
        this.Type = PredictionPolicyType.Hybrid;
        this._k = arguments.PolicyArguments[0];
        this._bPolicy = new BimodalPolicy(arguments, false);
        this._gPolicy = new GsharePolicy(arguments, false);
        int size = (int)Math.pow(2, this._k);
        this._mask = size - 1;
        this.BranchPredictor = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            this.BranchPredictor.add(new SmithCounter(2,1));
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

    @Override
    protected boolean Predict(Branch b) {
        Action bPrediction = this._bPolicy.PerformPrediction(b);
        Action gPrediction = this._gPolicy.PerformPrediction(b);
        int index = GetIndex(b.GetParsedAddress());
        SmithCounter c = this.BranchPredictor.get(index);
        boolean result;
        boolean bCorrect = false,gCorrect = false;
        if(c.Counter < 2) {
            result = bPrediction == b.Actual;
            bCorrect = result;
            this._gPolicy.UpdateGhbr(b.Actual == Action.Taken ? 1 : 0);
            this._bPolicy.Predict(b);
        } else {
            result = gPrediction == b.Actual;
            gCorrect = result;
            this._gPolicy.Predict(b);
        }

        if(bCorrect && !gCorrect) {
            c.Decrease();
        }

        if(gCorrect && !bCorrect) {
            c.Increase();
        }

        return result;
    }

    @Override
    protected String GetName() {
        return "CHOOSER";
    }
}
