package edu.wpi.grip.core.operations.composite;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.wpi.grip.core.Description;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHints;

import java.util.List;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.Rect;


/**
 * Scale an image to an exact width and height using one of several interpolation modes.  Scaling
 * images down can be a useful optimization, and scaling them up might be necessary for combining
 * multiple images that are different sizes.
 */
@Description(name = "Crop Image",
             summary = "Crop an image to an exact size",
             category = OperationDescription.Category.IMAGE_PROCESSING,
             iconName = "crop")
public class CropOperation implements Operation {

  private final InputSocket<Mat> inputSocket;
  private final InputSocket<Number> xSocket;
  private final InputSocket<Number> ySocket;

  private final InputSocket<Number> widthSocket;
  private final InputSocket<Number> heightSocket;
  private final InputSocket<Origin> originSocket;

  private final OutputSocket<Mat> outputSocket;

  @Inject
  @SuppressWarnings("JavadocMethod")
  public CropOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory
      outputSocketFactory) {
    this.inputSocket = inputSocketFactory.create(SocketHints.Inputs
        .createMatSocketHint("Input", false));
    this.xSocket = inputSocketFactory.create(SocketHints.Inputs
        .createNumberSpinnerSocketHint("X", 100));
    this.ySocket = inputSocketFactory.create(SocketHints.Inputs
        .createNumberSpinnerSocketHint("Y", 100));
    this.widthSocket = inputSocketFactory.create(SocketHints.Inputs
        .createNumberSpinnerSocketHint("Width", 50));
    this.heightSocket = inputSocketFactory.create(SocketHints.Inputs
        .createNumberSpinnerSocketHint("Height", 50));
    this.originSocket = inputSocketFactory
        .create(SocketHints.createEnumSocketHint("Interpolation", Origin.CENTER));

    this.outputSocket = outputSocketFactory.create(SocketHints.Outputs
        .createMatSocketHint("Output"));
  }

  @Override
  public List<InputSocket> getInputSockets() {
    return ImmutableList.of(
        inputSocket,
        xSocket,
        ySocket,
        widthSocket,
        heightSocket,
        originSocket
    );
  }

  @Override
  public List<OutputSocket> getOutputSockets() {
    return ImmutableList.of(
        outputSocket
    );
  }

  @Override
  public void perform() {
    final Mat input = inputSocket.getValue().get();
    final Number x = xSocket.getValue().get();
    final Number y = ySocket.getValue().get();
    final Number width = widthSocket.getValue().get();
    final Number height = heightSocket.getValue().get();

    final Origin origin = originSocket.getValue().get();

    final Rect regionOfInterest = new Rect(
            x.intValue() + (int)(origin.xOffsetMultiplier * width.intValue()),
            y.intValue() + (int)(origin.yOffsetMultiplier * height.intValue()),
            width.intValue(),
            height.intValue()
            );

    final Mat output = new Mat(input, regionOfInterest).clone();

    outputSocket.setValue(output);
  }

  private enum Origin {
    TOP_LEFT("Top Left", 0, 0),
    TOP_RIGHT("Top Right", -1, 0),
    BOTTOM_LEFT("Bottom Left", 0, -1),
    BOTTOM_RIGHT("Bottom Right", -1, -1),
    CENTER("Center", -.5, -.5);

    final String label;
    final double xOffsetMultiplier;
    final double yOffsetMultiplier;

    Origin(String label, double xOffsetMultiplier, double yOffsetMultiplier) {
      this.label = label;
      this.xOffsetMultiplier = xOffsetMultiplier;
      this.yOffsetMultiplier = yOffsetMultiplier;
    }

    @Override
    public String toString() {
      return label;
    }
  }
}
