import util.Triangle3;

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

    public final PaintRoutine CLEAR = (g) -> {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    };

    public Render3() {
        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        routine = this.CLEAR;

        renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                routine.run((Graphics2D) g);
            }
        };

        pane.add(renderPanel, BorderLayout.CENTER);

        setSize(400, 400);
        setVisible(true);
    }

    // add a better paint routine
    private void addPaintRoutine(PaintRoutine routine) {
        this.routine = routine;
    }

    public PaintRoutine trianglePaintRoutine(ArrayList<Triangle3> tris) {
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
}
