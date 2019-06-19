package modeling;

import java.awt.*;
import java.awt.image.*;

class Diffraction{

    Dimension winSize;

    int gridSizeX = 200;
    int gridSizeY = 200;


    double radius;

    int gridBar=1000;
    int lengthBar=260;//TODO from params
    int zoomBar=200;
    int brightnessBar=50;
    double colorMult;
    double zbase;
    static final double pi = 3.14159265358979323846;
    static final double pi2 = pi*2;
    double func[][];
    boolean functionChanged;
    int pixels[];

    Aperture aperture;

    CircularAperture circularAperture;
    SquareAperture squareAperture;
    SlitAperture slitAperture;
    DoubleSlitAperture doubleSlitAperture;

    public BufferedImage getCircularImage(int length, double radius){
        lengthBar=length;
        this.radius=radius;
        aperture=circularAperture;
        aperture.setToDefaults();
        return buildImage();
    }
    public BufferedImage getSquareImage(int length, double radius){
        lengthBar=length;
        this.radius=radius;
        aperture=squareAperture;
        aperture.setToDefaults();
        return buildImage();
    }
    public BufferedImage getSlitImage(int length, double radius){
        lengthBar=length;
        this.radius=radius;
        aperture=slitAperture;
        aperture.setToDefaults();
        return buildImage();
    }
    public BufferedImage getDoubleSlitImage(int length, double radius){
        lengthBar=length;
        this.radius=radius;
        if(radius>1/8.0)
            this.radius=1/8.0;
        aperture=doubleSlitAperture;
        aperture.setToDefaults();
        return buildImage();
    }

    public Diffraction(Dimension winSize) {
        colorMult = 70*java.lang.Math.exp(brightnessBar/50.);

        functionChanged = true;
        this.winSize=winSize;

        zbase = 1/java.lang.Math.exp(200/50);

        circularAperture = new CircularAperture();
        squareAperture = new SquareAperture();
        slitAperture = new SlitAperture();
        doubleSlitAperture= new DoubleSlitAperture();
    }


    BufferedImage buildImage(){
        computeFunction();

        colorMult = 70* Math.exp(brightnessBar/50.);
        pixels = new int[winSize.width*winSize.height];


        int i, j;
        for (i = 0; i != gridSizeX; i++)
            for (j = 0; j != gridSizeY; j++) {
                int x = i*winSize.width/gridSizeX;
                int y = j*winSize.height/gridSizeY;
                int x2 = (i+1)*winSize.width/gridSizeX;
                int y2 = (j+1)*winSize.height/gridSizeY;
                int colval = 0;

                    int col = getColorValue(i, j);
                    colval = 0XFFFFFF ^ (col * 0X000101);

                int k, l;
                for (k = x; k < x2; k++)
                    for (l = y; l < y2; l++)
                        pixels[k+l*winSize.width] = colval;
            }
        BufferedImage pixelImage = new BufferedImage(winSize.width, winSize.height, BufferedImage.TYPE_INT_RGB);
        pixelImage.setRGB(0, 0, winSize.width, winSize.height, pixels,0, winSize.width);

        return pixelImage;
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
        if (aperture.oneDimensional()) {
            gridSizeX *= 2;
            gridSizeY = 1;
        }
        func = new double[gridSizeX][gridSizeY];
        int i, j;
        angleSteps = (gridBar>= 195) ? 1024 : 256;
        if (aperture.oneDimensional())
            angleSteps = (gridBar >= 195) ? 2048 : 1024;
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
        float sign = 1;

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

        int maxx = aperture.hasXSymmetry() ? gridSizeX/2 : gridSizeX;
        int maxy = aperture.hasYSymmetry() ? gridSizeY/2 : gridSizeY;

        if (aperture.hasDiagonalSymmetry())
                for (i = 0; i != maxx; i++)
                    for (j = 0; j <= i; j++)
                        func[j][i] = func[i][j];
        if (aperture.hasXSymmetry())
                for (i = 0; i != maxx; i++)
                    for (j = 0; j != maxy; j++)
                        func[gridSizeX-1-i][j] = func[i][j];
        if (aperture.hasYSymmetry())
                for (i = 0; i != gridSizeX; i++)
                    for (j = 0; j != maxy; j++)
                        func[i][gridSizeX-1-j] = func[i][j];
        functionChanged = false;
    }

