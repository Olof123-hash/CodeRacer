package UI;

import database.SettingsStore;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import model.Settings;

/**
 * The settings window. It is opened from the race screen in the same way as the history and the
 * game over window, and it uses the same chrome: an undecorated frame with the dark border and the
 * accent line from {@link WindowStyler}, the minimise/fullscreen/close row from
 * {@link WindowControls} and Escape or Ctrl+W to close it.
 *
 * <p>The window edits the {@link Settings} object it is given. Every change is written into that
 * object and saved, so the race screen only has to read the settings it already holds; the window
 * also tells it about the change through {@link #addSettingsChangeListener(Runnable)}, which is how
 * the new colour scheme is applied while the window is still open.
 *
 * <p>The settings live in their own sections: a small monospaced title with a thin rule
 * under it, then the rows with the caption on the left and the control on the right. The type is the
 * type of the race screen: monospaced for everything that shows a value, the interface font for the
 * captions, and the same secondary buttons as the race window. There are no boxes around the
 * sections, the race screen does not use them either.
 *
 * <ul>
 *   <li>the colour scheme, chosen from a drop down that shows a swatch of every scheme; the "Pick
 *       own..." button opens a colour chooser for an accent colour of the player's own
 *       ({@link ColourScheme#CUSTOM})
 *   <li>the background behind the race screen, chosen from a drop down of the images in
 *       {@code src/main/resources/backgrounds}; picking one paints it behind the race screen right
 *       away
 *   <li>whether the player types code snippets or plain text, as a switch 
 *   <li>which kind of plain text is used, random words or a passage from a text
 * </ul>
 */
public class SettingsPanel extends javax.swing.JFrame {

  /** The size of the window: wide enough for a caption, a hint and a control on one row. */
  private static final Dimension WINDOW_SIZE = new Dimension(560, 790);

  /** The settings this window edits; the race screen keeps the same object. */
  private final Settings settings;

  // The controls, they are built in buildWindow()
  private JComboBox<String> schemeBox;
  private JButton backgroundButton;
  private JComboBox<String> textSourceBox;
  private JCheckBox codeModeSwitch;
  private JButton pickColourButton;
  private JButton caretButton;
  private JButton correctButton;
  private JButton errorButton;
  private JButton textButton;
  private JButton resetButton;
  private JSlider sizeSlider;
  private JLabel sizeValue;
  private JPanel textSourceRow;

  // The labels and the rules under the section titles, kept so refreshTheme can style them again
  // after a scheme change
  private JLabel heading;
  private final List<JLabel> sectionTitles = new ArrayList<>();
  private final List<JLabel> captions = new ArrayList<>();
  private final List<JLabel> hints = new ArrayList<>();
  private final List<JPanel> rules = new ArrayList<>();

  // The accent colour of the custom scheme, kept in sync with the settings object
  private Color customAccent;

  // The background on the button: a name from BackgroundPanel, or Settings.BACKGROUND_CUSTOM_COLOUR
  // together with the plain colour that is painted instead of an image
  private String backgroundName;
  private Color backgroundColour;

  // The colours of typing, kept in sync with the settings object
  private Color caretColour;
  private Color correctColour;
  private Color errorColour;
  private Color textColour;

  // The rest of the application can subscribe here instead of polling the window
  private final List<Runnable> changeListeners = new ArrayList<>();

  // False while the window sets its own controls, so restoring a saved value does not look like a
  // change of the player and is not saved again
  private boolean notifying = true;

  // True while at least one setting is different from the defaults. It is recomputed from the
  // settings object (see updateResetVisibility) instead of being set by hand, so it can not get
  // out of step with what the window shows.
  private boolean hasChanged = false;

  /** Creates the settings window for the given settings object. */
  public SettingsPanel(Settings settings) {
    this.settings = settings == null ? new Settings() : settings;
    this.customAccent = new Color(this.settings.getCustomAccent());
    this.caretColour = new Color(this.settings.getCaretColour());
    this.correctColour = new Color(this.settings.getCorrectColour());
    this.errorColour = new Color(this.settings.getErrorColour());
    this.textColour = new Color(this.settings.getTextColour());

    setUndecorated(true);
    // The window chrome has to be installed before the window is packed, so the frame is included
    // in its size.
    WindowStyler.apply(this);

    buildWindow();
    restoreFromSettings();
    // The saved settings may already differ from the defaults, in which case the reset button is
    // offered straight away
    updateResetVisibility();
    pack();
    setLocationRelativeTo(null);
  }

