import util.Triangle3;
import util.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List tris = new ArrayList<>();
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
                                new Vector3(100, 100, -100),
                                Color.yellow));
        tris.add(new Triangle3( new Vector3(-100, 100, -100),
                                new Vector3(-100, -100, -100),
                                new Vector3(-100, -100, 100),
                                Color.blue));
        Render3 frame = new Render3();
    }
}
