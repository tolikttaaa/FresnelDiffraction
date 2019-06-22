package modeling.apertures;

import static modeling.calculations.FresnelCalc.fresnl;

public abstract class OneDimensionalAperture extends Aperture {

    public OneDimensionalAperture(int length, double size,int grid) {
        super(length, size,grid);
    }

    //Массив линий
    public double[] lineLocations;
    public int lineCount;

    boolean oneDimensional() { return true; }

    void compute() {
        double[] result = new double[2];
        double astart = 0;

        //Если линия одна, ограничим площадку справа
        if (lineCount == 1) {
            astart = 0.5;
        }

        int i, j;

        //Чтобы найти функцию достаточно один раз пройти по площадке слева направо
        //Т.к. функция симметрична, проходим только половину пути
        for (i = 0; i < gridSizeX/2; i++) {
            double x0 = (i/(double) gridSizeX)-.5;
            double ar = astart, ai = astart;
            int d = 1;
            //Ищем вклад каждой линии
            for (j = 0; j <lineCount; j++) {
                //Вычисляем интеграл, используя библиотеку
                fresnl((x0-lineLocations[j])*colorLenMults, result);
                ar += d*result[0];
                ai += d*result[1];
                d = -d;
            }
            //Находим интенсивность в точке
            double val = (ar*ar + ai*ai)/2;
            setFunction(i,0,val);
        }
    }

    @Override
    public void setParams(int length, double size) {
        this.length=length;
        this.size=size;
        colorLenMults = Math.exp(length/110.)*650/510.;
    }
}
