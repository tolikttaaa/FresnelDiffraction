package modeling.apertures;

import static java.lang.Math.PI;

public class CircularAperture extends Aperture {

    //Количество шагов при обходе
    int angleSteps;
    int angleStepsMask;

    //Значения косинуса и синуса для каждого шага
    //Чем больше шагов, тем точнее рассчет
    double[] angcos1;
    double[] angsin1;
    //Константа для точности вычисления интенсивности
    static final int fixedPoint = 2048;

    static final double pi2 = PI * 2;

    double apertureArgMult;

    long[] angcos2;
    long[] angsin2;
    int angleSteps2;
    int angleSteps2Mask;


    public CircularAperture(int length, double size,int grid) {
        super(length, size,grid);

        angleSteps =1024;
        angleStepsMask = angleSteps-1;

        angcos1 = new double[angleSteps];
        angsin1 = new double[angleSteps];


        int i;
        for (i = 0; i != angleSteps; i++) {
            angcos1[i] = java.lang.Math.cos(i*pi2/angleSteps);
            angsin1[i] = java.lang.Math.sin(i*pi2/angleSteps);
        }

        angleSteps2 = 4096;
        angleSteps2Mask = angleSteps2-1;
        angcos2 = new long[angleSteps2];
        angsin2 = new long[angleSteps2];

        for (i = 0; i != angleSteps2; i++) {
            angcos2[i] = (long)
                    (java.lang.Math.cos(i*pi2/angleSteps2) * fixedPoint);
            angsin2[i] = (long)
                    (java.lang.Math.sin(i*pi2/angleSteps2) * fixedPoint);
        }
    }

    void setFunction(int i, int j) {
        double ard = ((double) accumR)/(angleSteps*fixedPoint);
        double aid = ((double) accumI)/(angleSteps*fixedPoint);
        double mag = ard*ard + aid*aid;
        setFunction(i,j,mag);
    }


    //Фиксирует начало отверстия на радиусе r, считает интеграл
    void apertureStart(double r) {
        double r2 = r*r;
        int arg = ((int) (r2*apertureArgMult)) & angleSteps2Mask;
        accumR -= angcos2[arg];
        accumI -= angsin2[arg];
    }

    //Фиксирует конец отверстия на радиусе r, считает интеграл
    void apertureStop(double r) {
        double r2 = r*r;
        int arg = ((int) (r2*apertureArgMult)) & angleSteps2Mask;
        accumR += angcos2[arg];
        accumI += angsin2[arg];
    }

    //Учитывает геометрическую тень
    void apertureStartOrigin(boolean x) {
        if (x) {
            accumR -= fixedPoint*angleSteps;
        }
    }

    void compute() {
        int i, j;
        //В силу симметрии достаточно заполнить только треугольник
        //Потом его поотражаем и получим функцию на всей площадке
        for (i = 0; i != gridSizeX / 2; i++) {
            for (j = 0; j <= i; j++) {
                //Очищаем аккумулятор
                clearAccum();

                //Начало координат поместим в середину площадки интегрирования
                //И положим единичный отрезок, равный длине этой площидки

                //x0 y0 - точка на плоскости интегрирования (это будут все точки заполняемого треугольника)
                double x0 = (i/(double) gridSizeX)-.5;
                double y0 = (j/(double) gridSizeY)-.5;

                //cx cy - точка, центрально симметричная x0 y0
                double cx = -x0;
                double cy = -y0;

                //Квадрат расстояния от начала координат до точки
                double c2 = cx*cx+cy*cy;
                //Радиус отверстия
                double cr = size;
                //Разность квадратов расстояния и радиуса отверстия
                double c = c2 - cr*cr;


                double th1 = 0;
                double th2 = pi2;

                //Если точка проециреутся за пределы отверстия отверстия
                if (c>0) {
                    //То нампридется вычислить угол тетта
                    //Находим расстояние до центра координат
                    double r = java.lang.Math.sqrt(c2);

                    //Косинус угла на экране
                    double cx1 = cx/r;
                    //Синус того же угла
                    double cy1 = cy/r;

                    //Находим изменение угла тетта через арктангентсы
                    double a1 = java.lang.Math.atan2(cy-cx1*cr, cx+cy1*cr);
                    double a2 = java.lang.Math.atan2(cy+cx1*cr, cx-cy1*cr);
                    th1 = Math.min(a1,a2);
                    th2=Math.max(a1,a2);

                    //Если направления лучей различаются больше, чем на Пи
                    //То мы взяли их не в том направлении
                    //Исправляем это
                    if (th2-th1 > PI) {
                        th1 = th2;
                        th2 = Math.min(a1,a2)+pi2;
                    }
                }
                //Готовися к итерации
                int th1i = (int) ((th1*angleSteps)/pi2);
                int th2i = (int) ((th2*angleSteps)/pi2);
                while (th1i < 0) {
                    th1i += angleSteps;
                    th2i += angleSteps;
                }

                //Эта константа пригодится дальше - вынесем ее
                double ac4 = c*4;

                int th;
                //Перебираем все углы
                for (th = th1i; th < th2i; th++) {
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
                    //Когда находим пересечение с кругом, считаем интегральную сумму
                    if (r1 > 0)
                        apertureStart(r1);
                    apertureStop(r2);
                }
                apertureStartOrigin(c < 0);
                setFunction(i, j);
            }
        }
    }

    @Override
    public void setParams(int length, double size) {
        this.length=length;
        this.size=size;
        colorLenMults = Math.exp(length/110.)*650/510.;

        apertureArgMult = angleSteps2/4;
        apertureArgMult *= colorLenMults*colorLenMults;
    }

    boolean hasXSymmetry() { return true; }
    boolean hasYSymmetry() { return true; }
    boolean hasDiagonalSymmetry() { return true; }
}