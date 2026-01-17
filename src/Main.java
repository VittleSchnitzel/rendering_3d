import util.Matrix3;
import util.Triangle3;
import util.Vector3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList<Triangle3> tris = new ArrayList<>();
        tris.add(new Triangle3( new Vector3(100, 100, 100),
                                new Vector3(-100, -100, 100),
                                new Vector3(-100, 100, -100),
                                Color.red));
        tris.add(new Triangle3( new Vector3(100, 100, 100),
                                new Vector3(-100, -100, 100),
                                new Vector3(100, -100, -100),
                                Color.green));
        tris.add(new Triangle3( new Vector3(-100, 100, -100),
                                new Vector3(100, -100, -100),
                                new Vector3(100, 100, 100),
                                Color.yellow));
        tris.add(new Triangle3( new Vector3(-100, 100, -100),
                                new Vector3(100, -100, -100),
                                new Vector3(-100, -100, 100),
                                Color.blue));
        Render3 frame = new Render3();

        Vector3.Transform rotate = (v) -> {
            return frame.getHeadingMatrix().multiply(frame.getPitchMatrix()).transform(v);
        };
        Vector3.Transform translate = (v) -> {
            return v.add(frame.getWidth()/2, frame.getHeight()/2, 0);
        };

        Render3.PaintRoutine routine = (g) -> {
            frame.paintRoutineClear().run(g);

            BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
            double[] zBuffer = new double[img.getWidth() * img.getHeight()];
            Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

            for (Triangle3 t : tris) {
                Vector3 v1 = translate.run(rotate.run(t.v1));
                Vector3 v2 = translate.run(rotate.run(t.v2));
                Vector3 v3 = translate.run(rotate.run(t.v3));

                int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                int maxX = (int) Math.min(img.getWidth() - 1,
                        Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                int maxY = (int) Math.min(img.getHeight() - 1,
                        Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);


                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                        double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                        double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                        if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                            double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                            int zIndex = y * img.getWidth() + x;

                            if (zBuffer[zIndex] < depth) {
                                img.setRGB(x, y, t.color.getRGB());
                                zBuffer[zIndex] = depth;
                            }
                        }
                    }
                }
            }
            g.drawImage(img, 0, 0, null);
            //frame.paintRoutineTransformTriangle(tris, rotate).run(g);
        };
        frame.addPaintRoutine(routine);

    }
}
