package modeling.apertures;

import static modeling.calculations.FresnelCalc.fresnl;

public abstract class OneDimensionalAperture extends Aperture {

    //Массив координат начал и концов линий
    double[] lineX;
    int lineCount;


    public OneDimensionalAperture(int length, double size,int grid) {
        super(length, size,grid);
    }

    boolean oneDimensional() { return true; }

    void calculatePart() {
        double[] result = new double[2];

        //Чтобы найти функцию достаточно один раз пройти по площадке слева направо
        //Т.к. функция симметрична, проходим только половину пути
        int i, j;
        for (i = 0; i < gridSizeX/2; i++) {
            double x0 = (i/(double) gridSizeX)-.5;
            double ar = 0, ai = 0;
            int d = 1;
            //Ищем вклад каждой линии
            for (j = 0; j <lineCount; j++) {
                //Вычисляем интеграл, используя библиотеку
                fresnl((x0- lineX[j])* lambdaMult, result);
                ar += d*result[0];
                ai += d*result[1];
                d = -d;
            }
            //Находим интенсивность в точке
            double val = (ar*ar + ai*ai)/2;
            setIntensity(i,0,val);
        }
    }

    @Override
    public void setUserParams(int length, double size) {
        this.length=length;
        this.size=size;
        lambdaMult = Math.exp(length/110.)*650/510.;
    }
}
