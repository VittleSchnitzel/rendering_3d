package util;

import java.awt.*;

public class Triangle3 {
    public Vector3 v1;
    public Vector3 v2;
    public Vector3 v3;
    public Color color;

    public Triangle3(Vector3 a, Vector3 b, Vector3 c, Color color) {
        this.v1 = a;
        this.v2 = b;
        this.v3 = c;
        this.color = color;
    }
}