    void setFunction(int i, int j) {
            double ard = ((double) accumR)/(angleSteps*fixedPoint);
            double aid = ((double) accumI)/(angleSteps*fixedPoint);
            double mag = ard*ard + aid*aid;
            func[i][j]= mag;
    }

    void clearAccum() {
        int i;
        for (i = 0; i != 3; i++)
            accumR = accumI = 0;
    }

    void apertureStart(double r) {
        // report the fact that we found the start of an opening
        // at radius r.
        double r2 = r*r;
        int arg = ((int) (r2*apertureArgMult)) & angleSteps2Mask;
        accumR -= angcos2[arg];
        accumI -= angsin2[arg];
    }

    void apertureStop(double r) {
        // report the fact that we found the end of an opening
        // at radius r.
        double r2 = r*r;
        int arg = ((int) (r2*apertureArgMult)) & angleSteps2Mask;
        accumR += angcos2[arg];
        accumI += angsin2[arg];
    }

    void apertureStartOrigin(boolean x) {
        // if x is true, that means the origin is at a transparent
        // spot.  Otherwise it's at an opaque spot.
        if (x) {
            accumR -= fixedPoint*angleSteps;
        }
    }

    int sign(double x) {
        return x < 0 ? -1 : 1;
    }


    int getColorValue(int i, int j) {
        int val = (int) (func[i][j] * colorMult);
        if (val > 255)
            val = 255;
        return val;
    }

    abstract class Aperture {
        abstract void compute();
        abstract void setToDefaults();
        int defaultBrightness() { return 50; }
        boolean oneDimensional() { return false; }
        boolean hasXSymmetry() { return false; }
        boolean hasYSymmetry() { return false; }
        boolean hasDiagonalSymmetry() { return false; }
        void rezoom(double x) {}
        Aperture() { setToDefaults(); }
    };

    class CircularAperture extends Aperture {
        String getName() { return "circle"; }
        boolean hasXSymmetry() { return true; }
        boolean hasYSymmetry() { return true; }
        boolean hasDiagonalSymmetry() { return true; }
        void setToDefaults() { radius = .25; }
        void rezoom(double z) { radius *= z; }
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


        int getSelection(int x, int y) {
            int rx = winSize.width/2-x;
            int ry = winSize.height/2-y;
            double r = java.lang.Math.sqrt(rx*rx+ry*ry)/winSize.width;
            return (java.lang.Math.abs(r-radius) < 5./winSize.width) ? 1 : -1;
        }

        boolean drag(int x, int y) {
            int rx = winSize.width/2-x;
            int ry = winSize.height/2-y;
            double r = java.lang.Math.sqrt(rx*rx+ry*ry)/winSize.width;
            if (r == radius)
                return false;
            radius = r;
            return true;
        }

        double getDimension() { return radius*2; }
    }

    abstract class OneDimensionalAperture extends Aperture {
        public double lineLocations[];
        public int lineCount;
        boolean oneDimensional() { return true; }

        void compute() {
            int i, j;
            double result[] = new double[2];
            // by default we use only green light.
            int xlim = (hasXSymmetry()) ? gridSizeX/2 : gridSizeX;
            double astart = 0;

            // add in a right side at infinity if only one line
            if (lineCount == 1)
                astart += .5;

            for (i = 0; i != xlim; i++) {
                double x0 = (i/(double) gridSizeX)-.5;
                double mult = colorLenMults;
                double ar = astart, ai = astart;
                int d = 1;
                for (j = 0; j != lineCount; j++) {
                    fresnl((x0-lineLocations[j])*mult, result);
                    ar += d*result[0];
                    ai += d*result[1];
                    d = -d;
                }
                func[i][0] = .5*(ar*ar + ai*ai);
            }
        }

