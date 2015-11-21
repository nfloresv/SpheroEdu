package cl.flores.nicolas.spheroedu.Utils;

public class Vector {
    private float x;
    private float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector other) {
        float x = this.x + other.x;
        float y = this.y + other.y;
        return new Vector(x, y);
    }

    public Vector subtract(Vector other) {
        Vector negOther = other.pond(-1f);
        return this.add(negOther);
    }

    public double module() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public Vector pond(float lambda) {
        float x = this.x * lambda;
        float y = this.y * lambda;
        return new Vector(x, y);
    }

    public float prod(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    public double angle(Vector other) {
        double angle = Math.acos(this.prod(other) / (this.module() * other.module()));
        return Math.toDegrees(angle);
    }

    public Vector normalize() {
        double lambda = 1 / this.module();
        return this.pond((float) lambda);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector (" + x + ", " + y + ')';
    }
}
