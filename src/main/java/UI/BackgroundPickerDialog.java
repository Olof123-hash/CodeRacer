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
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
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
import model.Settings;

/**
 * The window behind the "Change background" setting. It is the background counterpart of
 * {@link ColourPickerDialog}: the images that come with the application are shown as previews, and
 * under them the same grid of ready made colours and a hex field, so a plain colour is one click
 * away and does not need a setting of its own.
 *
 * <p>Clicking an image or a colour applies it straight away and closes the window, so there is no
 * OK button; Escape and Cancel close it without changing anything.
 */
public final class BackgroundPickerDialog extends JDialog {

  // The size of one colour of the grid, the space between two of them and how many fit in a row.
  // They match the colour window, so both pickers look the same.
  private static final int SWATCH_SIZE = 28;
  private static final int SWATCH_GAP = 6;
  private static final int COLUMNS = 8;
  private static final int SWATCH_RADIUS = 8;

  // The size of one preview of a background image; two of them fill the width of the colour grid
  private static final int TILE_WIDTH = 128;
  private static final int TILE_HEIGHT = 72;

  /** What the player picked: the name of a background, and the colour of the colour entry. */
  public static final class Choice {

    /** A name from {@link BackgroundPanel}, or {@link Settings#BACKGROUND_CUSTOM_COLOUR}. */
    public final String background;

    /** The colour, which only means something for {@link Settings#BACKGROUND_CUSTOM_COLOUR}. */
    public final Color colour;

    private Choice(String background, Color colour) {
      this.background = background;
      this.colour = colour;
    }
  }

  // The background and the colour that are in use now, drawn with a ring around them
  private final String currentName;
  private final Color currentColour;

  // The choice of the player, or null while the window is open
  private Choice picked;

  /**
   * Opens the window and waits until the player picked a background or closed it. Returns the
   * choice, or null when nothing was picked.
   */
  public static Choice showDialog(Component parent, String background, Color colour) {
    Window owner = null;
    if (parent instanceof Window) {
      owner = (Window) parent;
    } else if (parent != null) {
      owner = SwingUtilities.getWindowAncestor(parent);
    }
    BackgroundPickerDialog dialog = new BackgroundPickerDialog(owner, background, colour);
    dialog.setVisible(true);
    return dialog.picked;
  }

  private BackgroundPickerDialog(Window owner, String background, Color colour) {
    super(owner, Dialog.ModalityType.APPLICATION_MODAL);
    this.currentName = background;
    this.currentColour = colour == null ? Theme.BACKGROUND : colour;
    setUndecorated(true);
    WindowStyler.apply(this);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setContentPane(buildContent());
    pack();
    setResizable(false);
    setLocationRelativeTo(owner);
  }

  /** Builds the window: a heading, the images, the grid of colours and the hex row. */
  private JPanel buildContent() {
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(Theme.BACKGROUND);
    content.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

    JLabel heading = new JLabel("Background");
    LabelStyler.applyTitle(heading, 14);
    heading.setAlignmentX(LEFT_ALIGNMENT);
    content.add(heading);

    content.add(Box.createVerticalStrut(5));
    JLabel hint = new JLabel("Click an image, or a colour for a plain background.");
    LabelStyler.applyMuted(hint);
    hint.setAlignmentX(LEFT_ALIGNMENT);
    content.add(hint);

    content.add(Box.createVerticalStrut(16));
    content.add(buildImageGrid());

    content.add(Box.createVerticalStrut(18));
    content.add(sectionCaption("COLOUR"));
    content.add(Box.createVerticalStrut(10));
    content.add(buildColourGrid());

    content.add(Box.createVerticalStrut(16));
    content.add(buildHexRow());
    return content;
  }

  /** A quiet caption above one group of the window. */
  private JLabel sectionCaption(String text) {
    JLabel caption = new JLabel(text);
    LabelStyler.applyCaption(caption);
    caption.setAlignmentX(LEFT_ALIGNMENT);
    return caption;
  }

