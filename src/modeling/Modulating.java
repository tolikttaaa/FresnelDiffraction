package modeling;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Modulating {
    public static BufferedImage getCircleImage(int len, double radius) {
        Dimension winSize = new Dimension(500,500);

        Diffraction frame = Diffraction.getCircleDiffraction(winSize,1000, len,200,50,0, radius);

        return getImage(winSize, frame);
    }

    public static BufferedImage getSlitImage(int len, double radius) {
        Dimension winSize = new Dimension(500,500);

        Diffraction frame = Diffraction.getSlitDiffraction(winSize,1000, len,200,50,0, radius);

        return getImage(winSize, frame);
    }

    public static BufferedImage getDoubleSlitImage(int len, double radius) {
        Dimension winSize = new Dimension(500,500);

        Diffraction frame = Diffraction.getDoubleSlitDiffraction(winSize,1000, len,200,50,0, radius);

        return getImage(winSize, frame);
    }

    public static BufferedImage getSquareImage(int len, double radius) {
        Dimension winSize = new Dimension(500,500);

        Diffraction frame = Diffraction.getSquareDiffraction(winSize,1000, len,200,50,0, radius);

        return getImage(winSize, frame);
    }

    private static BufferedImage getImage(Dimension imageSize, Diffraction frame) {
        frame.computeFunction();

        int [] pixels = new int[imageSize.width * imageSize.height];

        int i, j;
        for (i = 0; i != frame.gridSizeX; i++)
            for (j = 0; j != frame.gridSizeY; j++) {
                int x = i * imageSize.width / frame.gridSizeX;
                int y = j * imageSize.height / frame.gridSizeY;
                int x2 = (i + 1) * imageSize.width / frame.gridSizeX;
                int y2 = (j + 1) * imageSize.height / frame.gridSizeY;
                int col = (int) frame.func[i][j];
                int colVal = 0XFFFFFF ^ (col * 0X000101);
                int k, l;
                for (k = x; k < x2; k++) {
                    for (l = y; l < y2; l++) {
                        pixels[k + l * imageSize.width] = colVal;
                    }
                }
            }

        BufferedImage pixelImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
        pixelImage.setRGB(0, 0, imageSize.width, imageSize.height, pixels,0, imageSize.width);

        return pixelImage;
    }
}
