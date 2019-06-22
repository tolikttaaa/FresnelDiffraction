package modeling.apertures;

public class DoubleSlitAperture extends OneDimensionalAperture {

    public DoubleSlitAperture(int length, double size,int grid) {
        super(length, size,grid);
    }

    @Override
    public void setUserParams(int length, double size) {
        if(size>0.125)size=0.125;
        super.setUserParams(length,size);
        lineX = new double[lineCount = 4];
        lineX[0] = -size;
        lineX[1] = -0.125;
        lineX[2] =  0.125;
        lineX[3] =  size;
    }
    boolean hasSymmetryX() { return true; }
}
