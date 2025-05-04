package concrete.goonie.core.axis;


import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;

/**
 * The {@code Axis} class is an abstract base class for chart axis renderers (e.g., X and Y axes).
 * It provides common configuration such as tick length, axis position, and chart settings.
 * <p>
 * Subclasses must implement the actual rendering logic by extending this class and
 * customizing axis-specific behaviors.
 *
 * @see AxisPosition
 * @see ChartConfig
 * @see XAxis
 * @see YAxis
 */
abstract class Axis extends Renderer {

    /**
     * The length of the axis ticks in pixels.
     */
    protected int tickLength = 3;

    /**
     * The position of the axis relative to the chart (e.g., LEFT, RIGHT, TOP, BOTTOM).
     */
    protected AxisPosition position = AxisPosition.RIGHT;

    Axis(ChartConfig config) {
        super(config);
    }

    /**
     * Sets the position of the axis.
     *
     * @param position the position of the axis (e.g., LEFT, RIGHT)
     */
    public void setPosition(AxisPosition position) {
        this.position = position;
    }

    /**
     * Returns the current position of the axis.
     *
     * @return the axis position
     */
    public AxisPosition getPosition() {
        return position;
    }

    /**
     * Sets the chart configuration used by the axis.
     *
     * @param config the {@link ChartConfig} instance containing style and rendering preferences
     */
    public void setConfig(ChartConfig config) {
        this.config = config;
    }
}
