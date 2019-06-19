package modeling;

import java.awt.*;
import java.awt.image.BufferedImage;
public class Modulating {
    private Diffraction diffraction;

    public Modulating() {
        Dimension winSize = new Dimension(1000,1000);
        diffraction = new Diffraction(winSize);
    }
    public BufferedImage getSlitImage(int len, double radius) {
        return diffraction.getSlitImage(len, radius);
    }

    public BufferedImage getCircleImage(int len, double radius) {
        return diffraction.getCircularImage(len, radius);
    }

    public BufferedImage getDoubleSlitImage(int len, double radius) {
        return diffraction.getDoubleSlitImage(len, radius / 8);
    }

    public BufferedImage getSquareImage(int len, double radius) {
        return diffraction.getSquareImage(len, radius);
    }
}
