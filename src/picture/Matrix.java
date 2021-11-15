package picture;

import java.lang.Math;

public class Matrix {

  public int w;
  public int h;
  public Double[][] vals;

  static private Double sin(Double angle) {
    return Math.sin(angle * Math.PI / 180);
  }

  static private Double cos(Double angle) {
    return Math.cos(angle * Math.PI / 180);
  }

  static Matrix rotate(Double angle) {
    final Double[][] vals = {
        {cos(-angle), -sin(-angle)},
        {sin(-angle), cos(-angle)}
    };
    return new Matrix(2, 2, vals);
  }

  Matrix(int w, int h, Double[][] vals) {
    this.w = w;
    this.h = h;
    this.vals = vals;
  }

  Matrix(Matrix m) {
    this.w = m.w;
    this.h = m.h;
    this.vals = m.vals;
  }

  public static void main(String[] args) {
    final Double[][] v1 = {{2.0, 0.0}, {0.0, 2.0}};
    final var m1 = new Matrix(2, 2, v1);

    final var m2 = new Point(1, 2);

    final var result = m1.multiply(m2);
    final var x = result.getX();
    final var y = result.getY();

    System.out.println(x);
    System.out.println(y);
  }

  public Matrix multiply(Matrix right) {
    // renamed to left for clarity
    final var left = this;

    // makes width of left is same as height of right
    // otherwise matrix multiplication cannot occur
    assert (left.w == right.h);

    final var w = right.w;
    final var h = left.h;
    final Double[][] vals = new Double[h][w];

    for (var r = 0; r < left.h; r++) {
      for (var c = 0; c < right.w; c++) {
        var sum = 0.0;

        for (var i = 0; i < left.w; i++) {
          sum += left.vals[r][i] * right.vals[i][c];
        }

        vals[r][c] = sum;
      }
    }

    return new Matrix(w, h, vals);
  }

  public Point multiply(Point right) {
    // switches the point into a matrix, does the multiplication
    // then switches the result back to a point
    final var result = this.multiply((Matrix) right);

    return new Point(result.vals[0][0], result.vals[1][0]);
  }

  public Matrix multiply(Double scalar) {
    final var result = new Matrix(this);

    for (var y = 0; y < this.h; y++) {
      for (var x = 0; x < this.w; x++) {
        result.vals[y][x] *= scalar;
      }
    }

    return result;
  }

  public Matrix inverse() throws RuntimeException {
    if (this.w == 2 && this.h == 2) {
      // 2x2 matrix inverse
      final var a = this.vals[0][0];
      final var b = this.vals[0][1];
      final var c = this.vals[1][0];
      final var d = this.vals[1][1];

      final Double[][] vals = {
          {d, -b},
          {-c, a}
      };

      return new Matrix(2, 2, vals).multiply(1 / (a * d - b * c));
    } else {
      // if not hit above, inverse not implimented yet
      throw new RuntimeException(
          "Inverse not implimented for " + this.w + "x" + this.h + " matricies");
    }
  }

  public Double sum() {
    var total = 0.0;

    for (var y = 0; y < this.h; y++) {
      for (var x = 0; x < this.w; x++) {
        total += this.vals[y][x];
      }
    }

    return total;
  }

  public Matrix normalised() {
    // divides matrix by a scalar such that it's sum is 1
    return this.multiply(1 / this.sum());
  }
}
