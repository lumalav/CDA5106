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
    protected abstract String GetName();
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

    public StringBuilder GetFinalContents(StringBuilder builder) {
        switch(Type) {
            case Hybrid:
            case Bimodal:
            case Gshare:
            builder.append(Type == PredictionPolicyType.Hybrid ? "" : "\n").append("FINAL ").append(GetName()).append(" CONTENTS\n");
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

        return builder;
    }

    @Override
    public String toString() {
        DecimalFormat dec = new DecimalFormat("#0.00%");
        StringBuilder builder = new StringBuilder();
        builder.append("number of predictions:\t\t").append(this.Arguments.Branches.size()).append("\n");
        builder.append("number of mispredictions:\t").append(this._mispredictions).append("\n");
        builder.append("misprediction rate:\t\t").append(dec.format(GetMispredictionRate())).append("\n");

        GetFinalContents(builder);

        if(Type == PredictionPolicyType.Hybrid) {
            HybridPolicy h = (HybridPolicy)this;
            h.GSharePolicy.GetFinalContents(builder);
            h.BimodalPolicy.GetFinalContents(builder);
        }

        return builder.toString();
    }
}
