package picture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class PictureProcessor {

  public static void main(String[] args) throws RuntimeException {
//    args = new String[]{"blur", "images/sunset64x32.png", "out/test.png"};

    // java pls add array destructuring
    final var commands = new ArrayList<String>(Arrays.asList(
        Arrays.copyOfRange(args, 0, args.length - 2)
    ));
    final var inputPath = args[args.length - 2];
    final var outputPath = args[args.length - 1];

    final var inputPicture = new Picture(inputPath);

    final var outputPicture = proccessCommands(inputPicture, commands);

    outputPicture.saveAs(outputPath);
  }

  private static Picture proccessCommands(Picture inputPicture, ArrayList<String> commands) {
    // base case:
    if (commands.isEmpty()) {
      return inputPicture;
    }

    // ingests the commands
    final var command = commands.remove(0);

    final Picture outputPicture = switch (command) {
      case "invert" -> inputPicture.inverted();
      case "grayscale" -> inputPicture.grayscaled();
      case "rotate" -> inputPicture.rotated(Double.parseDouble(commands.remove(0)));
      case "flip" -> inputPicture.transformed(switch (commands.remove(0)) {
        case "H" -> new Matrix(2, 2, new Double[][]{
            new Double[]{-1.0, 0.0},
            new Double[]{0.0, 1.0}
        });
        case "V" -> new Matrix(2, 2, new Double[][]{
            new Double[]{1.0, 0.0},
            new Double[]{0.0, -1.0}
        });
        default -> throw new RuntimeException("Invalid flip direction");
      });
      case "blend" -> inputPicture.blended(
          commands.stream().map(path -> new Picture(path))
      );
      case "blur" -> inputPicture.convoluted(new Matrix(3, 3, new Double[][]{
          new Double[]{1.0, 1.0, 1.0},
          new Double[]{1.0, 1.0, 1.0},
          new Double[]{1.0, 1.0, 1.0}
      }).normalised());
      case "matrix" -> inputPicture.transformed(new Matrix(2, 2, new Double[][]{
          new Double[]{
              Double.parseDouble(commands.remove(0)),
              Double.parseDouble(commands.remove(0))
          },
          new Double[]{
              Double.parseDouble(commands.remove(0)),
              Double.parseDouble(commands.remove(0))
          },
      }));
      default -> throw new RuntimeException("Invalid command: " + command);
    };

    if (command == "blend") {
      commands = new ArrayList<>();
    }

    return proccessCommands(outputPicture, commands);
  }
}
