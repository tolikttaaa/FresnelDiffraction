package modeling.apertures;

public class SlitAperture extends OneDimensionalAperture {

    public SlitAperture(int length, double size,int grid) {
        super(length, size,grid);
    }

    @Override
    public void setParams(int length, double size) {
        super.setParams(length, size);
        lineLocations = new double[lineCount = 2];
        lineLocations[0] = -size;
        lineLocations[1] =  size;
    }
    boolean hasXSymmetry() { return true; }
}
