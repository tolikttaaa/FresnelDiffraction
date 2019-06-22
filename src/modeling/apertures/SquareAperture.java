package modeling.apertures;

import static modeling.calculations.FresnelCalc.fresnl;

public class SquareAperture extends Aperture {

    //Массив линии, задающие квадрат
    double top,bottom,left,right;

    public SquareAperture(int length, double size,int grid) {
        super(length, size, grid);
    }

        void calculatePart() {
            double[] result1 = new double[2];
            double[] result2 = new double[2];
            double[] result3 = new double[2];
            double[] result4 = new double[2];


            int i, j;
            //Пройдем только по треугольнику
            //Потом отобразим функцию
            for (i = 0; i < gridSizeX / 2; i++) {

            double x0 = (i / (double) gridSizeX) - 0.5;
            for (j = 0; j <=i; j++) {

                double y0 = (j / (double) gridSizeY) - 0.5;

                //Находим интерал, используя библиотеку
                fresnl((left - x0) * lambdaMult, result1);
                fresnl((right - x0) * lambdaMult, result2);
                fresnl((top - y0) * lambdaMult, result3);
                fresnl((bottom - y0) * lambdaMult, result4);

                //Находим значения по x и y
                double ar1 = result1[0] - result2[0];
                double ai1 = result1[1] - result2[1];

                double ar2 = result3[0] - result4[0];
                double ai2 = result3[1] - result4[1];

                //Конечное значение равно произведению значений по x и y
                double ar = (ar1 * ar2 - ai1 * ai2)/2;
                double ai = (ar1 * ai2 + ai1 * ar2)/2;

                double val =ar * ar + ai * ai;
                setIntensity(i,j,val);
            }
        }
    }

    boolean hasSymmetryX() { return true; }
    boolean hasSymmetryY() { return true; }
    boolean hasDiagonalSymmetry() { return true; }

    @Override
    public void setUserParams(int length, double size) {
        lambdaMult = Math.exp(length/110.)*650/510.;
        this.length=length;
        this.size=size;

        left = -size;
        right = size;
        top = -size;
        bottom = size;

    }
}
