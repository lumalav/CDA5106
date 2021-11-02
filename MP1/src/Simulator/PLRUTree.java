package Simulator;

public class PLRUTree {
    Node[] Tree;
    private int Height, Column, NodeIndex;
    private Node[] LastLevel;

    public PLRUTree(int associativity) {
        this.LastLevel = new Node[associativity];
        this.Height = Helpers.Log2(associativity);
        int nodeCount = (int)(Math.pow((double)2, this.Height+1) - 1);
        this.Tree = new Node[nodeCount];
        this.Column = -1;
        Initialize(null, 0, 0, -1);
    }

    private void Initialize(Node parent, int index, int level, int direction) {
        if (index < this.Tree.length) {
            Node node = new Node();
            node.Level = level;
            node.Height = this.Height;
            node.Parent = parent;
            node.Index = ++this.NodeIndex;
            if (node.Level >= this.Height) {
                node.Column = ++this.Column;
                this.LastLevel[node.Column] = node;
            }
            this.Tree[index] = node;
            if (parent != null) {
                if(direction == 0) {
                    parent.Left = node;
                } else {
                    parent.Right = node;
                }
            }

            Initialize(node, 2*index+1, level+1, 0);//left
            Initialize(node, 2*index+2, level+1, 1);//right
        }
    }

    public void Hit(int column) {
        Node node = this.LastLevel[column];
        Node parent = node.Parent;
        while(parent != null) {
            parent.Direction = parent.Right.equals(node);
            node = parent;
            parent = parent.Parent;
        }
    }

    public void PrintFlipBits() {
        PrintFlipBits(this.Tree[0]);
    }

    public int GetLRU() {
        return GetLRU(this.Tree[0]);
    }

    private void PrintFlipBits(Node node) {
        System.out.println(node.toString());
    }

    private int GetLRU(Node node) {
        if(node.Left == null && node.Right == null) {
            return node.Column;
        }
        if(node.Direction) {
            return GetLRU(node.Left);
        }

        return GetLRU(node.Right);
    }
}

class Node {
    public Node Parent;
    public Node Left;
    public Node Right;
    public int Level;
    public boolean Direction; //false left, true right
    public int Column;
    public int Index;
    public int Height;

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        Print(buffer, "", "");
        return buffer.toString();
    }

    private void Print(StringBuilder buffer, String prefix, String childrenPrefix) {

        if(this.Level >= this.Height) {
            return;
        }

        buffer.append(prefix);
        buffer.append(this.Direction ? 1 : 0);
        buffer.append('\n');
        if(this.Left != null) {
            this.Left.Print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
        } 

        if(this.Right != null) {
            this.Right.Print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null || this.getClass() != obj.getClass()) 
            return false;

        Node other = (Node) obj;
        return this.Index == other.Index;
    }
}