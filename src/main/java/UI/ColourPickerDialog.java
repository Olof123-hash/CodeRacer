package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A small window to pick a colour in, used by the colour settings of the settings window instead of
 * {@link javax.swing.JColorChooser}. That dialog belongs to the look of another decade: sliders,
 * tabs, a preview panel and a lot of controls for something as simple as choosing a colour.
 *
 * <p>This one shows a grid of ready made colours and a hex field for everything else. Clicking a
 * colour applies it straight away and closes the window, so there is no OK button to find; Escape
 * and Cancel close it without changing anything.
 */
public final class ColourPickerDialog extends JDialog {

  /**
   * The colours that are offered, eight to a row: a row of greys, a row of soft tints, then the
   * normal, the deep and the dark version of the same eight hues.
   */
  // Package private, because the background window offers the same colours as it does
  static final int[] PRESET_COLOURS = {
    0xffffff, 0xe4e4e7, 0xa1a1aa, 0x71717a, 0x3f3f46, 0x27272a, 0x18181b, 0x0a0a0a,
    0xfecaca, 0xfed7aa, 0xfef08a, 0xbbf7d0, 0xa5f3fc, 0xbfdbfe, 0xc4b5fd, 0xfbcfe8,
    0xef4444, 0xf97316, 0xeab308, 0x22c55e, 0x06b6d4, 0x3b82f6, 0x8b5cf6, 0xec4899,
    0xdc2626, 0xea580c, 0xca8a04, 0x16a34a, 0x0891b2, 0x2563eb, 0x7c3aed, 0xdb2777,
    0x7f1d1d, 0x9a3412, 0x854d0e, 0x14532d, 0x164e63, 0x1e3a8a, 0x4c1d95, 0x831843
  };

  // The size of one square of the grid, and the space between two of them
  private static final int SWATCH_SIZE = 28;
  private static final int SWATCH_GAP = 6;
  // How many colours are shown next to each other
  private static final int COLUMNS = 8;
  // The radius of the corners of a swatch
  private static final int SWATCH_RADIUS = 8;

  /** The colour that is selected now, which is drawn with a ring around it. */
  private final Color current;

  /** The colour the player picked, or null while the window is open. */
  private Color picked;

  /**
   * Opens the window and waits until the player picked a colour or closed it. Returns the picked
   * colour, or null when nothing was picked.
   */
  public static Color showDialog(Component parent, String title, Color current) {
    // getWindowAncestor returns null when the component is already a window, which is exactly what
    // the settings window passes in; the dialog would then open on its own instead of on top of it
    Window owner = null;
    if (parent instanceof Window) {
      owner = (Window) parent;
    } else if (parent != null) {
      owner = SwingUtilities.getWindowAncestor(parent);
    }
    ColourPickerDialog dialog = new ColourPickerDialog(owner, title, current);
    dialog.setVisible(true);
    return dialog.picked;
  }

  private ColourPickerDialog(Window owner, String title, Color current) {
    super(owner, Dialog.ModalityType.APPLICATION_MODAL);
    this.current = current == null ? Theme.TEXT : current;
    setUndecorated(true);
    WindowStyler.apply(this);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setContentPane(buildContent(title));
    pack();
    setResizable(false);
    setLocationRelativeTo(owner);
  }

  /** Builds the window: a heading, the grid of colours and the row with the hex field. */
  private JPanel buildContent(String title) {
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(Theme.BACKGROUND);
    content.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

    JLabel heading = new JLabel(title);
    LabelStyler.applyTitle(heading, 14);
    heading.setAlignmentX(LEFT_ALIGNMENT);
    content.add(heading);

    content.add(Box.createVerticalStrut(5));
    JLabel hint = new JLabel("Click a colour, or type a hex value.");
    LabelStyler.applyMuted(hint);
    hint.setAlignmentX(LEFT_ALIGNMENT);
    content.add(hint);

    content.add(Box.createVerticalStrut(16));
    content.add(buildGrid());

    content.add(Box.createVerticalStrut(16));
    content.add(buildHexRow());
    return content;
  }

  /** The grid of ready made colours. */
  private JPanel buildGrid() {
    JPanel grid =
        new JPanel(
            new GridLayout(PRESET_COLOURS.length / COLUMNS, COLUMNS, SWATCH_GAP, SWATCH_GAP));
    grid.setOpaque(false);
    grid.setAlignmentX(LEFT_ALIGNMENT);
    for (int rgb : PRESET_COLOURS) {
      grid.add(new Swatch(new Color(rgb)));
    }
    return grid;
  }

