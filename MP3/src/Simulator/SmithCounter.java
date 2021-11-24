package Simulator;

/**
 * Simple smith counter (saturating counter)
 */
public class SmithCounter {
    public byte Counter;
    private byte _max;
    private final byte _threshold;

    SmithCounter(int bitCount, int init) {
        this._max = (byte) (((byte)Math.pow(2, bitCount))-1);
        this._threshold = (byte)Math.pow(2, bitCount-1);
        this.Counter = init > -1 ? (byte)init: this._threshold;
    }

    public Action Predict() {
        return this.Counter >= this._threshold ? Action.Taken : Action.NotTaken;
    }

    public void Decrease() {
        if (this.Counter > 0){
            this.Counter--;
        }
    }

    public void Increase() {
        if(this.Counter < this._max) {
            this.Counter++;
        }
    }

    public void Update(Action actual) {
        switch(actual) {
            case Taken:
                Increase();
                break;
            default:
                Decrease();
                break;
        }
    }
}
