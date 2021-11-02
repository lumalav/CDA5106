package Simulator;

import java.math.BigInteger;

public class Operation {
    public ProcessorRequest Operation;
    public String HexAddress;
    private String _binAddress;
    public int Index;

    public Operation(ProcessorRequest processorRequest, String address, int index) {
        this.Operation = processorRequest;
        this.HexAddress = address;
        this.Index = index;
    }

    public String GetBinaryAddress() {
        if (this._binAddress == null || this._binAddress.isEmpty() || this._binAddress.length() < 1) {
            String value = new BigInteger(this.HexAddress, 16).toString(2);
            this._binAddress = String.format("%32s", value).replace(" ", "0");
        }

        return this._binAddress;
    }
    
    public Operation OverrideOperation(Operation op, String newAddress, ProcessorRequest processorRequest) {
        op.SetAddress(newAddress);
        op.Operation = processorRequest;
        return op;
    }

    private void SetAddress(String address) {
        this.HexAddress = address;
        this._binAddress = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Index;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null || this.getClass() != obj.getClass()) 
            return false;

        Operation other = (Operation) obj;
        return this.Index == other.Index;
    }
}