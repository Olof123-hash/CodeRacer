package UI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * Applies the theme to the frames of the application. The race windows are undecorated, so they
 * get their own thin frame from this class: a dark background, a subtle outline and a small accent
 * line at the top instead of the title bar of the operating system.
 *
 * <p>Every window can also be closed with the Escape key, because an undecorated window has no
 * close button of its own.
 *
 * <p>This class also knows where the popup windows (the game over screen and the history) should
 * be placed. They are put next to the race window whenever the screen has room for that, so they
 * never cover the buttons of the race screen.
 */
public final class WindowStyler {

  /** The corners of the race window that a popup can fall back to. */
  public enum Corner {
    TOP_LEFT,
    TOP_RIGHT
  }

  private WindowStyler() {
    // Utility class, no instances
  }

  /** Styles a frame; call this before the components are packed so the border is included. */
  public static void apply(JFrame frame) {
    frame.setBackground(Theme.BACKGROUND);
    frame.getContentPane().setBackground(Theme.BACKGROUND);
    if (frame.isUndecorated()) {
      // A window without a title bar has no edges to drag either, so the border leaves a margin
      // around the content that the resize handler below turns into a resize edge.
      frame.setResizable(true);
      frame.getRootPane().setBorder(resizableFrameBorder());
      ResizeHandler.install(frame);
    }
    installEscapeToClose(frame.getRootPane(), frame::dispose);
  }

  /** Styles a dialog in the same way as a frame. */
  public static void apply(JDialog dialog) {
    dialog.setBackground(Theme.BACKGROUND);
    dialog.getContentPane().setBackground(Theme.BACKGROUND);
    if (dialog.isUndecorated()) {
      dialog.getRootPane().setBorder(frameBorder());
    }
    installEscapeToClose(dialog.getRootPane(), dialog::dispose);
  }

  /**
   * Applies the theme again to a window that is already on the screen, after the palette changed.
   * The components of the window keep the colours they were created with, so the window styles its
   * own content again; this method only takes care of the frame and its border.
   */
  public static void refresh(JFrame frame) {
    frame.setBackground(Theme.BACKGROUND);
    frame.getContentPane().setBackground(Theme.BACKGROUND);
    if (frame.isUndecorated()) {
      frame.getRootPane().setBorder(resizableFrameBorder());
    }
    frame.repaint();
  }

  /** The border of an undecorated window: an accent line on top and an outline around the frame. */
  public static Border frameBorder() {
    Border accentLine = BorderFactory.createMatteBorder(3, 0, 0, 0, Theme.ACCENT);
    Border outline = BorderFactory.createLineBorder(Theme.SUBTLE, 1);
    return BorderFactory.createCompoundBorder(accentLine, outline);
  }

  /** The width of the invisible edge of a window in which a drag resizes it. */
  private static final int RESIZE_MARGIN = 6;

  /** The smallest size a resized window can have. */
  private static final Dimension MIN_RESIZED = new Dimension(360, 240);

