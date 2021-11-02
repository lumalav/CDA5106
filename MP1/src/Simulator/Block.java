package Simulator;

public class Block {
    public String Tag;
    public int Column;
    public boolean Dirty;
    public int Counter;
    public String Address;
    public int Index;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Address.hashCode();
        result = prime * result + Tag.hashCode();
        result = prime * result + Index;
        result = prime * result + Column;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null || this.getClass() != obj.getClass()) 
            return false;

        Block other = (Block) obj;
        return this.Index == other.Index && 
                this.Column == other.Column && 
                    this.Tag.equals(other.Tag) && 
                        this.Address.equals(other.Address);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.Tag);

        String temp = this.Tag;
        while(temp.length() < 6) {
            builder.append(" ");
            temp = temp+" ";
        }
        if(this.Dirty) {
            builder.append(" D");
        } else {
            builder.append("  ");
        }
        builder.append("\t");
        return builder.toString();
    }
}