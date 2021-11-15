package picture;

import java.util.Objects;

/**
 * Encapsulate the colours using the RGB direct color-model. The individual red, green and blue
 * components of a colour are assigned a value ranging from 0 to 255. A component value of 0
 * signifies no contribution is made to the color.
 */
public class Color {

  private final int red;
  private final int green;
  private final int blue;

  /**
   * Constructs a new Color object with the specified intensity values for the red, green and blue
   * components.
   *
   * @param red   the intensity of the red component in this Color.
   * @param green the intensity of the green component in this Color.
   * @param blue  the intensity of the blue component in this Color.
   */
  public Color(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public int getBlue() {
    return blue;
  }

  public int getGreen() {
    return green;
  }

  public int getRed() {
    return red;
  }

  public Color inverted() {
    return new Color(
        255 - this.red,
        255 - this.green,
        255 - this.blue
    );
  }

  public Color grayscaled() {
    final var avg = (this.red + this.green + this.blue) / 3;
    return new Color(avg, avg, avg);
  }

  @Override
  public String toString() {
    return "Color{" +
        "red=" + red +
        ", green=" + green +
        ", blue=" + blue +
        '}';
  }

  public Color add(Color right) {
    final var left = this;

    return new Color(
        left.red + right.red,
        left.green + right.green,
        left.blue + right.blue
    );
  }

  public Color multiply(Double k) {
    return new Color(
        (int) (k * this.red + 0.01), // +.01 so floating point errors
        (int) (k * this.green + 0.01), // don't effect what anything is rounded to
        (int) (k * this.blue + 0.01)
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Color color = (Color) o;
    return red == color.red &&
        green == color.green &&
        blue == color.blue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(red, green, blue);
  }
}
