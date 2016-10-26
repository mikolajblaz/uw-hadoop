public class Pair {

    public int a;
    public int b;

    public Pair(int a, int b) {
        if (a < b) {
            this.a = a;
            this.b = b;
        } else {
            this.a = b;
            this.b = a;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return (pair.a == this.a) && (pair.b == this.b);
    }

    @Override
    public int hashCode() {
        return a ^ b;
    }
}
