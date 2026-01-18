import util.Matrix3;
import util.Triangle3;
import util.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static Vector3 pos = new Vector3(0, -100, 0);
    static Vector3 vel = new Vector3(0, 0, 0);
    static Vector3 acc = new Vector3(0, 12000, 0);

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
        for (int i = 0; i < 5; i++) tris = inflate(tris);

        Render3 frame = new Render3();
        Render3.PaintRoutine routine =
                frame.paintRoutineZBufShadeTriangle(
                        tris,
                        (v) -> {
                            v = frame.rotateByRender.run(v);
                            v = v.add(pos);
                            return v;
                        },
                        new Vector3(0, 0, 1)
                );
        frame.addPaintRoutine(routine);

        RenderUpdater ru = new RenderUpdater();
        ru.init(frame);
        frame.addWindowListener(ru);
    }

    public static class RenderUpdater implements WindowListener {
        private Timer timer;

        public void init(Render3 r) {
            int delay = 30;
            timer = new Timer(delay, null);
            timer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!(pos.y > 47 && Math.abs(vel.y) < 50)) pos = pos.add(vel.scale(0.01));
                    if (!(pos.y > 47 && Math.abs(vel.y) < 50)) vel = vel.add(acc.scale(0.01));
                    if (pos.y > 47 && vel.y > 0) {
                        vel = new Vector3(vel.x, -vel.y*Math.atan(Math.abs(vel.y/200))*2/Math.PI*0.8, vel.z);
                    }
                    r.repaint();
                }
            });
            timer.start();
        }
        @Override public void windowOpened(WindowEvent e) {}
        @Override public void windowClosing(WindowEvent e) {}
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
        @Override public void windowIconified(WindowEvent e) {}
        @Override public void windowDeiconified(WindowEvent e) {}
        @Override public void windowActivated(WindowEvent e) {}
        @Override public void windowDeactivated(WindowEvent e) {}
    }

    public static ArrayList<Triangle3> inflate(ArrayList<Triangle3> tris) {
        ArrayList<Triangle3> result = new ArrayList<Triangle3>();
        for (Triangle3 t : tris) {
            Vector3 m1 =
                    new Vector3((t.v1.x + t.v2.x)/2, (t.v1.y + t.v2.y)/2, (t.v1.z + t.v2.z)/2);
            Vector3 m2 =
                    new Vector3((t.v2.x + t.v3.x)/2, (t.v2.y + t.v3.y)/2, (t.v2.z + t.v3.z)/2);
            Vector3 m3 =
                    new Vector3((t.v1.x + t.v3.x)/2, (t.v1.y + t.v3.y)/2, (t.v1.z + t.v3.z)/2);

            result.add(new Triangle3(t.v1, m1, m3, t.color));
            result.add(new Triangle3(t.v2, m1, m2, t.color));
            result.add(new Triangle3(t.v3, m2, m3, t.color));
            result.add(new Triangle3(m1, m2, m3, t.color));
        }

        for (Triangle3 t : result) {
            for (Vector3 v : new Vector3[] {t.v1, t.v2, t.v3}) {
                double l = v.mag() / Math.sqrt(10000);
                v.x /= l;
                v.y /= l;
                v.z /= l;
            }
        }

        return result;
    }
}
