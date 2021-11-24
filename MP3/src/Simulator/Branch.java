package Simulator;

/**
 * Represents a branch
 * Contains the address and the action 
 */
public class Branch {
    public final Action Actual;
    public final String HexAddress;
    private int _parsedAddress;
    private boolean _parsedHex;
    public final int Index;

    public Branch(String address, Action action, int index) {
        this.Actual = action;
        this.HexAddress = address;
        this.Index = index;
    }

    public int GetParsedAddress() {
        if (!this._parsedHex) {
            this._parsedAddress = Integer.parseInt(this.HexAddress, 16);
            this._parsedHex = true;
        }

        return this._parsedAddress;
    }
}