  // ------------------------------------------------------------------------------- Layout

  /**
   * Builds the window: the window buttons in the top right corner, the title and the four
   * settings in their sections, and the reset button at the bottom.
   */
  private void buildWindow() {
    JPanel content = new JPanel(new BorderLayout());
    content.setOpaque(false);
    content.setPreferredSize(WINDOW_SIZE);
    content.add(WindowControls.titleBar(this), BorderLayout.NORTH);
    content.add(buildSettings(), BorderLayout.CENTER);

    setContentPane(content);
  }

  /** The settings themselves, below the title bar. */
  private JPanel buildSettings() {
    JPanel panel = new JPanel(new BorderLayout(0, 12));
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(4, 18, 16, 18));

    // The title and the sections are kept together at the top, so the sections keep their own height
    // and the empty room of the window stays below them.
    JPanel header = new JPanel(new BorderLayout(0, 14));
    header.setOpaque(false);

    heading = new JLabel("SETTINGS");
    header.add(heading, BorderLayout.NORTH);

    JPanel cardPanel = new JPanel();
    cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
    cardPanel.setOpaque(false);
    cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    cardPanel.add(buildAppearanceCard());
    cardPanel.add(Box.createVerticalStrut(12));
    cardPanel.add(buildBackgroundCard());
    cardPanel.add(Box.createVerticalStrut(12));
    cardPanel.add(buildTypingCard());
    cardPanel.add(Box.createVerticalStrut(12));
    cardPanel.add(buildContentCard());
    header.add(cardPanel, BorderLayout.CENTER);

