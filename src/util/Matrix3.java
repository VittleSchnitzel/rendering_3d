package util;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;

public class Matrix3 {
    double[] values;
    public Matrix3(double[] values) {
        this.values = values;
    }
    public Matrix3 multiply(Matrix3 other) {
        double[] result = new double[9];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                    result[row * 3 + col] +=
                            this.values[row * 3 + i] * other.values[i * 3 + col];
                }
            }
        }
        return new Matrix3(result);
    }
    public Vector3 transform(Vector3 in) {
        return new Vector3(
                in.x * values[0] + in.y * values[3] + in.z * values[6],
                in.x * values[1] + in.y * values[4] + in.z * values[7],
                in.x * values[2] + in.y * values[5] + in.z * values[8]
        );
    }

    public static Matrix3 getRotateXY(double theta) {
        return new Matrix3( new double[] {
                Math.cos(theta),    -Math.sin(theta),   0,
                Math.sin(theta),    Math.cos(theta),    0,
                0,                  0,                  1
        });
    }
    public static Matrix3 getRotateYZ(double theta) {
        return new Matrix3( new double[] {
                1,  0,                  0,
                0,  Math.cos(theta),    Math.sin(theta),
                0,  -Math.sin(theta),   Math.cos(theta)
        });
    }
    public static Matrix3 getRotateXZ(double theta) {
        return new Matrix3( new double[] {
                Math.cos(theta),    0,  -Math.sin(theta),
                0,                  1,  0,
                Math.sin(theta),    0,  Math.cos(theta)
        });
    }
    public static Matrix3 id() {
        return new Matrix3(new double[] {1, 0, 0, 0, 1, 0, 0, 0, 1});
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[");
        for (double i : values) {
            s.append(i);
            s.append(",");
        }
        s.append("]");
        return s.toString();
    }
}
