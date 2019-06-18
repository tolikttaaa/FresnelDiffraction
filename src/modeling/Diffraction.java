package modeling;

import java.awt.*;

import static java.lang.Math.PI;

public class Diffraction{

    Dimension winSize;

    int gridSizeX;
    int gridSizeY;


    int gridBar=250;//10-600
    int lengthBar=260;//35-500
    int zoomBar=200;//30-400
    int brightnessBar=50;//1-500
    double colorMult;
    double zbase;
    static final double pi = PI;
    static final double pi2 = pi*2;
    double func[][];

    Aperture aperture;


    public Diffraction(Dimension winSize, int grid, int length, int zoom, int brightness, double zbase,double radius) {
        this.winSize = winSize;
        this.gridBar = grid;
        this.lengthBar = length;
        this.zoomBar = zoom;
        this.brightnessBar = brightness;
        this.zbase = zbase;

        aperture = new Aperture(radius);

        init();
    }

    public void init() {

        colorMult = 70*java.lang.Math.exp(brightnessBar/50.);

        reinit();
        zbase = 1/java.lang.Math.exp(200/50);

        handleResize();
    }

    void reinit() {
        handleResize();
    }

    void handleResize() {
        if (winSize.width == 0)
            return;
        int w = (winSize.width > winSize.height) ? winSize.height : winSize.width;
        winSize.width = winSize.height = w;
    }

    int angleSteps;
    int angleStepsMask;
    double angcos1[], angsin1[];
    long angcos2[], angsin2[];
    int angleSteps2;
    int angleSteps2Mask;
    static final int fixedPoint = 256;
    int accumR, accumI;
    double apertureArgMult;
    double colorLenMults;
    double zoomFactor;

    void computeFunction() {
        gridSizeX = gridSizeY = (gridBar & ~1);

        func = new double[gridSizeX][gridSizeY];
        int i, j;
        angleSteps = (gridBar >= 195) ? 1024 : 256;

        angleStepsMask = angleSteps-1;
        zoomFactor = java.lang.Math.exp(zoomBar/50.)*zbase;
        double baselen = java.lang.Math.exp(lengthBar/110.)
                /zoomFactor;
        angcos1 = new double[angleSteps];
        angsin1 = new double[angleSteps];
        // precompute sines and cosines, one for each angle step
        for (i = 0; i != angleSteps; i++) {
            angcos1[i] = java.lang.Math.cos(i*pi2/angleSteps);
            angsin1[i] = java.lang.Math.sin(i*pi2/angleSteps);
        }
        angleSteps2 = 4096;
        angleSteps2Mask = angleSteps2-1;
        angcos2 = new long[angleSteps2];
        angsin2 = new long[angleSteps2];
        float sign =1;

        for (i = 0; i != angleSteps2; i++) {
            angcos2[i] = (long)
                    (java.lang.Math.cos(i*pi2/angleSteps2) * fixedPoint*sign);
            angsin2[i] = (long)
                    (java.lang.Math.sin(i*pi2/angleSteps2) * fixedPoint*sign);
        }

        colorLenMults = baselen;

        apertureArgMult = angleSteps2*.25;
        apertureArgMult *= baselen*baselen;

        aperture.compute();

        int maxx = gridSizeX/2;
        int maxy = gridSizeY/2;

        for (i = 0; i != maxx; i++)
            for (j = 0; j <= i; j++)
                func[j][i] = func[i][j];

        for (i = 0; i != maxx; i++)
            for (j = 0; j != maxy; j++)
                func[gridSizeX-1-i][j] = func[i][j];

        for (i = 0; i != gridSizeX; i++)
            for (j = 0; j != maxy; j++)
                func[i][gridSizeX-1-j] = func[i][j];

    }

    void setFunction(int i, int j) {
        double ard = ((double) accumR)/(angleSteps*fixedPoint);
        double aid = ((double) accumI)/(angleSteps*fixedPoint);
        double mag = ard*ard + aid*aid;
        mag*=colorMult;
        if(mag>255)mag=255;
        func[i][j] = mag;
    }

    void clearAccum() {
        accumR = accumI = 0;
    }

    void apertureStart(double r) {
        double r2 = r*r;
        int arg = ((int) (r2*apertureArgMult)) & angleSteps2Mask;
        accumR -= angcos2[arg];
        accumI -= angsin2[arg];
    }

    void apertureStop(double r) {

        double r2 = r*r;
        int arg = ((int) (r2*apertureArgMult)) & angleSteps2Mask;
        accumR += angcos2[arg];
        accumI += angsin2[arg];
    }

    void apertureStartOrigin(boolean x) {
        if (x) {
            accumR -= fixedPoint*angleSteps;
        }
    }

    class Aperture {

        double radius;

        public Aperture(double radius) {
            this.radius = radius;
        }

        void compute() {
            int i, j;
            for (i = 0; i != gridSizeX/2; i++) {
                for (j = 0; j <= i; j++) {
                    clearAccum();
                    double x0 = (i/(double) gridSizeX)-.5;
                    double y0 = (j/(double) gridSizeY)-.5;
                    int th;
                    double cx = -x0;
                    double cy = -y0;
                    double c2 = cx*cx+cy*cy;
                    double cr = radius;
                    double c = c2 - cr*cr;
                    double ac4 = c*4;
                    double th1 = 0;
                    double th2 = pi2;
                    if (c <= 0) {
                        // need to do all of them
                    } else {
                        // figure out what angle range we need to do
                        double r = java.lang.Math.sqrt(c2);
                        double cx1 = cx/r;
                        double cy1 = cy/r;
                        double a1 = java.lang.Math.atan2(cy-cx1*cr, cx+cy1*cr);
                        double a2 = java.lang.Math.atan2(cy+cx1*cr, cx-cy1*cr);
                        th1 = (a1 < a2) ? a1 : a2;
                        th2 = (a1 > a2) ? a1 : a2;
                        if (th2-th1 > pi) {
                            th1 = (a1 > a2) ? a1 : a2;
                            th2 = (a1 < a2) ? a1 : a2;
                            th2 += pi2;
                        }
                    }
                    int th1i = (int) ((th1*angleSteps)/pi2);
                    int th2i = (int) ((th2*angleSteps)/pi2);
                    while (th1i < 0) {
                        th1i += angleSteps;
                        th2i += angleSteps;
                    }
                    for (th = th1i; th < th2i; th++) {
                        // find intersection of this ray with circle
                        double costh = angcos1[th & angleStepsMask];
                        double sinth = angsin1[th & angleStepsMask];
                        double b = -2*(costh*cx+sinth*cy);
                        double discrim = b*b-ac4;
                        if (discrim < 0)
                            continue;
                        discrim = java.lang.Math.sqrt(discrim);
                        double r1 = .5*(-b-discrim);
                        double r2 = .5*(-b+discrim);
                        if (r1 < 0 && r2 < 0)
                            continue;
                        if (r1 > 0)
                            apertureStart(r1);
                        apertureStop(r2);
                    }
                    apertureStartOrigin(c < 0);
                    setFunction(i, j);
                }
            }
        }
    }
}