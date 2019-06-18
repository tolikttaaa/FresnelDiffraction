package modeling;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Modulating {

    public static void main(String[] args) {
        getImage(1,1,1,6e-6);
    }

    public static BufferedImage getImage(double a, double b, double n, double lambda) {

        ZoneRadius zoneRadius = new ZoneRadius(a,b,n,lambda);

        Dimension winSize = new Dimension(500,500);

        Diffraction frame = new Diffraction(winSize,1000,260,200,50,0,zoneRadius.getRadius() * 100);
        frame.computeFunction();

        int [] pixels = new int[winSize.width*winSize.height];

        int i,j;
        for (i = 0; i != frame.gridSizeX; i++)
            for (j = 0; j != frame.gridSizeY; j++) {
                int x = i*winSize.width/frame.gridSizeX;
                int y = j*winSize.height/frame.gridSizeY;
                int x2 = (i+1)*winSize.width/frame.gridSizeX;
                int y2 = (j+1)*winSize.height/frame.gridSizeY;
                int colval = 0;

                    int col = (int)frame.func[i][j];
                    colval = 0xFF000000 | (col*0X010101);

                int k, l;
                for (k = x; k < x2; k++)
                    for (l = y; l < y2; l++){
                        pixels[k+l*winSize.width] = colval;
                        }
            }

        BufferedImage pixelImage = new BufferedImage(winSize.width, winSize.height, BufferedImage.TYPE_INT_RGB);
        pixelImage.setRGB(0, 0, winSize.width, winSize.height, pixels, 0, winSize.width);

        try {
            ImageIO.write(pixelImage,"png", new File("a1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pixelImage;
    }
}
