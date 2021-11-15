package picture;

public class Pixel {

  public Point pos;
  public Color color;

  Pixel(Point pos, Color color) {
    this.pos = pos;
    this.color = color;
  }

  @Override
  public String toString() {
    return "Pixel{" +
        "pos=" + pos +
        ", color=" + color +
        '}';
  }
}