  /**
   * The frame border with an invisible margin around it. The margin belongs to the root pane, which
   * is not covered by the content, so mouse events there reach the resize handler.
   */
  private static Border resizableFrameBorder() {
    return BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(RESIZE_MARGIN, RESIZE_MARGIN, RESIZE_MARGIN, RESIZE_MARGIN),
        frameBorder());
  }

  /**
   * Resizes an undecorated window while an edge or a corner of its border is dragged. The listener
   * sits on the root pane, which owns the margin that {@link #resizableFrameBorder()} leaves around
   * the content.
   */
  private static final class ResizeHandler extends MouseAdapter {

    private static final int NONE = 0;
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 4;
    private static final int EAST = 8;

    private final JFrame frame;
    private int edge = NONE;
    private Point pressed;
    private Rectangle startBounds;

    private ResizeHandler(JFrame frame) {
      this.frame = frame;
    }

    private static void install(JFrame frame) {
      ResizeHandler handler = new ResizeHandler(frame);
      frame.getRootPane().addMouseListener(handler);
      frame.getRootPane().addMouseMotionListener(handler);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
      frame.getRootPane().setCursor(Cursor.getPredefinedCursor(cursorOf(edgeAt(event))));
    }

    @Override
    public void mouseExited(MouseEvent event) {
      frame.getRootPane().setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent event) {
      edge = edgeAt(event);
      pressed = event.getLocationOnScreen();
      startBounds = frame.getBounds();
    }

    @Override
    public void mouseReleased(MouseEvent event) {
      edge = NONE;
      pressed = null;
    }

    @Override
    public void mouseDragged(MouseEvent event) {
      if (edge == NONE || pressed == null) {
        return;
      }
      Point now = event.getLocationOnScreen();
      int dx = now.x - pressed.x;
      int dy = now.y - pressed.y;
      int x = startBounds.x;
      int y = startBounds.y;
      int width = startBounds.width;
      int height = startBounds.height;
      if ((edge & WEST) != 0) {
        x += dx;
        width -= dx;
      }
      if ((edge & EAST) != 0) {
        width += dx;
      }
      if ((edge & NORTH) != 0) {
        y += dy;
        height -= dy;
      }
      if ((edge & SOUTH) != 0) {
        height += dy;
      }
      // Keep the smallest size, and keep the edge opposite to the drag where it was
      if (width < MIN_RESIZED.width) {
        if ((edge & WEST) != 0) {
          x = startBounds.x + startBounds.width - MIN_RESIZED.width;
        }
        width = MIN_RESIZED.width;
      }
      if (height < MIN_RESIZED.height) {
        if ((edge & NORTH) != 0) {
          y = startBounds.y + startBounds.height - MIN_RESIZED.height;
        }
        height = MIN_RESIZED.height;
      }
      frame.setBounds(x, y, width, height);
    }

    /** Which edges of the window the mouse is near, as a combination of the constants above. */
    private int edgeAt(MouseEvent event) {
      int x = event.getX();
      int y = event.getY();
      int width = frame.getRootPane().getWidth();
      int height = frame.getRootPane().getHeight();
      int edge = NONE;
      if (y <= RESIZE_MARGIN) {
        edge |= NORTH;
      }
      if (y >= height - RESIZE_MARGIN) {
        edge |= SOUTH;
      }
      if (x <= RESIZE_MARGIN) {
        edge |= WEST;
      }
      if (x >= width - RESIZE_MARGIN) {
        edge |= EAST;
      }
      return edge;
    }

    private int cursorOf(int edge) {
      switch (edge) {
        case NORTH:
          return Cursor.N_RESIZE_CURSOR;
        case SOUTH:
          return Cursor.S_RESIZE_CURSOR;
        case WEST:
          return Cursor.W_RESIZE_CURSOR;
        case EAST:
          return Cursor.E_RESIZE_CURSOR;
        case NORTH | WEST:
          return Cursor.NW_RESIZE_CURSOR;
        case NORTH | EAST:
          return Cursor.NE_RESIZE_CURSOR;
        case SOUTH | WEST:
          return Cursor.SW_RESIZE_CURSOR;
        case SOUTH | EAST:
          return Cursor.SE_RESIZE_CURSOR;
        default:
          return Cursor.DEFAULT_CURSOR;
      }
    }
  }

  /** Makes Escape and Ctrl+W close the given window. */
  public static void installEscapeToClose(JRootPane rootPane, Runnable closeAction) {
    Action action =
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent event) {
            closeAction.run();
          }
        };
    rootPane.getActionMap().put("coderacer.closeWindow", action);
    rootPane
        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "coderacer.closeWindow");
    rootPane
        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(
            KeyStroke.getKeyStroke(KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK),
            "coderacer.closeWindow");
  }

  /** The text of the fullscreen button while the window is windowed. */
  private static final String FULLSCREEN_TEXT = "Fullscreen";

  /** The text of the fullscreen button while the window fills the screen. */
  private static final String WINDOWED_TEXT = "Windowed";

  /**
   * Creates a button that switches the window that contains {@code context} between fullscreen and
   * windowed. The context can be the window itself or a component inside it.
   */
  public static JButton fullscreenButton(Component context) {
    JButton button = new JButton(FULLSCREEN_TEXT);
    ButtonStyler.applyGhost(button);
    button.addActionListener(
        event -> {
          toggleFullscreen(context);
          button.setText(isFullscreen(context) ? WINDOWED_TEXT : FULLSCREEN_TEXT);
        });
    return button;
  }

  /**
   * Puts the window that contains {@code context} into fullscreen, or brings it back to its windowed
   * size and position. The button text follows the current state.
   */
  public static void toggleFullscreen(Component context) {
    Window window = windowOf(context);
    if (window == null || window.getGraphicsConfiguration() == null) {
      return;
    }
    GraphicsDevice device = window.getGraphicsConfiguration().getDevice();
    if (!device.isFullScreenSupported()) {
      // Not every environment supports the exclusive fullscreen mode, maximizing is the closest
      toggleMaximized(window);
      return;
    }
    // Leaving fullscreen gives the window its old size and position back
    device.setFullScreenWindow(device.getFullScreenWindow() == window ? null : window);
  }

  /** Tells whether the window that contains the component currently fills the screen. */
  public static boolean isFullscreen(Component context) {
    Window window = windowOf(context);
    if (window == null || window.getGraphicsConfiguration() == null) {
      return false;
    }
    if (window.getGraphicsConfiguration().getDevice().getFullScreenWindow() == window) {
      return true;
    }
    // The fallback for screens without fullscreen support maximizes the window instead
    return window instanceof Frame
        && (((Frame) window).getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
  }

  /** Used instead of the exclusive fullscreen mode when the screen does not support it. */
  private static void toggleMaximized(Window window) {
    if (!(window instanceof Frame)) {
      return;
    }
    Frame frame = (Frame) window;
    if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
      frame.setExtendedState(Frame.NORMAL);
    } else {
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }
  }

  /** The window that owns a component, or the component itself when it already is a window. */
  private static Window windowOf(Component context) {
    return context instanceof Window ? (Window) context : SwingUtilities.getWindowAncestor(context);
  }

  /**
   * Places a popup window next to the race window, so that it does not cover the buttons of the
   * race screen. The right side is tried first, then the left side, then below and above the race
   * window. When the screen is too small for that, the popup is put in the given top corner of the
   * race window, where the start, history and language controls stay free. Windows that are passed
   * as {@code avoid} (for example the other popup) are not covered either.
   */
  public static void placePopup(Component parent, Window popup, Corner fallbackCorner, Window... avoid) {
    Rectangle parentBounds = boundsOnScreen(parent);
    if (parentBounds == null) {
      // The race window is not visible (yet), so there is nothing to place the popup next to
      popup.setLocationRelativeTo(null);
      return;
    }
    Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    int gap = 16;
    int width = popup.getWidth();
    int height = popup.getHeight();
    int top = clamp(parentBounds.y, screen.y + gap, screen.y + screen.height - height - gap);

    // The places where the popup could go, in the order in which they are tried. The first one
    // that is completely on the screen and that does not cover another popup is used.
    int[][] candidates = {
      // Right of the race window
      {parentBounds.x + parentBounds.width + gap, top},
      // Left of the race window
      {parentBounds.x - width - gap, top},
      // Below the race window
      {parentBounds.x, parentBounds.y + parentBounds.height + gap},
      // Above the race window
      {parentBounds.x, parentBounds.y - height - gap},
      // In one of the top corners of the race window
      {
        fallbackCorner == Corner.TOP_RIGHT
            ? parentBounds.x + parentBounds.width - width - gap
            : parentBounds.x + gap,
        parentBounds.y + gap
      },
      {
        fallbackCorner == Corner.TOP_RIGHT
            ? parentBounds.x + gap
            : parentBounds.x + parentBounds.width - width - gap,
        parentBounds.y + gap
      }
    };

    Rectangle firstChoice = null;
    for (int[] candidate : candidates) {
      Rectangle bounds =
          new Rectangle(candidate[0], candidate[1], width, height);
      if (!contains(screen, bounds)) {
        continue;
      }
      if (firstChoice == null) {
        firstChoice = bounds;
      }
      if (!overlapsAny(bounds, avoid)) {
        popup.setLocation(bounds.getLocation());
        return;
      }
    }
    if (firstChoice != null) {
      popup.setLocation(firstChoice.getLocation());
    } else {
      popup.setLocationRelativeTo(null);
    }
  }

  /** Returns the bounds of a component on the screen, or null when it is not visible. */
  private static Rectangle boundsOnScreen(Component component) {
    if (component == null || !component.isShowing()) {
      return null;
    }
    try {
      Point location = component.getLocationOnScreen();
      return new Rectangle(location, component.getSize());
    } catch (IllegalComponentStateException e) {
      return null;
    }
  }

  /** Checks whether a rectangle is completely inside another one. */
  private static boolean contains(Rectangle outer, Rectangle inner) {
    return inner.x >= outer.x
        && inner.y >= outer.y
        && inner.x + inner.width <= outer.x + outer.width
        && inner.y + inner.height <= outer.y + outer.height;
  }

  /** Checks whether a rectangle overlaps one of the visible windows that should be avoided. */
  private static boolean overlapsAny(Rectangle bounds, Window... avoid) {
    for (Window window : avoid) {
      if (window == null || !window.isVisible()) {
        continue;
      }
      if (bounds.intersects(new Rectangle(window.getLocation(), window.getSize()))) {
        return true;
      }
    }
    return false;
  }

  /** Keeps a value between a minimum and a maximum, used to keep popups on the screen. */
  private static int clamp(int value, int minimum, int maximum) {
    return Math.max(minimum, Math.min(value, maximum));
  }
}
