package modeling.apertures;

public class SlitAperture extends OneDimensionalAperture {

    public SlitAperture(int length, double size,int grid) {
        super(length, size,grid);
    }

    @Override
    public void setUserParams(int length, double size) {
        super.setUserParams(length, size);
        lineX = new double[lineCount = 2];
        lineX[0] = -size;
        lineX[1] =  size;
    }
    boolean hasSymmetryX() { return true; }
}