    panel.add(header, BorderLayout.NORTH);
    panel.add(buildFooter(), BorderLayout.SOUTH);
    return panel;
  }

  /** The card with the colour scheme setting. */
  private JPanel buildAppearanceCard() {
    JPanel card = settingCard("COLOUR SCHEME");

    schemeBox = new JComboBox<>(ColourScheme.displayNames());
    ComboBoxStyler.apply(schemeBox);
    // The drop down shows a swatch in front of every name, so the schemes can be told apart
    schemeBox.setRenderer(new SchemeRenderer());
    schemeBox.setPreferredSize(new Dimension(170, 34));
    schemeBox.setToolTipText("Colour scheme of the race screen");
    schemeBox.addActionListener(event -> fireSettingsChanged());

    // The button is always enabled: pressing it switches the drop down to Custom as well, so the
    // player does not have to choose that entry first.
    pickColourButton = new JButton("Pick own...");
    pickColourButton.setToolTipText("Pick your own accent colour");
    pickColourButton.addActionListener(event -> pickOwnColour());

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    controls.setOpaque(false);
    controls.add(schemeBox);
    controls.add(pickColourButton);

    card.add(
        settingRow("Accent colour", "The swatch shows the colour of the scheme.", controls));
    return card;
  }

  /**
   * The card with the background of the race screen. The button shows the background that is in use
   * and opens a window with the images that come with the application and a grid of colours, the
   * same way every colour setting opens a window; a picked colour fills the screen instead of an
   * image.
   */
  private JPanel buildBackgroundCard() {
    JPanel card = settingCard("BACKGROUND");

    backgroundButton = new JButton();
    ButtonStyler.applySecondary(backgroundButton);
    // Wide and tall enough for the name of a background in front of its preview
    backgroundButton.setPreferredSize(new Dimension(210, 38));
    backgroundButton.setToolTipText("Background of the race screen");
    backgroundButton.getAccessibleContext().setAccessibleName("Change background");
    backgroundButton.addActionListener(event -> pickBackground());

    card.add(
        settingRow(
            "Change background",
            "Open the window to pick an image, or choose any colour for a plain background.",
            backgroundButton));
    return card;
  }

  /**
   * The card with the colours that are used while the player types. They are settings of their
   * own, so they stay the same when another colour scheme is picked.
   *
   * <p>Every row is one button that shows its colour as a swatch and as a hex value; pressing it
   * opens the colour chooser and the new colour is written into the settings object immediately.
   */
  private JPanel buildTypingCard() {
    JPanel card = settingCard("TYPING COLOURS");

    JLabel description = new JLabel("How your typing is shown during a race.");
    description.setAlignmentX(Component.LEFT_ALIGNMENT);
    hints.add(description);
    card.add(description);
    card.add(Box.createVerticalStrut(10));

    // The size of the snippet text, dragged with a slider instead of typed into a field
    sizeSlider =
        new JSlider(
            Settings.MIN_SNIPPET_FONT_SIZE,
            Settings.MAX_SNIPPET_FONT_SIZE,
            settings.getSnippetFontSize());
    SliderStyler.apply(sizeSlider);
    sizeSlider.setToolTipText("Size of the code you type");
    sizeSlider.getAccessibleContext().setAccessibleName("Text size");
    sizeSlider.addChangeListener(event -> sizeChanged());

    sizeValue = new JLabel(sizeSlider.getValue() + " pt");
    JPanel sizeControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    sizeControls.setOpaque(false);
    sizeControls.add(sizeSlider);
    sizeControls.add(sizeValue);

    card.add(
        settingRow(
            "Text size",
            "Drag the handle, or use the arrow keys, to make the code bigger or smaller.",
            sizeControls));
    card.add(Box.createVerticalStrut(10));

    caretButton = colourButton(caretColour, "The blinking line that shows where you type");
    caretButton.addActionListener(event -> pickCaretColour());
    card.add(colourRow("Caret", caretButton));
    card.add(Box.createVerticalStrut(8));

    correctButton = colourButton(correctColour, "The characters you typed correctly");
    correctButton.addActionListener(event -> pickCorrectColour());
    card.add(colourRow("Correct text", correctButton));
    card.add(Box.createVerticalStrut(8));

    errorButton = colourButton(errorColour, "The character you typed wrongly");
    errorButton.addActionListener(event -> pickErrorColour());
    card.add(colourRow("Wrong text", errorButton));
    card.add(Box.createVerticalStrut(8));

    textButton = colourButton(textColour, "The text that is still waiting to be typed");
    textButton.addActionListener(event -> pickTextColour());
    card.add(colourRow("Untyped text", textButton));
    return card;
  }

  /** The card with the content type and the source of the plain text. */
  private JPanel buildContentCard() {
    JPanel card = settingCard("CONTENT");

    codeModeSwitch = new JCheckBox();
    codeModeSwitch.setSelected(true);
    ToggleSwitchStyler.apply(codeModeSwitch);
    codeModeSwitch.setToolTipText("Type code snippets instead of plain text");
    codeModeSwitch.getAccessibleContext().setAccessibleName("Code snippets");
    codeModeSwitch.addActionListener(event -> codeModeChanged());

    card.add(
        settingRow("Code snippets", "", codeModeSwitch));
    card.add(Box.createVerticalStrut(10));

    textSourceBox =
        new JComboBox<>(
            new String[] {Settings.TEXT_SOURCE_WORDS, Settings.TEXT_SOURCE_PASSAGE});
    ComboBoxStyler.apply(textSourceBox);
    textSourceBox.setPrototypeDisplayValue(Settings.TEXT_SOURCE_PASSAGE);
    textSourceBox.setPreferredSize(new Dimension(200, 34));
    textSourceBox.setToolTipText("Kind of plain text that is used");
    textSourceBox.addActionListener(event -> fireSettingsChanged());

    textSourceRow =
        settingRow("Text source", "Used while code snippets are switched off.", textSourceBox);
    // Code snippets are the default, so the text source is not shown until the switch is off
    textSourceRow.setVisible(false);
    card.add(textSourceRow);
    return card;
  }

  /** The reset button at the bottom of the window. */
  private JPanel buildFooter() {
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    footer.setOpaque(false);

    resetButton = new JButton("Reset to defaults");
    resetButton.setToolTipText("Back to Ember, code snippets and random words");
    resetButton.addActionListener(event -> resetToDefaults());
    // The button is only shown while something differs from the defaults, see updateResetVisibility
    resetButton.setVisible(false);
    footer.add(resetButton);
    return footer;
  }

  /**
   * A section of the settings screen: the title of the group, a thin rule and room for the rows.
   * The sections are not drawn as boxes, a rule under the title is the only separation there is.
   */
  private JPanel settingCard(String title) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setOpaque(false);
    card.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    sectionTitles.add(titleLabel);

    // A one pixel line in the border colour, stretched over the full width of the section
    JPanel rule = new JPanel();
    rule.setOpaque(true);
    rule.setBackground(Theme.SUBTLE);
    rule.setAlignmentX(Component.LEFT_ALIGNMENT);
    rule.setMinimumSize(new Dimension(10, 1));
    rule.setPreferredSize(new Dimension(10, 1));
    rule.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    rules.add(rule);

    card.add(titleLabel);
    card.add(Box.createVerticalStrut(4));
    card.add(rule);
    card.add(Box.createVerticalStrut(10));
    return card;
  }

  /**
   * One setting: the caption with the control on the same line, and the hint below it over the
   * full width of the card, so a longer hint is never cut off. Adding a setting later is one call
   * to this method, which keeps the rows of the window the same.
   */
  private JPanel settingRow(String caption, String hint, JComponent control) {
    JPanel row = new JPanel(new BorderLayout(12, 2));
    row.setOpaque(false);
    row.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel name = new JLabel(caption);
    captions.add(name);

    // The control is kept at its own size and aligned with the right edge of the card
    JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    holder.setOpaque(false);
    holder.add(control);

    JPanel line = new JPanel(new BorderLayout(12, 0));
    line.setOpaque(false);
    line.add(name, BorderLayout.WEST);
    line.add(holder, BorderLayout.EAST);

    JLabel description = new JLabel(hint);
    hints.add(description);

    row.add(line, BorderLayout.NORTH);
    row.add(description, BorderLayout.CENTER);
    return row;
  }

  /** One row of the typing card: the caption on the left and the colour button on the right. */
  private JPanel colourRow(String caption, JButton button) {
    JPanel row = new JPanel(new BorderLayout(12, 0));
    row.setOpaque(false);
    row.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel name = new JLabel(caption);
    captions.add(name);

    JPanel holder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    holder.setOpaque(false);
    holder.add(button);

    row.add(name, BorderLayout.WEST);
    row.add(holder, BorderLayout.EAST);
    return row;
  }

  /** A quiet button that shows a colour as a swatch and as a hex value. */
  private JButton colourButton(Color colour, String tooltip) {
    JButton button = new JButton();
    button.setToolTipText(tooltip);
    // Wide and tall enough for the padding of the secondary button style and the swatch
    button.setPreferredSize(new Dimension(150, 38));
    paintColourButton(button, colour);
    return button;
  }

  /** Draws a colour on its button: the swatch in front of the hex value of the colour. */
  private void paintColourButton(JButton button, Color colour) {
    button.setText(String.format("#%06X", colour.getRGB() & 0xffffff));
    button.setIcon(new SwatchIcon(colour, 14));
    button.setIconTextGap(9);
    // Styling again after the icon changed keeps the padding and the hover behaviour
    ButtonStyler.applySecondary(button);
    button.repaint();
  }

  // ---------------------------------------------------------------------------- Behaviour

  /** Fills the controls with the values of the settings object, without notifying anyone. */
  private void restoreFromSettings() {
    notifying = false;
    try {
      customAccent = new Color(settings.getCustomAccent());
      // An old or hand-edited file can contain a name that no longer exists; the byName lookup
      // falls back to Ember and the drop down shows that instead of a blank entry
      schemeBox.setSelectedItem(ColourScheme.byName(settings.getScheme()).displayName());
      codeModeSwitch.setSelected(settings.isCodeMode());
      textSourceBox.setSelectedItem(settings.getTextSource());
      textSourceRow.setVisible(!settings.isCodeMode());
      backgroundName =
          BackgroundPanel.hasBackground(settings.getBackground())
              ? settings.getBackground()
              : Settings.DEFAULT_BACKGROUND;
      backgroundColour = new Color(settings.getBackgroundColour());
      sizeSlider.setValue(settings.getSnippetFontSize());

      caretColour = new Color(settings.getCaretColour());
      correctColour = new Color(settings.getCorrectColour());
      errorColour = new Color(settings.getErrorColour());
      textColour = new Color(settings.getTextColour());
      paintColourButton(caretButton, caretColour);
      paintColourButton(correctButton, correctColour);
      paintColourButton(errorButton, errorColour);
      paintColourButton(textButton, textColour);
    } finally {
      notifying = true;
    }
    refreshTheme();
  }

  /** Shows or hides the text source row when the player switches between code and plain text. */
  private void codeModeChanged() {
    textSourceRow.setVisible(!codeModeSwitch.isSelected());
    revalidate();
    repaint();
    fireSettingsChanged();
  }

  /** Asks the player for an accent colour and makes it the colour of the custom scheme. */
  private void pickOwnColour() {
      
    Color picked = ColourPickerDialog.showDialog(this, "Accent colour", customAccent);
    if (picked == null) {
      // The player closed the chooser, so the old colour stays
      return;
    }
    customAccent = picked;
    if (ColourScheme.CUSTOM.displayName().equals(getSelectedScheme())) {
      // The selection did not change, so the action listener of the drop down does not fire
      fireSettingsChanged();
    } else {
      // This selects the Custom entry and fires the action listener of the drop down
      schemeBox.setSelectedItem(ColourScheme.CUSTOM.displayName());
    }
    // The swatch of the Custom entry is painted from customAccent, so the drop down is repainted
    schemeBox.repaint();
  }

  /** Asks for the caret colour and stores it as a setting. */
  private void pickCaretColour() {
    
    Color picked = ColourPickerDialog.showDialog(this, "Caret colour", caretColour);
    if (picked == null) {
      // The chooser was closed without picking, so the old colour stays
      return;
    }
    caretColour = picked;
    paintColourButton(caretButton, caretColour);
    fireSettingsChanged();
  }

  /** Asks for the colour of correctly typed text and stores it as a setting. */
  private void pickCorrectColour() {
      
    Color picked = ColourPickerDialog.showDialog(this, "Correct text", correctColour);
    if (picked == null) {
      return;
    }
    correctColour = picked;
    paintColourButton(correctButton, correctColour);
    fireSettingsChanged();
  }

  /** Asks for the colour of a mistake and stores it as a setting. */
  private void pickErrorColour() {
     
    Color picked = ColourPickerDialog.showDialog(this, "Wrong text", errorColour);
    if (picked == null) {
      return;
    }
    errorColour = picked;
    paintColourButton(errorButton, errorColour);
    fireSettingsChanged();
  }

  /**
   * Keeps the size label in step while the handle of the slider is dragged. The settings are only
   * saved and applied when the drag ends: every change re-themes the race screen and writes the
   * JSON file, so doing that for every intermediate value would make dragging feel heavy.
   */
  private void sizeChanged() {
   
    sizeValue.setText(sizeSlider.getValue() + " pt");
    if (!sizeSlider.getValueIsAdjusting()) {
      fireSettingsChanged();
    }
  }

  /** Asks for the colour of the text that has not been typed yet and stores it as a setting. */
  private void pickTextColour() {
    Color picked = ColourPickerDialog.showDialog(this, "Untyped text", textColour);
    if (picked == null) {
      return;
    }
    textColour = picked;
    paintColourButton(textButton, textColour);
    fireSettingsChanged();
  }

  /** Opens the background window: the prebuilt images and a colour grid in one place. */
  private void pickBackground() {
    BackgroundPickerDialog.Choice choice =
        BackgroundPickerDialog.showDialog(
            this, getSelectedBackground(), getSelectedBackgroundColour());
    if (choice == null) {
      // The window was closed without picking, so the background stays
      return;
    }
    backgroundName = choice.background;
    if (choice.colour != null) {
      backgroundColour = choice.colour;
    }
    paintBackgroundButton();
    fireSettingsChanged();
  }

  /**
   * Draws the background on its button: a preview of the image with its name, or the swatch and hex
   * value of a plain colour.
   */
  private void paintBackgroundButton() {
    if (backgroundButton == null) {
      return;
    }
    String name = getSelectedBackground();
    if (Settings.BACKGROUND_CUSTOM_COLOUR.equals(name)) {
      Color colour = getSelectedBackgroundColour();
      backgroundButton.setText(String.format("#%06X", colour.getRGB() & 0xffffff));
      backgroundButton.setIcon(new SwatchIcon(colour, 14));
    } else {
      backgroundButton.setText(name);
      BufferedImage preview = BackgroundPanel.thumbnail(name, 34, 20);
      backgroundButton.setIcon(preview == null ? null : new ImageIcon(preview));
    }
    backgroundButton.setIconTextGap(9);
    // Styling again after the icon changed keeps the padding and the hover behaviour
    ButtonStyler.applySecondary(backgroundButton);
    backgroundButton.repaint();
  }

  /** Puts every control back to the values the window starts with. */
  private void resetToDefaults() {
    notifying = false;
    try {
      customAccent = new Color(Settings.DEFAULT_CUSTOM_ACCENT_RGB);
      schemeBox.setSelectedItem(Settings.DEFAULT_SCHEME);
      codeModeSwitch.setSelected(true);
      textSourceBox.setSelectedItem(Settings.TEXT_SOURCE_WORDS);
      textSourceRow.setVisible(false);
      backgroundName = Settings.DEFAULT_BACKGROUND;
      backgroundColour = new Color(Settings.DEFAULT_BACKGROUND_COLOUR_RGB);
      paintBackgroundButton();
      sizeSlider.setValue(Settings.DEFAULT_SNIPPET_FONT_SIZE);

      caretColour = new Color(Settings.DEFAULT_CARET_COLOUR_RGB);
      correctColour = new Color(Settings.DEFAULT_CORRECT_COLOUR_RGB);
      errorColour = new Color(Settings.DEFAULT_ERROR_COLOUR_RGB);
      textColour = new Color(Settings.DEFAULT_TEXT_COLOUR_RGB);
      paintColourButton(caretButton, caretColour);
      paintColourButton(correctButton, correctColour);
      paintColourButton(errorButton, errorColour);
      paintColourButton(textButton, textColour);
    } finally {
      notifying = true;
    }
    revalidate();
    repaint();
    fireSettingsChanged();
  }

  /**
   * Copies the values of the controls into the settings object, saves them, and tells the
   * listeners that something changed. This is the one place where a change of the player turns
   * into a change of the settings, so it is also the one place that has to be kept in sync when a
   * setting is added.
   */
  private void fireSettingsChanged() {
    // While the window fills its own controls there is no change of the player to save
    if (!notifying) {
      return;
    }
    settings.setScheme(getSelectedScheme());
    settings.setCustomAccent(customAccent.getRGB() & 0xffffff);
    settings.setCodeMode(codeModeSwitch.isSelected());
    settings.setTextSource(getTextSource());
    settings.setBackground(getSelectedBackground());
    settings.setBackgroundColour(getSelectedBackgroundColour().getRGB() & 0xffffff);
    settings.setCaretColour(caretColour.getRGB() & 0xffffff);
    settings.setCorrectColour(correctColour.getRGB() & 0xffffff);
    settings.setErrorColour(errorColour.getRGB() & 0xffffff);
    settings.setTextColour(textColour.getRGB() & 0xffffff);
    settings.setSnippetFontSize(sizeSlider.getValue());
    SettingsStore.save(settings);
    updateResetVisibility();

    for (Runnable listener : List.copyOf(changeListeners)) {
      listener.run();
    }
  }

  /**
   * Shows the reset button only while at least one setting differs from the value the application
   * starts with. The flag is recomputed from the settings object every time, so resetting the
   * window to the defaults hides the button again and changing one control shows it.
   */
  private void updateResetVisibility() {
    Settings defaults = new Settings();
    hasChanged =
        !Objects.equals(settings.getScheme(), defaults.getScheme())
            || settings.getCustomAccent() != defaults.getCustomAccent()
            || settings.isCodeMode() != defaults.isCodeMode()
            || !Objects.equals(settings.getTextSource(), defaults.getTextSource())
            || !Objects.equals(settings.getBackground(), defaults.getBackground())
            || settings.getBackgroundColour() != defaults.getBackgroundColour()
            || settings.getCaretColour() != defaults.getCaretColour()
            || settings.getCorrectColour() != defaults.getCorrectColour()
            || settings.getErrorColour() != defaults.getErrorColour()
            || settings.getTextColour() != defaults.getTextColour()
            || settings.getSnippetFontSize() != defaults.getSnippetFontSize();

    if (resetButton == null || resetButton.isVisible() == hasChanged) {
      return;
    }
    resetButton.setVisible(hasChanged);
    // Lay the footer out again, so a hidden button does not leave a gap where it used to be
    if (resetButton.getParent() != null) {
      resetButton.getParent().revalidate();
      resetButton.getParent().repaint();
    }
  }

  /** Applies the current palette to the window and to all of its components again. */
  public void refreshTheme() {
    // The title of the window is the monospaced accent type of the other windows, at the size of the
    // statistics of the race screen
    LabelStyler.applyTitle(heading, 16);
    // The sections use the same monospaced type one size down, so they read as quiet headers above
    // their rows instead of as filled titles
    for (JLabel label : sectionTitles) {
      LabelStyler.applyTitle(label, 12);
    }
    // The captions are the interface font of the race screen, quiet and one size below the labels
    for (JLabel label : captions) {
      LabelStyler.applyMuted(label);
    }
    for (JLabel label : hints) {
      LabelStyler.applyCaption(label);
    }
    for (JPanel rule : rules) {
      rule.setBackground(Theme.SUBTLE);
    }

    ButtonStyler.applySecondary(pickColourButton);
    ButtonStyler.applySecondary(caretButton);
    ButtonStyler.applySecondary(correctButton);
    ButtonStyler.applySecondary(errorButton);
    ButtonStyler.applySecondary(textButton);
    ButtonStyler.applySecondary(resetButton);
    // The slider and its value are painted by their own UI, so they are styled again as well
    SliderStyler.apply(sizeSlider);
    LabelStyler.applyCaption(sizeValue);

    // The combo box styler installs its own renderer, so the swatches are put back after it
    ComboBoxStyler.apply(schemeBox);
    schemeBox.setRenderer(new SchemeRenderer());
    ComboBoxStyler.apply(textSourceBox);
    paintBackgroundButton();
    ToggleSwitchStyler.apply(codeModeSwitch);

    WindowStyler.refresh(this);
    repaint();
  }

  // ------------------------------------------------------------------------- Public values

  /**
   * Registers a listener that runs whenever a setting changes, so the rest of the application can
   * apply the settings without polling this window.
   */
  public void addSettingsChangeListener(Runnable listener) {
    if (listener != null) {
      changeListeners.add(listener);
    }
  }

  /** The scheme the player chose, one of {@link ColourScheme#displayNames()}. */
  public String getSelectedScheme() {
    Object selected = schemeBox.getSelectedItem();
    return selected == null
        ? Settings.DEFAULT_SCHEME
        : ColourScheme.byName(selected.toString()).displayName();
  }

  /** The accent colour of the custom scheme, which is also the last one the player picked. */
  public Color getCustomAccent() {
    return customAccent;
  }

  /** Tells whether the player types code snippets, instead of plain text. */
  public boolean isCodeMode() {
    return codeModeSwitch.isSelected();
  }

  /**
   * The kind of plain text that is used: {@link Settings#TEXT_SOURCE_WORDS} or
   * {@link Settings#TEXT_SOURCE_PASSAGE}.
   */
  public String getTextSource() {
    Object selected = textSourceBox.getSelectedItem();
    return selected == null ? Settings.TEXT_SOURCE_WORDS : selected.toString();
  }

  /** The background the player chose: a name from {@link BackgroundPanel}, or the colour entry. */
  public String getSelectedBackground() {
    return BackgroundPanel.hasBackground(backgroundName)
        ? backgroundName
        : Settings.DEFAULT_BACKGROUND;
  }

  /** The colour that fills the race screen when the player picked a colour, not an image. */
  public Color getSelectedBackgroundColour() {
    return backgroundColour == null
        ? new Color(Settings.DEFAULT_BACKGROUND_COLOUR_RGB)
        : backgroundColour;
  }

  // ------------------------------------------------------------------------------- Painting

  /**
   * The gear that is drawn on the button that opens this window. It is painted instead of being
   * written as a character, because not every font of the theme has a gear glyph.
   */
  public static Icon gearIcon() {
    return new GearIcon();
  }

  /**
   * Draws the entries of the scheme drop down with a swatch in front of the name, in the same
   * colours as the entries of the other combo boxes.
   */
  private final class SchemeRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label =
          (JLabel)
              super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      label.setFont(Theme.mono(14));
      label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
      label.setIcon(new SwatchIcon(swatchOf(String.valueOf(value))));
      label.setIconTextGap(10);

      if (index < 0) {
        // The value that is shown inside the combo box itself; the rounded background is behind it
        label.setOpaque(false);
        label.setForeground(Theme.ACCENT);
      } else if (isSelected) {
        label.setOpaque(true);
        label.setBackground(Theme.BACKGROUND);
        label.setForeground(Theme.ACCENT);
      } else {
        label.setOpaque(true);
        label.setBackground(Theme.BACKGROUND_ALT);
        label.setForeground(Theme.TEXT);
      }
      return label;
    }
  }

  /** The preview colour of a scheme; the custom scheme shows the colour the player picked. */
  private Color swatchOf(String scheme) {
    return Theme.accentOf(ColourScheme.byName(scheme), customAccent);
  }

  /** A small rounded square filled with a colour, used as the preview of a colour setting. */
  private static final class SwatchIcon implements Icon {

    /** The size of the swatches in the scheme drop down. */
    private static final int DEFAULT_SIZE = 12;

    private final Color color;
    private final int size;

    private SwatchIcon(Color color) {
      this(color, DEFAULT_SIZE);
    }

    private SwatchIcon(Color color, int size) {
      this.color = color;
      this.size = size;
    }

    @Override
    public int getIconWidth() {
      return size;
    }

    @Override
    public int getIconHeight() {
      return size;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      // The swatch is one pixel lower than the icon box, so it lines up with the text of the row
      graphics2D.setColor(color);
      graphics2D.fillRoundRect(x, y + 1, size, size - 1, 5, 5);
      graphics2D.setColor(Theme.withAlpha(Theme.BACKGROUND, 140));
      graphics2D.setStroke(new BasicStroke(1.0f));
      graphics2D.drawRoundRect(x, y + 1, size - 1, size - 2, 5, 5);
      graphics2D.dispose();
    }
  }

  /** A small gear: an outer circle with eight teeth and a hub in the middle. */
  private static final class GearIcon implements Icon {

    private static final int SIZE = 14;
    // The radii are chosen so that the teeth still fit inside the 14 by 14 pixel icon
    private static final double OUTER_RADIUS = 4.0;
    private static final double TOOTH_RADIUS = 6.0;
    private static final double INNER_RADIUS = 1.6;

    @Override
    public int getIconWidth() {
      return SIZE;
    }

    @Override
    public int getIconHeight() {
      return SIZE;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
      Graphics2D graphics2D = (Graphics2D) graphics.create();
      Theme.enableQuality(graphics2D);
      graphics2D.translate(x + SIZE / 2.0, y + SIZE / 2.0);
      // The icon follows the colour of the button, so it stays readable on hover as well
      Color color = component == null ? Theme.TEXT_MUTED : component.getForeground();
      graphics2D.setColor(color);
      graphics2D.setStroke(new BasicStroke(1.5f));
      graphics2D.draw(
          new Ellipse2D.Double(-OUTER_RADIUS, -OUTER_RADIUS, OUTER_RADIUS * 2, OUTER_RADIUS * 2));
      // Eight teeth, each one rotated an eighth of a full turn further than the last
      for (int tooth = 0; tooth < 8; tooth++) {
        graphics2D.rotate(Math.PI / 4);
        graphics2D.draw(new Line2D.Double(0, -OUTER_RADIUS, 0, -TOOTH_RADIUS));
      }
      graphics2D.draw(
          new Ellipse2D.Double(-INNER_RADIUS, -INNER_RADIUS, INNER_RADIUS * 2, INNER_RADIUS * 2));
      graphics2D.dispose();
    }
  }
}
