import util.Matrix3;
import util.Triangle3;
import util.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Render3 extends JFrame {
    JSlider headingSlider;
    JSlider pitchSlider;
    JPanel renderPanel;

    public interface PaintRoutine {
        void run(Graphics2D g);
    }
    PaintRoutine routine;

    public Render3() {
        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        headingSlider = new JSlider(0, 360, 180);
        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(headingSlider, BorderLayout.SOUTH);

        pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pitchSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(pitchSlider, BorderLayout.EAST);

        routine = paintRoutineClear();

        renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                routine.run((Graphics2D) g);
            }
        };

        pane.add(renderPanel, BorderLayout.CENTER);

        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // add a better paint routine
    public void addPaintRoutine(PaintRoutine routine) {
        this.routine = routine;
    }

    public PaintRoutine paintRoutineClear() {
        return (g) -> {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        };
    }

    public PaintRoutine paintRoutineSimpleTriangle(ArrayList<Triangle3> tris) {
        return (g) -> {
            g.translate(getWidth()/2, getHeight()/2);
            g.setColor(Color.WHITE);
            for (Triangle3 t : tris) {
                Path2D path = new Path2D.Double();
                path.moveTo(t.v1.x, t.v1.y);
                path.lineTo(t.v2.x, t.v2.y);
                path.lineTo(t.v3.x, t.v3.y);
                path.closePath();
                g.draw(path);
            }
        };
    }

    public PaintRoutine paintRoutineTransformTriangle(ArrayList<Triangle3> tris, Vector3.Transform transform) {
        return (g) -> {
            g.translate(getWidth()/2, getHeight()/2);
            g.setColor(Color.WHITE);
            for (Triangle3 t : tris) {
                Vector3 v1 = transform.run(t.v1);
                Vector3 v2 = transform.run(t.v2);
                Vector3 v3 = transform.run(t.v3);
                Path2D path = new Path2D.Double();
                path.moveTo(v1.x, v1.y);
                path.lineTo(v2.x, v2.y);
                path.lineTo(v3.x, v3.y);
                path.closePath();
                g.draw(path);
            }
        };
    }

    public int getHeading() {
        return headingSlider.getValue();
    }

    public Matrix3 getHeadingMatrix() {
        return Matrix3.getRotateXZ(getHeading()*Math.PI/180);
    }

    public int getPitch() {
        return pitchSlider.getValue();
    }

    public Matrix3 getPitchMatrix() {
        return Matrix3.getRotateYZ(getPitch()*Math.PI/180);
    }
}
