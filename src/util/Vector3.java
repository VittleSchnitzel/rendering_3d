package util;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public interface Transform {
        Vector3 run(Vector3 in);
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 scale(double m) {
        return new Vector3(x * m, y * m, z * m);
    }

    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 unit() {
        return this.scale(1/this.mag());
    }

    public Vector3 neg() {
        return new Vector3(-x, -y, -z);
    }

    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public Vector3 norm(Vector3 other) {
        Vector3 cross = this.cross(other);
        return cross.unit();
    }
}
