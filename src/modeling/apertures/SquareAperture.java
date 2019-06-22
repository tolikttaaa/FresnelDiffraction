package modeling.apertures;
import java.awt.*;

import static modeling.calculations.FresnelCalc.fresnl;

public class SquareAperture extends Aperture {

    //Массив линии, задающие квадрат
    double top,bottom,left,right;

    public SquareAperture(int length, double size,int grid) {
        super(length, size, grid);
    }

        void compute() {
        setupRects();
        int i, j;
        double[] result1 = new double[2];
        double[] result2 = new double[2];
        double[] result3 = new double[2];
        double[] result4 = new double[2];


        //Пройдем только по треугольнику
        //Дальше отобразим функцию
        for (i = 0; i < gridSizeX / 2; i++) {

            double x0 = (i / (double) gridSizeX) - .5;
            for (j = 0; j <=i; j++) {

                double y0 = (j / (double) gridSizeY) - .5;

                //Находим интерал, используя библиотеку
                fresnl((left - x0) * colorLenMults, result1);
                fresnl((right - x0) * colorLenMults, result2);
                fresnl((top - y0) * colorLenMults, result3);
                fresnl((bottom - y0) * colorLenMults, result4);

                //Находим значения по x и y
                double ar1 = result1[0] - result2[0];
                double ai1 = result1[1] - result2[1];

                double ar2 = result3[0] - result4[0];
                double ai2 = result3[1] - result4[1];

                //Конечное значение равно произведению значений по x и y
                double ar = (ar1 * ar2 - ai1 * ai2)/2;
                double ai = (ar1 * ai2 + ai1 * ar2)/2;

                double val =ar * ar + ai * ai;
                setFunction(i,j,val);
            }
        }
    }

    boolean hasXSymmetry() { return true; }
    boolean hasYSymmetry() { return true; }
    boolean hasDiagonalSymmetry() { return true; }

    @Override
    public void setParams(int length, double size) {
        colorLenMults = Math.exp(length/110.)*650/510.;
        this.length=length;
        this.size=size;

    }
    void setupRects() {
        left = -size;
        right = size;
        top = -size;
        bottom = size;
    }
}
