package modeling.apertures;

public abstract class Aperture {

    //Разбиение плоскости интегрирования
    int gridSizeX;
    int gridSizeY;

    //Параметры, заданные пользователем
    int length;
    double size;

    //Множитель для получения цвета из интенсивности
    double colorValMult;

    //Функция интенсивности
    //И ее максимум
    private double[][] intensity;
    private double max;

    //Аккумулятор - комплексное число
    //Это его действительная и мнимая части соответственно
    int accumR, accumI;


    //Мультипликатор длины волны
    double lambdaMult;

    public Aperture(int length, double size,int grid) {
        colorValMult =150;
        gridSizeX = gridSizeY = grid;
        intensity =new double[gridSizeX][gridSizeY];
        setUserParams(length,size);
    }

    //Метод для получения цвета из функции
    public int getColorValue(int i, int j) {
        int val = (int) (intensity[i][j] * colorValMult);
        if (val > 255)
            val = 255;
        return val;
    }

    public void calculateFunction(){
        //В этом методе рассчитается часть функции, достаточная для построения
        calculatePart();


        //Достроим функцию, воспользовавшись симметрией
        int maxx = hasSymmetryX() ? gridSizeX/2 : gridSizeX;
        int maxy = hasSymmetryY() ? gridSizeY/2 : gridSizeY;

        int i,j;
        if(oneDimensional()){
            for(i=0;i<maxx;i++)
                for(j=0;j<maxy;j++)
                    intensity[i][j]= intensity[i][0];
        }
        if (hasDiagonalSymmetry())
            for (i = 0; i != maxx; i++)
                for (j = 0; j <= i; j++)
                    intensity[j][i] = intensity[i][j];
        if (hasSymmetryX())
            for (i = 0; i != maxx; i++)
                for (j = 0; j != maxy; j++)
                    intensity[gridSizeX-1-i][j] = intensity[i][j];
        if (hasSymmetryY())
            for (i = 0; i != gridSizeX; i++)
                for (j = 0; j != maxy; j++)
                    intensity[i][gridSizeX-1-j] = intensity[i][j];
    }

    //Метод для установки значения функции интенсивности
    void setIntensity(int x, int y, double val){
        if(val>max)
            max=val;
        intensity[x][y]=val;
    }
    //Метод для вычисления достаточной части функции интенсивности
    abstract void calculatePart();
    //Метод для установки пользователем параметров
    public abstract void setUserParams(int length, double size);
    //Флаги симметричности
    boolean oneDimensional() { return false; }
    boolean hasSymmetryX() { return false; }
    boolean hasSymmetryY() { return false; }
    boolean hasDiagonalSymmetry() { return false; }
}