        void rezoom(double z) {
            int i;
            for (i = 0; i != lineCount; i++)
                lineLocations[i] *= z;
        }
    }

    class SlitAperture extends OneDimensionalAperture {
        void setToDefaults() {
            lineLocations = new double[lineCount = 2];
            lineLocations[0] = -radius;
            lineLocations[1] =  radius;
        }
        int defaultBrightness() { return 200; }
        boolean hasXSymmetry() { return true; }
    }

    class DoubleSlitAperture extends OneDimensionalAperture {
        void setToDefaults() {
            lineLocations = new double[lineCount = 4];
            lineLocations[0] = -radius;
            lineLocations[1] = -1/8.0;
            lineLocations[2] =  1/8.0;
            lineLocations[3] =  radius;
        }
        int defaultBrightness() { return 140; }
        boolean hasXSymmetry() { return true; }
    }

    abstract class BlockAperture extends Aperture {
        int blockCountX, blockCountY;

        boolean blocks[][];

        // Lines.  Only odd indices used.
        double lineXLocations[];
        double lineYLocations[];

        int rectCount;
        double rects[][];

        abstract void setupRects();

        void compute() {
            setupRects();
            int i, j;
            double result1[] = new double[2];
            double result2[] = new double[2];
            double result3[] = new double[2];
            double result4[] = new double[2];
            // by default we use only green light.
            double astart = 0;
            int xlim = (hasXSymmetry()) ? gridSizeX / 2 : gridSizeX;
            int ylim = (hasYSymmetry()) ? gridSizeY / 2 : gridSizeY;
            for (i = 0; i != xlim; i++) {
                if (hasDiagonalSymmetry())
                    ylim = i + 1;
                double x0 = (i / (double) gridSizeX) - .5;
                for (j = 0; j != ylim; j++) {
                    double y0 = (j / (double) gridSizeY) - .5;
                        double mult = colorLenMults;
                        double ar = 0, ai = astart;
                        int l;
                        // add up contributions from all rectangles
                        for (l = 0; l != rectCount; l++) {
                            fresnl((rects[l][0] - x0) * mult, result1);
                            fresnl((rects[l][2] - x0) * mult, result2);
                            fresnl((rects[l][1] - y0) * mult, result3);
                            fresnl((rects[l][3] - y0) * mult, result4);
                            double ar1 = result1[0] - result2[0];
                            double ai1 = result1[1] - result2[1];
                            double ar2 = result3[0] - result4[0];
                            double ai2 = result3[1] - result4[1];
                            ar += rects[l][4] * (ar1 * ar2 - ai1 * ai2);
                            ai += rects[l][4] * (ar1 * ai2 + ai1 * ar2);
                        }
                        func[i][j] = ar * ar + ai * ai;
                }
            }
        }

        boolean isSelected(int x, int y) {
            return isSelected(x, y, 0);
        }

        boolean isSelected(int x, int y, int iter) {
            // determine if a line is selected, accounting for
            // symmetry.
            return false;
        }

        int getSelection(int x, int y) {
            double xf = ((double) x)/winSize.width - .5;
            double yf = ((double) y)/winSize.width - .5;
            double thresh = 3./winSize.width;
            int sel = -1;
            int i;
            for (i = 1; i < blockCountX; i += 2) {
                double dist = java.lang.Math.abs(lineXLocations[i]-xf);
                if (dist < thresh) {
                    sel = 100+i;
                    thresh = dist;
                }
            }
            for (i = 1; i < blockCountY; i += 2) {
                double dist = java.lang.Math.abs(lineYLocations[i]-yf);
                if (dist < thresh) {
                    sel = 200+i;
                    thresh = dist;
                }
            }
            return sel;
        }

