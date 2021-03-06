package Simulator;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Abstract prediction policy
 * Keeps track of the mispredictions
 */
public abstract class PredictionPolicy {

    protected Arguments Arguments;
    protected PredictionPolicyType Type;
    protected List<SmithCounter> BranchPredictor;
    protected abstract boolean Predict(Branch b);
    private int _mispredictions;

    public PredictionPolicy(Arguments arguments) {
        this.Arguments = arguments;
    }

    public void Execute(Branch b) {
        if(!Predict(b)) {
            this._mispredictions++;
        }
    }

    public double GetMispredictionRate() {
        if (this.Arguments.Branches.size() == 0) {
            return 0;
        }

        return this._mispredictions/(double)this.Arguments.Branches.size();
    }

    @Override
    public String toString() {
        DecimalFormat dec = new DecimalFormat("#0.00%");
        StringBuilder builder = new StringBuilder();
        builder.append("number of predictions:\t\t").append(this.Arguments.Branches.size()).append("\n");
        builder.append("number of mispredictions:\t").append(this._mispredictions).append("\n");
        builder.append("misprediction rate:\t\t").append(dec.format(GetMispredictionRate())).append("\n");

        switch(Type) {
            case Bimodal:
            case Gshare:
            builder.append("FINAL ").append(Type.toString().toUpperCase()).append(" CONTENTS\n");
            int index = 0;
            for (SmithCounter smithCounter : BranchPredictor) {
                builder.append(index++).append("\t\t").append(smithCounter.Counter).append("\n");
            }
            builder.setLength(builder.length() - 1);
            break;
            default:
            builder.append("FINAL COUNTER CONTENT:\t\t").append(this.BranchPredictor.get(this.BranchPredictor.size()-1).Counter);
            break;
        }

        return builder.toString();
    }
}
