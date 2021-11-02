package Simulator;

import java.math.BigInteger;

/**
 * Represents an operation from the trace
 */
public class Operation {
    public ProcessorRequest Operation;
    public String HexAddress;
    private String _binAddress;
    public int Index;

    /**
     * Constructor
     */
    public Operation(ProcessorRequest processorRequest, String address, int index) {
        this.Operation = processorRequest;
        this.HexAddress = address;
        this.Index = index;
    }

    /**
     * Lazy calculation of the binary address
     * @return
     */
    public String GetBinaryAddress() {
        if (this._binAddress == null || this._binAddress.isEmpty() || this._binAddress.length() < 1) {
            String value = new BigInteger(this.HexAddress, 16).toString(2);
            this._binAddress = String.format("%32s", value).replace(" ", "0");
        }

        return this._binAddress;
    }
    
    /**
     * Changes the type of operation and the address
     * @param op
     * @param newAddress
     * @param processorRequest
     * @return
     */
    public Operation OverrideOperation(Operation op, String newAddress, ProcessorRequest processorRequest) {
        this.HexAddress = newAddress;
        this._binAddress = null;
        op.Operation = processorRequest;
        return op;
    }

    /**
     * Used to store Operations in hash structures
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Index;
        return result;
    }

    /**
     * Checks if two operations are the same. Based on the position of the operation in the trace file
     */
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