        boolean drag(int x, int y) {
            double xf = ((double) x)/winSize.width - .5;
            double yf = ((double) y)/winSize.width - .5;
            return dragLine(-101, -1, xf, 0);
        }

        void rezoom(double z) {
            int i;
            for (i = 1; i < blockCountX; i += 2)
                lineXLocations[i] *= z;
            for (i = 1; i < blockCountY; i += 2)
                lineYLocations[i] *= z;
        }

        boolean dragLine(int x, int y, double loc, int iter) {
            // drag a line and all lines it is related to by symmetry.
            if (x != -1) {
                if (hasXSymmetry() && sign(lineXLocations[x]) != sign(loc))
                    return false;
                if (x > 1 && loc <= lineXLocations[x-2])
                    return false;
                if (x < blockCountX-2 && loc >= lineXLocations[x+2])
                    return false;
            }
            if (y != -1) {
                if (hasYSymmetry() && sign(lineYLocations[y]) != sign(loc))
                    return false;
                if (y > 1 && loc <= lineYLocations[y-2])
                    return false;
                if (y < blockCountY-2 && loc >= lineYLocations[y+2])
                    return false;
            }
            if (x != -1 && hasXSymmetry() && iter < 1)
                dragLine(blockCountX-1-x, y, -loc, 1);
            if (y != -1 && hasYSymmetry() && iter < 2)
                dragLine(x, blockCountY-1-y, -loc, 2);
            if (hasDiagonalSymmetry() && iter < 3)
                dragLine(y, x, loc, 3);
            if (x != -1)
                lineXLocations[x] = loc;
            if (y != -1)
                lineYLocations[y] = loc;
            return true;
        }

        double getDimension() { return lineXLocations[blockCountX-2]-
                lineXLocations[1]; }
    }

    class SquareAperture extends BlockAperture {
        String getName() { return "square"; }
        boolean hasXSymmetry() { return true; }
        boolean hasYSymmetry() { return true; }
        boolean hasDiagonalSymmetry() { return true; }
        void setToDefaults() {
            double sqdim = .25;
            blockCountX = blockCountY = 5;
            blocks = new boolean[blockCountX][blockCountY];
            blocks[2][2] = true;
            lineXLocations = new double[blockCountX];
            lineYLocations = new double[blockCountY];
            lineXLocations[1] = -radius;
            lineXLocations[3] =  radius;
            lineYLocations[1] = -radius;
            lineYLocations[3] =  radius;
        }
        void setupRects() {
            rectCount = 1;
            rects = new double[1][5];
            double sqdim = lineXLocations[3];
            rects[0][0] = -radius;
            rects[0][1] = -radius;
            rects[0][2] = radius;
            rects[0][3] = radius;
            rects[0][4] = .5;
        }
    }

    // fresnel integral routines from Cephes Math Library

    /* S(x) for small x */
    static final double sn[] = {
            -2.99181919401019853726E3,
            7.08840045257738576863E5,
            -6.29741486205862506537E7,
            2.54890880573376359104E9,
            -4.42979518059697779103E10,
            3.18016297876567817986E11,
    };
    static final double sd[] = {
            2.81376268889994315696E2,
            4.55847810806532581675E4,
            5.17343888770096400730E6,
            4.19320245898111231129E8,
            2.24411795645340920940E10,
            6.07366389490084639049E11,
    };

    static final double cn[] = {
            -4.98843114573573548651E-8,
            9.50428062829859605134E-6,
            -6.45191435683965050962E-4,
            1.88843319396703850064E-2,
            -2.05525900955013891793E-1,
            9.99999999999999998822E-1,
    };
    static final double cd[] = {
            3.99982968972495980367E-12,
            9.15439215774657478799E-10,
            1.25001862479598821474E-7,
            1.22262789024179030997E-5,
            8.68029542941784300606E-4,
            4.12142090722199792936E-2,
            1.00000000000000000118E0,
    };

