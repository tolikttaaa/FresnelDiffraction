package modeling.apertures;

public abstract class Aperture {


    //Разбиение плоскости интегрирования
    int gridSizeX;
    int gridSizeY;

    //Параметры, заданные пользователем
    int length;
    double size;

    //Множитель для получения цвета из интенсивности
    double colorMult;

    //Функция интенсивности
    //И ее максимум
    private double[][] func;
    private double max;

    //Аккумулятор - комплексное число
    //Это его действительная и мнимая части соответственно
    int accumR, accumI;


    //Длина волны мы используем красную
    double colorLenMults;

    public Aperture(int length, double size,int grid) {
        gridSizeX = gridSizeY = grid;
        func=new double[gridSizeX][gridSizeY];
        setParams(length,size);
    }

    //Очистка аккумулятора
    void clearAccum() {
        accumR = accumI = 0;
    }


    //Метод для получения цвета из функции
    public int getColorValue(int i, int j) {
        int val = (int) (func[i][j] * colorMult);
        if (val > 255)
            val = 255;
        return val;
    }

    public void computeFunction(){
        max=0;
        //В этом методе рассчитается часть функции, достаточная для построения
        compute();
        //Устарновим нормальную яркость
        if(max!=0) colorMult=255f/max;
        colorMult=150;


        //Достроим функцию, воспользовавшись симметрией
        int maxx = hasXSymmetry() ? gridSizeX/2 : gridSizeX;
        int maxy = hasYSymmetry() ? gridSizeY/2 : gridSizeY;

        int i,j;
        if(oneDimensional()){
            for(i=0;i<maxx;i++)
                for(j=0;j<maxy;j++)
                    func[i][j]=func[i][0];
        }
        if (hasDiagonalSymmetry())
            for (i = 0; i != maxx; i++)
                for (j = 0; j <= i; j++)
                    func[j][i] = func[i][j];
        if (hasXSymmetry())
            for (i = 0; i != maxx; i++)
                for (j = 0; j != maxy; j++)
                    func[gridSizeX-1-i][j] = func[i][j];
        if (hasYSymmetry())
            for (i = 0; i != gridSizeX; i++)
                for (j = 0; j != maxy; j++)
                    func[i][gridSizeX-1-j] = func[i][j];
    }

    //Метод для установки значения функции интенсивности
    void setFunction(int x,int y,double val){
        if(val>max)
            max=val;
        func[x][y]=val;
    }
    //Метод для вычисления достаточной части функции интенсивности
    abstract void compute();
    //Метод для установки пользователем параметров
    public abstract void setParams(int length, double size);
    //Флаги симметричности
    boolean oneDimensional() { return false; }
    boolean hasXSymmetry() { return false; }
    boolean hasYSymmetry() { return false; }
    boolean hasDiagonalSymmetry() { return false; }
}
