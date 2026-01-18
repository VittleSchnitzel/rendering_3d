import util.Matrix3;
import util.Triangle3;
import util.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Render3 extends JFrame {
    JSlider headingSlider;
    JSlider pitchSlider;
    JPanel renderPanel;

    public interface PaintRoutine {
        void run(Graphics2D g);
    }
    PaintRoutine routine;

    Vector3.Transform rotateByRender = (v) -> {
        return getHeadingMatrix().multiply(getPitchMatrix()).transform(v);
    };
    Vector3.Transform translateToCenter = (v) -> {
        return v.add(getWidth()/2, getHeight()/2, 0);
    };

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

    // This paint routine uses a z buffer and filling method to color the triangle
    // after it has been transformed
    public PaintRoutine paintRoutineZBufFillTriangle(ArrayList<Triangle3> tris, Vector3.Transform transform) {
        return (g) -> {
            paintRoutineClear().run(g);

            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            double[] zBuffer = new double[img.getWidth() * img.getHeight()];
            Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

            for (Triangle3 t : tris) {
                Vector3 v1 = translateToCenter.run(transform.run(t.v1));
                Vector3 v2 = translateToCenter.run(transform.run(t.v2));
                Vector3 v3 = translateToCenter.run(transform.run(t.v3));

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
        };
    }

    // Does essentially the same thing as ZBufFill, but shades the triangle
    // fill based on the surface normal and a light direction
    public PaintRoutine paintRoutineZBufShadeTriangle(ArrayList<Triangle3> tris, Vector3.Transform transform, Vector3 light) {
        return (g) -> {
            paintRoutineClear().run(g);

            Vector3 lightDir = light.unit();

            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            double[] zBuffer = new double[img.getWidth() * img.getHeight()];
            Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

            for (Triangle3 t : tris) {
                Vector3 v1 = translateToCenter.run(transform.run(t.v1));
                Vector3 v2 = translateToCenter.run(transform.run(t.v2));
                Vector3 v3 = translateToCenter.run(transform.run(t.v3));

                int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                int maxX = (int) Math.min(img.getWidth() - 1,
                        Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                int maxY = (int) Math.min(img.getHeight() - 1,
                        Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                Vector3 v12 = v1.add(v2.neg());
                Vector3 v13 = v1.add(v3.neg());
                Vector3 norm = v12.norm(v13);

                double angleCos = Math.abs(norm.dot(lightDir));
                Color shadedColor = getShade(t.color, angleCos);

                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                        double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                        double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                        if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                            double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                            int zIndex = y * img.getWidth() + x;

                            if (zBuffer[zIndex] < depth) {
                                img.setRGB(x, y, shadedColor.getRGB());
                                zBuffer[zIndex] = depth;
                            }
                        }
                    }
                }
            }
            g.drawImage(img, 0, 0, null);
        };
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
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