    /* Auxiliary function f(x) */
    static final double fn[] = {
            4.21543555043677546506E-1,
            1.43407919780758885261E-1,
            1.15220955073585758835E-2,
            3.45017939782574027900E-4,
            4.63613749287867322088E-6,
            3.05568983790257605827E-8,
            1.02304514164907233465E-10,
            1.72010743268161828879E-13,
            1.34283276233062758925E-16,
            3.76329711269987889006E-20,
    };
    static final double fd[] = {
            /*  1.00000000000000000000E0, */
            7.51586398353378947175E-1,
            1.16888925859191382142E-1,
            6.44051526508858611005E-3,
            1.55934409164153020873E-4,
            1.84627567348930545870E-6,
            1.12699224763999035261E-8,
            3.60140029589371370404E-11,
            5.88754533621578410010E-14,
            4.52001434074129701496E-17,
            1.25443237090011264384E-20,
    };

    /* Auxiliary function g(x) */
    static final double gn[] = {
            5.04442073643383265887E-1,
            1.97102833525523411709E-1,
            1.87648584092575249293E-2,
            6.84079380915393090172E-4,
            1.15138826111884280931E-5,
            9.82852443688422223854E-8,
            4.45344415861750144738E-10,
            1.08268041139020870318E-12,
            1.37555460633261799868E-15,
            8.36354435630677421531E-19,
            1.86958710162783235106E-22,
    };

    static final double gd[] = {
            /*  1.00000000000000000000E0, */
            1.47495759925128324529E0,
            3.37748989120019970451E-1,
            2.53603741420338795122E-2,
            8.14679107184306179049E-4,
            1.27545075667729118702E-5,
            1.04314589657571990585E-7,
            4.60680728146520428211E-10,
            1.10273215066240270757E-12,
            1.38796531259578871258E-15,
            8.39158816283118707363E-19,
            1.86958710162783236342E-22,
    };

    static final double PI = pi;
    static final double PIBYTWO = pi/2;

    int fresnl(double xxa, double result[]) {
        double f, g, cc, ss, c, s, t, u;
        double x, x2;

        while (true) {
            x = java.lang.Math.abs(xxa);
            x2 = x * x;
            if (x2 < 2.5625) {
                t = x2 * x2;
                ss = x * x2 * polevl(t, sn, 5) / p1evl(t, sd, 6);
                cc = x * polevl(t, cn, 5) / polevl(t, cd, 6);
                break;
            }
            if (x > 36974.0) {
                cc = 0.5;
                ss = 0.5;
                break;
            }
            /* Auxiliary functions for large argument  */
            x2 = x * x;
            t = PI * x2;
            u = 1.0 / (t * t);
            t = 1.0 / t;
            f = 1.0 - u * polevl(u, fn, 9) / p1evl(u, fd, 10);
            g = t * polevl(u, gn, 10) / p1evl(u, gd, 11);
            t = PIBYTWO * x2;
            c = java.lang.Math.cos(t);
            s = java.lang.Math.sin(t);
            t = PI * x;
            cc = 0.5 + (f * s - g * c) / t;
            ss = 0.5 - (f * c + g * s) / t;
            break;
        }

        if (xxa < 0.0) {
            cc = -cc;
            ss = -ss;
        }
        result[0] = cc;
        result[1] = ss;
        return (0);
    }

    double polevl(double x, double coef[], int N) {
        double ans;
        int i;
        int p = 0;

        ans = coef[p++];
        i = N;

        do
            ans = ans * x + coef[p++];
        while (--i > 0);

        return (ans);
    }

    double p1evl(double x, double coef[], int N) {
        double ans;
        int p = 0;
        int i;

        ans = x + coef[p++];
        i = N - 1;

        do
            ans = ans * x + coef[p++];
        while (--i > 0);

        return (ans);
    }
}
