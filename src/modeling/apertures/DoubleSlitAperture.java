package modeling.apertures;

public class DoubleSlitAperture extends OneDimensionalAperture {

    public DoubleSlitAperture(int length, double size,int grid) {
        super(length, size,grid);
    }

    @Override
    public void setParams(int length, double size) {
        if(size>0.125)size=0.125;
        super.setParams(length,size);
        lineLocations = new double[lineCount = 4];
        lineLocations[0] = -size;
        lineLocations[1] = -0.125;
        lineLocations[2] =  0.125;
        lineLocations[3] =  size;
    }
    boolean hasXSymmetry() { return true; }
}
