package picture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A class that encapsulates and provides a simplified interface for manipulating an image. The
 * internal representation of the image is based on the RGB direct colour model.
 */
public class Picture {

  /**
   * The internal image representation of this picture.
   */
  private final BufferedImage image;
  private final Double horizontalRadius;
  private final Double verticalRadius;

  /**
   * Construct a new (blank) Picture object with the specified width and height.
   */
  public Picture(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    horizontalRadius = ((double) this.getWidth() - 1) / 2;
    verticalRadius = ((double) this.getHeight() - 1) / 2;
  }

  public Picture(int width, int height, Stream<Pixel> pixelStream) {
    this(width, height);

    pixelStream.forEach(p -> this.setPixel(p));
  }

  /**
   * Construct a new Picture from the image data in the specified file.
   */
  public Picture(String filepath) {
    try {
      image = ImageIO.read(new File(filepath));
      horizontalRadius = ((double) this.getWidth() - 1) / 2;
      verticalRadius = ((double) this.getHeight() - 1) / 2;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Picture(Picture picture) {
    this(picture.getWidth(), picture.getHeight(), picture.pixelStream());
  }

  /**
   * Test if the specified point lies within the boundaries of this picture.
   *
   * @param x the x co-ordinate of the point
   * @param y the y co-ordinate of the point
   * @return <tt>true</tt> if the point lies within the boundaries of the picture, <tt>false</tt>
   * otherwise.
   */
  public boolean contains(int x, int y) {
    return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
  }

  /**
   * Returns true if this Picture is graphically identical to the other one.
   *
   * @param other The other picture to compare to.
   * @return true iff this Picture is graphically identical to other.
   */
  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (!(other instanceof Picture)) {
      return false;
    }

    Picture otherPic = (Picture) other;

    if (image == null || otherPic.image == null) {
      return image == otherPic.image;
    }
    if (image.getWidth() != otherPic.image.getWidth()
        || image.getHeight() != otherPic.image.getHeight()) {
      return false;
    }

    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        if (image.getRGB(i, j) != otherPic.image.getRGB(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Return the height of the <tt>Picture</tt>.
   *
   * @return the height of this <tt>Picture</tt>.
   */
  public int getHeight() {
    return image.getHeight();
  }

  /**
   * Return the colour components (red, green, then blue) of the pixel-value located at (x,y).
   *
   * @param x x-coordinate of the pixel value to return
   * @param y y-coordinate of the pixel value to return
   * @return the RGB components of the pixel-value located at (x,y).
   * @throws ArrayIndexOutOfBoundsException if the specified pixel-location is not contained within
   *                                        the boundaries of this picture.
   */
  public Color getPixel(int x, int y) {
    // if out of bounds, return black
    if (x >= this.getWidth() || x < 0 || y >= this.getHeight() || y < 0) {
      return new Color(0, 0, 0);
    }

    int rgb = image.getRGB(x, y);
    return new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
  }

  public Color getPixel(Point point) {
    return this.getPixel(
        (int) (point.getX() + this.horizontalRadius + 0.5), // +.5 added for rounding
        (int) (this.verticalRadius - point.getY() + 0.5)
    );
  }

  /**
   * Return the width of the <tt>Picture</tt>.
   *
   * @return the width of this <tt>Picture</tt>.
   */
  public int getWidth() {
    return image.getWidth();
  }

  @Override
  public int hashCode() {
    if (image == null) {
      return -1;
    }
    int hashCode = 0;
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        hashCode = 31 * hashCode + image.getRGB(i, j);
      }
    }
    return hashCode;
  }

  public void saveAs(String filepath) {
    try {
      ImageIO.write(image, "png", new File(filepath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Update the pixel-value at the specified location.
   *
   * @param x   the x-coordinate of the pixel to be updated
   * @param y   the y-coordinate of the pixel to be updated
   * @param rgb the RGB components of the updated pixel-value
   * @throws ArrayIndexOutOfBoundsException if the specified pixel-location is not contained within
   *                                        the boundaries of this picture.
   */
  public void setPixel(int x, int y, Color rgb) {

    image.setRGB(
        x,
        y,
        0xff000000
            | (((0xff & rgb.getRed()) << 16)
            | ((0xff & rgb.getGreen()) << 8)
            | (0xff & rgb.getBlue())));
  }

  public void setPixel(Pixel pixel) {
    this.setPixel(
        (int) (pixel.pos.getX() + this.horizontalRadius + 0.5), // +.5 added for rounding
        (int) (this.verticalRadius - pixel.pos.getY() + 0.5),
        pixel.color
    );
  }

  /**
   * Returns a String representation of the RGB components of the picture.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        Color rgb = getPixel(x, y);
        sb.append("(");
        sb.append(rgb.getRed());
        sb.append(",");
        sb.append(rgb.getGreen());
        sb.append(",");
        sb.append(rgb.getBlue());
        sb.append(")");
      }
      sb.append("\n");
    }
    sb.append("\n");
    return sb.toString();
  }

  public Stream<Pixel> pixelStream() {
    return IntStream.range(0, this.getHeight())
        .mapToObj(y ->
            IntStream.range(0, this.getWidth())
                .mapToObj(x -> new Pixel(
                    this.getPoint(x, y),
                    this.getPixel(x, y)
                ))
        )
        .flatMap(o -> o);
  }

  public Point getPoint(int x, int y) {
    return new Point(x - horizontalRadius, verticalRadius - y);
  }

  public Picture add(Picture right) {
    final var left = new Picture(this);

    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        left.setPixel(
            x,
            y,
            left.getPixel(x, y)
                .add(right.getPixel(x, y))
        );
      }
    }

    return left;
  }

  public Picture multiply(Double scalar) {
    final var result = new Picture(this);

    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        result.setPixel(
            x,
            y,
            result.getPixel(x, y)
                .multiply(scalar)
        );
      }
    }

    return result;
  }

  public Picture mapColor(Function<Color, Color> mapFunc) {
    return this.map(pixel -> new Pixel(pixel.pos, mapFunc.apply(pixel.color)));
  }

  public Picture map(Function<Pixel, Pixel> mapFunc) {
    return new Picture(
        this.getWidth(),
        this.getHeight(),
        this.pixelStream().map(mapFunc::apply)
    );
  }

  public Picture inverted() {
    return this.mapColor(c -> c.inverted());
  }

  public Picture grayscaled() {
    return this.mapColor(c -> c.grayscaled());
  }

  public Picture transformed(Matrix transformation) {
    // uses two test points to determine resultant image size
    final var tl = this.getPoint(0, 0);
    final var tr = this.getPoint(this.getWidth() - 1, 0);
    // translates the test points
    final var ntl = transformation.multiply(tl);
    final var ntr = transformation.multiply(tr);
    // uses the location of those after transformation to determine size
    final var horizontalRadius = Math.max(Math.abs(ntl.getX()), Math.abs(ntr.getX()));
    final var verticalRadius = Math.max(Math.abs(ntl.getY()), Math.abs(ntr.getY()));
    // then calculates the size of the new image
    final var w = (int) (2 * horizontalRadius + 1);
    final var h = (int) (2 * verticalRadius + 1);
    // new picture
    final var result = new Picture(w, h);

    final var inverse = transformation.inverse();

    result.pixelStream()
        .forEach(p -> result.setPixel(new Pixel(
            p.pos,
            this.getPixel(inverse.multiply(p.pos))
        )));

    return result;
  }

  public Picture rotated(Double angle) {
    return this.transformed(Matrix.rotate(angle));
  }

  public Picture convoluted(Matrix kernal) {
    // check: kernal has a middle
    assert (kernal.h % 2 == 1 && kernal.w % 2 == 1);
    // horizontal and vertical kernal radii
    final var hkr = (kernal.w - 1) / 2;
    final var vkr = (kernal.h - 1) / 2;
    // copies into new picture
    final var result = new Picture(this);

    for (var x = 0; x < this.getWidth() - kernal.w + 1; x++) {
      for (var y = 0; y < this.getHeight() - kernal.h + 1; y++) {
        var r = 0.0; // had to do this all separately so I didn't have to
        var g = 0.0; // make a Color class that used Doubles, because
        var b = 0.0; // I can't have truncation
        for (var kx = 0; kx < kernal.w; kx++) {
          for (var ky = 0; ky < kernal.h; ky++) {
            final var kernelValue = kernal.vals[ky][kx];
            final var color = this.getPixel(x + kx, y + ky);
            r += kernelValue * color.getRed();
            g += kernelValue * color.getGreen();
            b += kernelValue * color.getBlue();
          }
        }
        // these 0.01 values make sure the floating point errors dont change what it's rounded to
        final var c = new Color((int) (r + 0.01), (int) (g + 0.01), (int) (b + 0.01));
        result.setPixel(x + hkr, y + vkr, c);
      }
    }

    return result;
  }

//  public Picture meanBlurred() {
//    return this.convoluted(new Matrix(3, 3, new Double[][]{
//            new Double[]{1.0, 1.0, 1.0},
//            new Double[]{1.0, 1.0, 1.0},
//            new Double[]{1.0, 1.0, 1.0}
//    }).normalised());
//  }

  public Picture blended(Stream<Picture> pictures) {
    final var result = new Picture(this.getWidth(), this.getHeight());

    // how frustrating having to make a stream into an array just to iterate over it!
    // I would love feedback on how to iterate over the stream without copying it.
    // .forEach seems to have some weird limitation about side effects in the function
    // you pass it so I can't use that
    // I've learnt that trying to get Java to be functional really doesn't assist
    // in writing elegant code
    final var pictureArray = pictures.toArray(Picture[]::new);
    final var n = pictureArray.length + 1;

    for (var x = 0; x < this.getWidth(); x++) {
      for (var y = 0; y < this.getHeight(); y++) {
        var total = this.getPixel(x, y);
        for (final var picture : pictureArray) {
          total = total.add(picture.getPixel(x, y));
        }
        result.setPixel(x, y, total.multiply(1.0 / n));
      }
    }

    return result;
  }
}
