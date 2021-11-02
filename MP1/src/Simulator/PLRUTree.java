package Simulator;

/**
 * Represents a PLRU tree.
 * Takes a specific set of the cache and keeps track of the block usage
 */
public class PLRUTree {
    Node[] Tree;
    private int Height, Column, NodeIndex;
    private Node[] LastLevel;

    /**
     * Constructor
     * @param associativity
     */
    public PLRUTree(int associativity) {
        this.LastLevel = new Node[associativity];
        this.Height = Helpers.Log2(associativity);
        int nodeCount = (int)(Math.pow((double)2, this.Height+1) - 1);
        this.Tree = new Node[nodeCount];
        this.Column = -1;
        Initialize(null, 0, 0, -1);
    }

    /**
     * Initializes the PLRU tree
     * @param parent
     * @param index
     * @param level
     * @param direction
     */
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

    /**
     * Bits need to be flipped everytime a hit happens
     * @param column
     */
    public void Hit(int column) {
        Node node = this.LastLevel[column];
        Node parent = node.Parent;
        while(parent != null) {
            parent.Direction = parent.Right.equals(node);
            node = parent;
            parent = parent.Parent;
        }
    }

    /**
     * Prints the contents of the tree
     */
    public void PrintFlipBits() {
        System.out.println(this.Tree[0].toString());
    }

    /**
     * Traverses the PLRU tree and returns the LRU
     * @return
     */
    public int GetLRU() {
        return GetLRU(this.Tree[0]);
    }

    /**
     * Recursively traverses the tree in the opposite direction to get the LRU
     * @param node
     * @return
     */
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

/**
 * Represents a node of the PLRU Tree
 */
class Node {
    public Node Parent;
    public Node Left;
    public Node Right;
    public int Level;
    public boolean Direction; //false left, true right
    public int Column;
    public int Index;
    public int Height;

    /**
     * Prints the contents of the Tree recursively
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        Print(buffer, "", "");
        return buffer.toString();
    }

    /**
     * Prints the contents of a node and their children
     */
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

    /**
     * Checks if two nodes are the same, based on position and not contents
     */
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