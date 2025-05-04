package concrete.goonie.core;


import concrete.goonie.Chart;
import concrete.goonie.ChartConfig;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

/**
 * Handles mouse interactions for a {@link Chart} panel including dragging, zooming,
 * and transforming view via AffineTransform.
 * <p>
 * Supports panning in horizontal, vertical, or both directions, as well as
 * zooming via mouse wheel. Automatically adjusts the chart's AffineTransform
 * to reflect user interactions.
 */
public class ChartMouseHandler extends MouseAdapter implements MouseMotionListener, MouseWheelListener, KeyListener {

    private boolean autoScaleY = false;
    private double minVisibleY = Double.MAX_VALUE;
    private double maxVisibleY = Double.MIN_VALUE;

    private final Chart chartPanel;
    private AffineTransform transform;

    private double scaleX = 19.0;
    private double scaleY = 19.0;
    private double translateX = 120;
    private double translateY = 120;

    private double width, height;

    private boolean isDragging = false;
    private int lastX, lastY;
    private int panX;
    private double panY;

    private DragMode dragMode = DragMode.BOTH;


    private enum DragMode {
        NONE, HORIZONTAL, VERTICAL, BOTH
    }

    /**
     * Constructs a ChartMouseHandler that handles user interactions on the given chart.
     *
     * @param chartPanel the chart component to attach listeners to
     */
    public ChartMouseHandler(Chart chartPanel) {
        this.chartPanel = chartPanel;
        this.transform = new AffineTransform();
        resetView();

        chartPanel.setFocusable(true);
        chartPanel.addMouseMotionListener(this);
        chartPanel.addMouseListener(this);
        chartPanel.addMouseWheelListener(this);
        chartPanel.addKeyListener(this);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                translateX += 1 * scaleX;  // Pan left, adjusting for zoom level
                break;
            case KeyEvent.VK_RIGHT:
                translateX -= 1 * scaleX;  // Pan right, adjusting for zoom level
                break;
            case KeyEvent.VK_UP:
                translateY += 2 * scaleY;  // Pan up, adjusting for zoom level
                break;
            case KeyEvent.VK_DOWN:
                translateY -= 2 * scaleY;  // Pan down, adjusting for zoom level
                break;
            case KeyEvent.VK_J:
                translateToLastCandle(panX, panY);
                break;
        }
        updateTransform();
        chartPanel.repaint();

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        determineCursorAndDragMode(x, y);
    }

    /**
     * Adjusts the cursor type and sets the drag mode depending on the mouse position.
     */
    private void determineCursorAndDragMode(int x, int y) {
        boolean nearRight = x >= width - chartPanel.getConfig().getyPad();
        boolean nearBottom = y >= height - chartPanel.getConfig().getMarginBottom();

        if (nearRight && nearBottom) {
            setDefaultCursor();
        } else if (nearRight) {
            setVerticalCursor();
        } else if (nearBottom) {
            setHorizontalCursor();
        } else {
            setMoveCursor();
        }
    }

    private void setDefaultCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        dragMode = DragMode.NONE;
    }

    private void setVerticalCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        dragMode = DragMode.VERTICAL;
    }

    private void setHorizontalCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        dragMode = DragMode.HORIZONTAL;
    }

    private void setMoveCursor() {
        chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        dragMode = DragMode.BOTH;
    }

    /**
     * Invoked when a mouse button is pressed on the chart panel.
     * <p>
     * This method records the initial mouse position if the drag mode is not NONE,
     * allowing the chart to be dragged.
     *
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (dragMode != DragMode.NONE) {
            isDragging = true;
            lastX = e.getX();
            lastY = e.getY();
        }
    }

    /**
     * Invoked when the mouse is dragged on the chart panel.
     * <p>
     * This method calculates the movement of the mouse and translates the chart accordingly.
     * The chart can be moved horizontally, vertically, or both, depending on the current drag mode.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isDragging || dragMode == DragMode.NONE) return;

        int x = e.getX();
        int y = e.getY();
        int deltaX = x - lastX;
        int deltaY = y - lastY;

        handleDrag(deltaX, deltaY);

        lastX = x;
        lastY = y;

        updateTransform();
        chartPanel.repaint();
    }

    /**
     * Translates the chart based on drag direction and distance.
     */
    private void handleDrag(int deltaX, int deltaY) {
        switch (dragMode) {
            case HORIZONTAL -> translateX += deltaX;
            case VERTICAL -> translateY -= deltaY;
            case BOTH -> {
                translateX += deltaX;
                translateY -= deltaY;
            }
        }
    }

    /**
     * Invoked when a mouse button is released on the chart panel.
     * <p>
     * This method resets the dragging state.
     *
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;
    }

    /**
     * Invoked when the mouse wheel is moved over the chart panel.
     * <p>
     * This method zooms the chart in or out based on the mouse wheel rotation,
     * adjusting both the scale and the translation. Zooming can occur in different modes:
     * - Both X and Y axes (near bottom-right)
     * - Only X axis (near X edge)
     * - Only Y axis (near Y edge)
     *
     * @param e The mouse wheel event.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        double zoomFactor = e.getWheelRotation() > 0 ? 0.9 : 1.1;

        boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
        ChartConfig config = chartPanel.getConfig();
        boolean insideRight = mouseX <= width - config.getyPad();
        boolean insideBottom = mouseY <= height - config.getMarginBottom();

        if (ctrlDown) {
            // Anchor to mouse X, scale X only
            translateX -= (mouseX - translateX) * (zoomFactor - 1);
            scaleX *= zoomFactor;
        } else if (insideRight && insideBottom) {
            double flippedMouseY = height - mouseY;
            translateX -= (mouseX - translateX) * (zoomFactor - 1);
            if (!autoScaleY) translateY -= (flippedMouseY - translateY) * (zoomFactor - 1);
            scaleX *= zoomFactor;
            if (!autoScaleY) scaleY *= zoomFactor;

        } else if (insideRight) {
            double anchorX = width - 50;
            translateX -= (anchorX - translateX) * (zoomFactor - 1);
            scaleX *= zoomFactor;
        } else if (insideBottom) {

            double flippedAnchorY = height - (height / 2);
            translateY -= (flippedAnchorY - translateY) * (zoomFactor - 1);
            scaleY *= zoomFactor;

        }

        updateTransform();
        chartPanel.repaint();
    }

    /**
     * Resets the view to default zoom and translation.
     */
    public void resetView() {
        scaleX = 20;
        scaleY = 14;
        translateX = 0;
        translateY = 0;
        updateTransform();
        chartPanel.repaint();
    }

    /**
     * Sets the dimensions of the chart component.
     *
     * @param width  the width of the chart panel
     * @param height the height of the chart panel
     */
    public void setChartSize(double width, double height) {
        this.width = width;
        this.height = height;
        updateTransform();
    }

    /**
     * Updates the internal AffineTransform based on scale and translation.
     */
    private void updateTransform() {
        transform.setToIdentity();
        applyFlippingAndTranslation();
        applyZoom();
    }

    /**
     * Applies the flipping and translation transformations to the chart.
     * <p>
     * This method flips the Y-axis and translates the chart based on the current
     * translateX and translateY values. The Y-axis is flipped to have the origin at
     * the bottom-left corner of the chart.
     */
    private void applyFlippingAndTranslation() {
        transform.scale(1, -1);  // Flip the Y-axis
        transform.translate(translateX, -height + translateY);  // Apply translation
    }

    /**
     * Applies the zoom transformation to the chart.
     * <p>
     * This method scales the chart based on the current scaleX and scaleY values,
     * which control the zoom level for both the X and Y axes.
     */
    private void applyZoom() {
        transform.scale(scaleX, scaleY);  // Apply zoom for both X and Y axes
    }

    /**
     * Creates an axis-specific transformation matrix.
     * <p>
     * This method generates a transformation matrix that applies translation and scaling
     * for either the X-axis or the Y-axis, depending on the provided flag.
     * The Y-axis transformation is flipped to ensure the origin is at the bottom of the chart.
     *
     * @param isXAxis A boolean flag indicating whether the transformation is for the X-axis
     *                (if true) or the Y-axis (if false).
     * @return The transformation matrix for the specified axis.
     */
    private AffineTransform createAxisTransform(boolean isXAxis) {
        AffineTransform axisTransform = new AffineTransform();

        if (isXAxis) {
            axisTransform.translate(translateX, 0);  // Translate along X-axis
            axisTransform.scale(scaleX, 1);          // Apply scaling to X-axis
        } else {
            axisTransform.scale(1, -1);              // Flip Y-axis
            axisTransform.translate(0, -height + translateY);  // Apply Y translation
            axisTransform.scale(1, scaleY);          // Apply scaling to Y-axis
        }

        return axisTransform;
    }

    /**
     * Returns the current full AffineTransform used for rendering the chart.
     *
     * @return the full AffineTransform
     */
    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    /**
     * Returns the horizontal (X-axis) AffineTransform.
     *
     * @return the X-axis AffineTransform
     */
    public AffineTransform getXAxisTransform() {
        return createAxisTransform(true);
    }

    /**
     * Returns the vertical (Y-axis) AffineTransform.
     *
     * @return the Y-axis AffineTransform
     */
    public AffineTransform getYAxisTransform() {
        return createAxisTransform(false);
    }

    public void updateVisibleYRange(double minY, double maxY) {
        // Only update the visible range if auto-scaling is enabled
        if (autoScaleY) {
            minVisibleY = minY;
            maxVisibleY = maxY;
            adjustYScaleToFitContent();
        }
    }

    private void adjustYScaleToFitContent() {
        if (minVisibleY == Double.MAX_VALUE || maxVisibleY == Double.MIN_VALUE) return;

        double range = maxVisibleY - minVisibleY;
        double padding = range * 0.5;

        // Apply padding
        double paddedMinY = minVisibleY - padding;
        double paddedMaxY = maxVisibleY + padding;
        double paddedRange = paddedMaxY - paddedMinY;

        // Scale to fit padded range
        scaleY = (height * 0.9) / paddedRange;
        translateY = height - (paddedMaxY * scaleY);

        updateTransform();
    }

    public void setAutoScaleY(boolean autoScaleY) {
        boolean wasAutoScale = this.autoScaleY;
        this.autoScaleY = autoScaleY;

        if (autoScaleY && !wasAutoScale) {
            // Reset visible range when enabling auto-scale
            minVisibleY = Double.MAX_VALUE;
            maxVisibleY = Double.MIN_VALUE;
            chartPanel.repaint();  // This will trigger a new range calculation
        }
    }

    /**
     * Translates and scales the view to focus on the specified candle position
     *
     * @param time       The x-position (time) of the candle to center
     * @param value      The y-position (price) to center vertically
     * @param autoScaleX Whether to automatically adjust horizontal scaling
     * @param autoScaleY Whether to automatically adjust vertical scaling
     */
    public void translateToLastCandle(int time, double value, boolean autoScaleX, boolean autoScaleY) {
        // Calculate desired X translation
        double targetX = time; // Right padding of 10 units
        double centerX = width - width / 3;

        if (autoScaleX) {
            // Auto calculate scaleX to show about 50 candles
            scaleX = width / 50.0;
        }
        translateX = centerX - (targetX * scaleX);

        // Calculate desired Y translation
        double centerY = height / 2;

        if (autoScaleY) {
            scaleY = 0.7;

        }
        translateY = centerY - (value * scaleY);
        updateTransform();
        adjustYScaleToFitContent();
        chartPanel.repaint();
    }

    // Overloaded version with default scaling behavior
    public void translateToLastCandle(int time, double value) {
        translateToLastCandle(time, value, false, false);
    }
    public void setLastCandleValues(int time, double value) {
        this.panX = time;
        this.panY = value;
    }
}