  /** The images that come with the application, two in a row. */
  private JPanel buildImageGrid() {
    String[] names = BackgroundPanel.imageBackgroundNames();
    JPanel grid = new JPanel(new GridLayout(0, 2, SWATCH_GAP, SWATCH_GAP));
    grid.setOpaque(false);
    grid.setAlignmentX(LEFT_ALIGNMENT);
    for (String name : names) {
      grid.add(new ImageTile(name));
    }
    return grid;
  }

  /** The grid of ready made colours, the same one the colour window shows. */
  private JPanel buildColourGrid() {
    JPanel grid =
        new JPanel(
            new GridLayout(
                ColourPickerDialog.PRESET_COLOURS.length / COLUMNS,
                COLUMNS,
                SWATCH_GAP,
                SWATCH_GAP));
    grid.setOpaque(false);
    grid.setAlignmentX(LEFT_ALIGNMENT);
    for (int rgb : ColourPickerDialog.PRESET_COLOURS) {
      grid.add(new Swatch(new Color(rgb)));
    }
    return grid;
  }

  /** The row under the grid: the hex field of the colour in use, and the cancel button. */
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
    JTextField field = new JTextField(String.format("%06X", currentColour.getRGB() & 0xffffff));
    field.setFont(Theme.mono(14));
    field.setForeground(Theme.TEXT);
    field.setBackground(Theme.BACKGROUND_ALT);
    field.setCaretColor(Theme.ACCENT);
    field.setSelectionColor(Theme.withAlpha(Theme.ACCENT, 70));
    field.setSelectedTextColor(Theme.BACKGROUND);
    field.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    field.setPreferredSize(new Dimension(112, 34));
    field.setMaximumSize(new Dimension(112, 34));
    field.setToolTipText("A hex value such as 101018, press Enter to use it");
    field.addActionListener(
        event -> {
          Color colour = ColourPickerDialog.parseHex(field.getText());
          if (colour == null) {
            // The text was not a colour, so the field goes back to the current one
            field.setText(String.format("%06X", currentColour.getRGB() & 0xffffff));
            return;
          }
          pickColour(colour);
        });
    return field;
  }

  /** Applies a colour as a plain background and closes the window. */
  private void pickColour(Color colour) {
    picked = new Choice(Settings.BACKGROUND_CUSTOM_COLOUR, colour);
    dispose();
  }

  /** One preview of a background image. */
  private final class ImageTile extends JComponent {

    private final String name;
    private final BufferedImage preview;
    private boolean hovered;

    private ImageTile(String name) {
      this.name = name;
      this.preview = BackgroundPanel.thumbnail(name, TILE_WIDTH, TILE_HEIGHT);
      setPreferredSize(new Dimension(TILE_WIDTH, TILE_HEIGHT));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setToolTipText(name);
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
              picked = new Choice(name, null);
              dispose();
            }
          });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      int inset = 1;

      // The image is clipped to a rounded rectangle, so every preview has the same soft shape
      RoundRectangle2D shape =
          new RoundRectangle2D.Float(
              inset, inset, getWidth() - inset * 2, getHeight() - inset * 2,
              SWATCH_RADIUS, SWATCH_RADIUS);
      if (preview != null) {
        graphics2D.clip(shape);
        graphics2D.drawImage(
            preview, inset, inset, getWidth() - inset * 2, getHeight() - inset * 2, this);
        graphics2D.setClip(null);
      } else {
        graphics2D.setColor(Theme.BACKGROUND_ALT);
        graphics2D.fill(shape);
      }

      boolean selected = name.equals(currentName);
      if (selected) {
        graphics2D.setColor(Theme.ACCENT);
        graphics2D.setStroke(new BasicStroke(2.0f));
      } else if (hovered) {
        graphics2D.setColor(Theme.ACCENT_BRIGHT);
        graphics2D.setStroke(new BasicStroke(1.4f));
      } else {
        graphics2D.setColor(Theme.withAlpha(Theme.TEXT, 45));
        graphics2D.setStroke(new BasicStroke(1.0f));
      }
      graphics2D.draw(shape);
      graphics2D.dispose();
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
              pickColour(colour);
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
      boolean selected =
          Settings.BACKGROUND_CUSTOM_COLOUR.equals(currentName)
              && (colour.getRGB() & 0xffffff) == (currentColour.getRGB() & 0xffffff);
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
