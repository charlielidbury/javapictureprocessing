package picture;

public class Point extends Matrix {

  Point(double x, double y) {
    super(1, 2, new Double[][]{
        new Double[]{x},
        new Double[]{y}
    });
  }

  public double getX() {
    return this.vals[0][0];
  }

  public double getY() {
    return this.vals[1][0];
  }

  @Override
  public String toString() {
    return "(" + this.getX() + ", " + this.getY() + ")";
  }
}
