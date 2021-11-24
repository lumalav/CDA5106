package Simulator;

import java.util.Collections;

/**
 * Simple smith policy
 */
public class SmithPolicy extends PredictionPolicy {

    private int _b;

    public SmithPolicy(Arguments arguments) {
        super(arguments);
        this.Type = PredictionPolicyType.Smith;
        this._b = this.Arguments.PolicyArguments[0];
    }

    @Override
    protected boolean Predict(Branch b) {

        if (this.BranchPredictor == null) {
            this.BranchPredictor = 
            Collections.nCopies(this.Arguments.Branches.size(), 
            new SmithCounter(_b,-1));
        }

        SmithCounter counter = GetCounter(b);
        Action prediction = counter.Predict();
        counter.Update(b.Actual);
        return prediction == b.Actual;
    }

    private SmithCounter GetCounter(Branch b) {
        return this.BranchPredictor.get(b.Index);
    }

    @Override
    protected String GetName() {
        return this.Type.toString().toUpperCase();
    }
}