  /** The row under the grid: the hex field of the current colour, and the cancel button. */
  private JPanel buildHexRow() {
    JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);

    JLabel caption = new JLabel("HEX");
    LabelStyler.applyCaption(caption);
    row.add(caption);
    row.add(Box.createHorizontalStrut(10));
    row.add(buildHexField());
    row.add(Box.createHorizontalStrut(12));

    JButton cancel = new JButton("Cancel");
    ButtonStyler.applySecondary(cancel);
    cancel.setPreferredSize(new Dimension(96, 34));
    cancel.addActionListener(event -> dispose());
    row.add(cancel);
    return row;
  }

  /** The field for a colour that is not in the grid, applied with Enter. */
  private JTextField buildHexField() {
    JTextField field = new JTextField(String.format("%06X", current.getRGB() & 0xffffff));
    field.setFont(Theme.mono(14));
    field.setForeground(Theme.TEXT);
    field.setBackground(Theme.BACKGROUND_ALT);
    field.setCaretColor(Theme.ACCENT);
    field.setSelectionColor(Theme.withAlpha(Theme.ACCENT, 70));
    field.setSelectedTextColor(Theme.BACKGROUND);
    field.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    field.setPreferredSize(new Dimension(112, 34));
    field.setMaximumSize(new Dimension(112, 34));
    field.setToolTipText("A hex value such as FF8A1F, press Enter to use it");
    field.addActionListener(
        event -> {
          Color colour = parseHex(field.getText());
          if (colour == null) {
            // The text was not a colour, so the field goes back to the current one
            field.setText(String.format("%06X", current.getRGB() & 0xffffff));
            return;
          }
          picked = colour;
          dispose();
        });
    return field;
  }

  /**
   * Reads a hex colour such as {@code FF8A1F}, {@code #ff8a1f} or the short {@code f80}. Returns
   * null when the text is not a colour.
   */
  static Color parseHex(String text) {
    if (text == null) {
      return null;
    }
    String value = text.trim();
    if (value.startsWith("#")) {
      value = value.substring(1);
    }
    if (value.length() == 3) {
      // The short form repeats every digit, so f80 becomes ff8800
      StringBuilder expanded = new StringBuilder();
      for (char digit : value.toCharArray()) {
        expanded.append(digit).append(digit);
      }
      value = expanded.toString();
    }
    if (value.length() != 6) {
      return null;
    }
    try {
      return new Color(Integer.parseInt(value, 16) & 0xffffff);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** One clickable colour of the grid. */
  private final class Swatch extends JComponent {

    private final Color colour;
    private boolean hovered;

    private Swatch(Color colour) {
      this.colour = colour;
      setPreferredSize(new Dimension(SWATCH_SIZE, SWATCH_SIZE));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setToolTipText(String.format("#%06X", colour.getRGB() & 0xffffff));
      addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
              hovered = true;
              repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
              hovered = false;
              repaint();
            }

            @Override
            public void mouseClicked(MouseEvent event) {
              picked = colour;
              dispose();
            }
          });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      int size = Math.min(getWidth(), getHeight());
      int inset = 1;
      int box = size - inset * 2;
      graphics2D.setColor(colour);
      graphics2D.fillRoundRect(inset, inset, box, box, SWATCH_RADIUS, SWATCH_RADIUS);

      // The colour that is selected now keeps a ring, and a hovering swatch gets a lighter one
      boolean selected = (colour.getRGB() & 0xffffff) == (current.getRGB() & 0xffffff);
      if (selected) {
        graphics2D.setColor(Theme.ACCENT);
        graphics2D.setStroke(new BasicStroke(2.0f));
      } else if (hovered) {
        graphics2D.setColor(Theme.ACCENT_BRIGHT);
        graphics2D.setStroke(new BasicStroke(1.4f));
      } else {
        // A faint edge, so the light colours stay visible on the dark window
        graphics2D.setColor(Theme.withAlpha(Theme.TEXT, 45));
        graphics2D.setStroke(new BasicStroke(1.0f));
      }
      graphics2D.drawRoundRect(inset, inset, box - 1, box - 1, SWATCH_RADIUS, SWATCH_RADIUS);
      graphics2D.dispose();
    }
  }
}
