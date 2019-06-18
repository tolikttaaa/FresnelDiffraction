package modeling;

import static java.lang.StrictMath.sqrt;

public class ZoneRadius {
    //from light to hole
    private double a;

    //from hole to screen
    private double b;

    //zone number
    private double n;

    //light lambda
    private double lambda;

    public ZoneRadius(double a, double b, double n, double lambda) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.lambda = lambda;
    }

    public double getRadius(){
        return sqrt(n*lambda/(1/a+1/b));
    }
}
