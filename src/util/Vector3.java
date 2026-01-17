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
}
