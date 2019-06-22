package modeling.drawing;

import modeling.apertures.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Diffraction {

    Dimension winSize;

    int gridSize;

    int[] pixels;

    CircularAperture circularAperture;
    SquareAperture squareAperture;
    SlitAperture slitAperture;
    DoubleSlitAperture doubleSlitAperture;


    public BufferedImage getCircularImage(int length, double size) {
        circularAperture.setUserParams(length,size);
        return buildImage(circularAperture);
    }

    public BufferedImage getSquareImage(int length, double size) {
        squareAperture.setUserParams(length,size);
        return buildImage(squareAperture);
    }

    public BufferedImage getSlitImage(int length, double size) {
        slitAperture.setUserParams(length,size);
        return buildImage(slitAperture);
    }

    public BufferedImage getDoubleSlitImage(int length, double size) {
        doubleSlitAperture.setUserParams(length,size);
        return buildImage(doubleSlitAperture);
    }

    public Diffraction(Dimension winSize) {
        this.winSize=winSize;
        gridSize = Math.min(winSize.width,winSize.height);

        circularAperture = new CircularAperture(260,0.7,gridSize);
        squareAperture = new SquareAperture(260,0.7,gridSize);
        slitAperture = new SlitAperture(260,0.7,gridSize);
        doubleSlitAperture = new DoubleSlitAperture(260,0.7,gridSize);
    }


    public BufferedImage buildImage(Aperture aperture) {
        aperture.calculateFunction();

        pixels = new int[winSize.width*winSize.height];

        int i, j;
        for (i = 0; i != gridSize; i++)
            for (j = 0; j != gridSize; j++) {

                int col = aperture.getColorValue(i, j);
                //Красный луч на белом фоне
                int colval = 0XFFFFFF ^ (col * 0X000101);

                pixels[i + j * gridSize] = colval;
            }
        BufferedImage pixelImage = new BufferedImage(gridSize, gridSize, BufferedImage.TYPE_INT_RGB);
        pixelImage.setRGB(0, 0, gridSize, gridSize, pixels, 0, gridSize);

        return pixelImage;
    }
}
