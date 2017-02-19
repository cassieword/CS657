public class Vertex {
    final private String id;
    final private String name;
    final private boolean isBlock;
    final private int i;
    final private int j;

    public Vertex(int i, int j, boolean isBlock) {
        this.i = i;
        this.j = j;
        this.id = "Node_" + i + "_" + j;
        this.name = "Node_" + i + "_" + j;
        this.isBlock = isBlock;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getIsBlock() {
        return isBlock;
    }
    public int getI() {
        return i;
    }
    public int getJ() {
        return j;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}