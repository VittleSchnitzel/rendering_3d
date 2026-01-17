import util.Matrix3;
import util.Triangle3;
import util.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList tris = new ArrayList<>();
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
        Render3.PaintRoutine routine = (g) -> {
            frame.paintRoutineClear().run(g);
            frame.paintRoutineTransformTriangle(tris, rotate).run(g);
        };
        frame.addPaintRoutine(routine);

    }
}
