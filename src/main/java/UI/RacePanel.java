package UI;

import database.Database;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import logic.RaceEngine;
import logic.RaceEngine.RaceState;
import model.Snippet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 * @author maril
 */
// TODO CHANGE THE NAMES OF ALL BUTTONS AND CONTAINERS ETC ETC
public class RacePanel extends BackgroundPanel {

  public String language = "Random";
  // The font that is used for the snippet and for the typing field
  private static final String CODE_FONT = "Consolas";
  // The biggest font size the snippet is allowed to have
  private static final int MAX_SNIPPET_FONT = 28;
  // The smallest font size the snippet is allowed to have, below this the text is unreadable
  private static final int MIN_SNIPPET_FONT = 8;
  // The space that is kept below the end race button
  private static final int BOTTOM_MARGIN = 15;

  // Enumerator to handle the 3 different states that the player can be in

  public enum PanelState {
    NOT_STARTED,
    RUNNING,
    FINISHED;
  }

  // Sets the current panel state at the very start of the application being launched to not_started
  private PanelState currentPanel = PanelState.NOT_STARTED;
  // Initializes the game over screen
  private GameOver overWindow;
  // Initializes the two methods responsible for the refreshing of the values when we dynamically
  // display the statistics
  private javax.swing.Timer uiTimer;
  private javax.swing.Timer uiTimer2;
  // Initialize classes
  private Database db;
  public RaceEngine currentRace;
  // The size of the layout as it is defined in the designer, the layout is scaled with this size
  private Dimension baseSize;

  // Constructor
  public RacePanel() {
    // Built in java code
    initComponents();
    // display of Label for Selection of language
    jLabel2.setForeground(Color.WHITE);
    // display of combo box
    jComboBox1.setRenderer(new DarkComboRenderer());
    jComboBox1.setBackground(new Color(10, 5, 15));
    jComboBox1.setForeground(new Color(255, 105, 180));
    jComboBox1.setBorder(BorderFactory.createLineBorder(new Color(255, 20, 147), 1));
    jComboBox1.setFocusable(false);
    // display of end race button
    endRaceButton.setOpaque(false);
    endRaceButton.setBorder(null);
    endRaceButton.setBorderPainted(false);
    endRaceButton.setContentAreaFilled(true);
    endRaceButton.setBackground(Color.BLACK);
    endRaceButton.setForeground(Color.RED);
    // display of stats labels
    wpmLabel.setForeground(Color.WHITE);
    accuracyLabel.setForeground(Color.WHITE);
    mistakesLabel.setForeground(Color.WHITE);
    jLabel1.setForeground(Color.WHITE);
    // dispay of snippetPane
    snippetPane.setOpaque(false);
    snippetPane.setBorder(null);
    snippetPan.setOpaque(false);
    snippetPan.getViewport().setOpaque(false);
    snippetPan.setBorder(null);
    snippetPane.setForeground(Color.ORANGE);
    // display of typingField
    typingField.setOpaque(false);
    typingField.setBorder(null);
    typingPane.setOpaque(false);
    typingPane.getViewport().setOpaque(false);
    typingPane.setBorder(null);
    typingField.setCaretColor(Color.PINK);

    // display of startbutton
    newRaceButton.setFocusPainted(false);
    newRaceButton.setOpaque(false);
    newRaceButton.setBorderPainted(false);
    newRaceButton.setContentAreaFilled(true);
    newRaceButton.setBackground(Color.BLACK);
    newRaceButton.setForeground(Color.getHSBColor(0.90f, 1.0f, 1.0f));
    // DocumentListener shit !Very important code

    typingField.setTransferHandler(null);
    typingField.getDocument().addDocumentListener(new MyDocumentListener());
    // The user can not use the mouse to move the caret or select text in the typing field
    disableTypingFieldMouseInput();
    // Enter jumps to the first word of the next line instead of leaving the caret on its
    // indentation
    installEnterKeyBinding();
    // Backspace can only remove a mistake at the end of the typed text
    installBackspaceKeyBinding();

    // The snippet is always scaled to fit its pane, so the font has to be recalculated when the
    // window is resized
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            SwingUtilities.invokeLater(RacePanel.this::fitSnippetFont);
          }
        });

    // Calls racemode which is responsible for whats visible on the ui
    setRaceMode();
    // The size of the layout of the designer, it is used to scale the layout when the window is
    // resized.
    // It is taken after the components are styled, because that can change their size.
    baseSize = getPreferredSize();
  }

  // The dictator of the ui
  private void setRaceMode() {
    // Kind of obvious code, makes buttons and fields visible or invisible whether or not the user
    // has pressed the play button.
    snippetPane.setVisible(currentPanel == PanelState.RUNNING);
    typingField.setVisible(currentPanel == PanelState.RUNNING);
    snippetPan.setVisible(currentPanel == PanelState.RUNNING);
    typingPane.setVisible(currentPanel == PanelState.RUNNING);
    endRaceButton.setVisible(currentPanel == PanelState.RUNNING);
    wpmLabel.setVisible(currentPanel == PanelState.RUNNING);
    accuracyLabel.setVisible(currentPanel == PanelState.RUNNING);
    mistakesLabel.setVisible(currentPanel == PanelState.RUNNING);
    jLabel1.setVisible(currentPanel == PanelState.RUNNING);
    newRaceButton.setVisible(
        currentPanel == PanelState.FINISHED || currentPanel == PanelState.NOT_STARTED);
    boolean name = currentPanel == PanelState.NOT_STARTED;
    if (name) {
      setImage();
    }
    revalidate();
    // The components that were hidden or shown have to be painted again
    repaint();
    // This is always going to be false no matter if the user has pressed the play button or not,
    // this is only visible when the user has ended a game so before pressing the play button again
    // GameOverField.setVisible(currentRace.currentState == RaceEngine.RaceState.FINNISHED);
  }

  // This function is responsible for showing the statistics on screen, or more precisely this
  // function is responsible for the refreshing of the functions that show the statistics on the
  // screen
  private void startUiTimer() {
    uiTimer = new javax.swing.Timer(100, e -> updateLabels());
    uiTimer2 = new javax.swing.Timer(500, e -> updateWPM());

    uiTimer2.start(); // every second
    uiTimer
        .start(); // every 5 seconds but currently has some bugs or idk it feels like it refreshes
    // faster thane very 5s
  }

  // is ran when the user presses the start button
  private void newGame() {

    language = jComboBox1.getSelectedItem().toString();
    db = new Database(language);
    // Calls the Snippet class and initializes it with a random snippet from the Database class.
    Snippet snip = db.getRandomSnippet();
    // Creates a RaceEngine object by calling the RaceEngine constructor with the snippet from the
    // previous lines.
    currentRace = new RaceEngine(snip.getBody());
    // Sets the text of the snippetPane with that of the current snippet
    snippetPane.setText(snip.getBody());
    // Removes the text of the previous race from the typing field
    typingField.setText("");
    // Scales the font of the snippet so that the whole snippet fits in the snippet pane without
    // scrolling
    SwingUtilities.invokeLater(this::fitSnippetFont);
    // This calls the function that is responsible for showing statistics on the screen
    startUiTimer();

    // Sets the state to be running
    currentPanel = PanelState.RUNNING;
    setRaceMode();

    // Closes the game over window if it exists, this has to happen before the focus is requested
    // because the focus can only be given to a window that is the active one
    if (overWindow != null) {
      overWindow.dispose();
    }

    // this line makes the player able to type immediately without having to click in the typing
    // area
    SwingUtilities.invokeLater(() -> typingField.requestFocusInWindow());
  }

  public String getLanguage() {
    return language;
  }

  private void gameOver() {
    // changes the state of the race and calls the ui dictator
    currentPanel = PanelState.FINISHED;
    // removes the typed text from the field
    typingField.setText("");
    setRaceMode();

    // makes the game over window visible with statistics
    overWindow = new GameOver(currentRace);
    overWindow.setVisible(true);

    uiTimer.stop();
    uiTimer2.stop();
    // GameOverField.setVisible(true)
  }

  /*
   * Replaces the normal behaviour of Backspace. The user is only allowed to delete the last
   * character when it is wrong, so that a mistake can be corrected. Once the wrong character is
   * gone, all remaining text is correct and Backspace does nothing anymore. This prevents the user
   * from deleting text that was typed correctly. Every modifier combination is bound, otherwise
   * Shift+Backspace, Ctrl+Backspace, etc. would still use the default editor action.
   */
  private void installBackspaceKeyBinding() {
    InputMap inputMap = typingField.getInputMap(JComponent.WHEN_FOCUSED);
    typingField
        .getActionMap()
        .put(
            "restrictedBackspace",
            new AbstractAction() {
              @Override
              public void actionPerformed(ActionEvent evt) {
                // Backspace is only allowed while the race is running and only for a wrong last
                // character
                if (currentPanel != PanelState.RUNNING
                    || currentRace == null
                    || !currentRace.canBackspace()) {
                  return;
                }
                // Only delete when the caret is at the end, so no earlier (correct) text can be
                // removed
                if (typingField.getSelectionStart() != typingField.getSelectionEnd()
                    || typingField.getCaretPosition() != typingField.getDocument().getLength()) {
                  return;
                }
                int lastIndex = typingField.getDocument().getLength() - 1;
                if (lastIndex < 0) {
                  return;
                }
                try {
                  typingField.getDocument().remove(lastIndex, 1);
                } catch (BadLocationException ex) {
                  Logger.getLogger(RacePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
            });

    int[] modifierMasks = {
      InputEvent.SHIFT_DOWN_MASK,
      InputEvent.CTRL_DOWN_MASK,
      InputEvent.ALT_DOWN_MASK,
      InputEvent.META_DOWN_MASK,
      InputEvent.ALT_GRAPH_DOWN_MASK
    };
    // Bind Backspace with every possible combination of the modifier keys above. This shadows all
    // of the default Backspace bindings of the text pane, including Shift+Backspace.
    for (int combination = 0; combination < (1 << modifierMasks.length); combination++) {
      int modifiers = 0;
      for (int i = 0; i < modifierMasks.length; i++) {
        if ((combination & (1 << i)) != 0) {
          modifiers |= modifierMasks[i];
        }
      }
      inputMap.put(
          KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, modifiers), "restrictedBackspace");
    }
  }

  /*
   * Prevents the user from clicking inside the text area. The mouse listeners of the text panes are
   * responsible for moving the caret and selecting text when the user clicks or drags, so they are
   * removed from both the typing field and the snippet pane. The snippet pane is also made
   * non-focusable, so a click there can not take the focus away from the typing field. A mouse
   * listener is added back that always puts the caret at the end, in case the look and feel
   * installs its mouse listeners again later.
   */
  private void disableTypingFieldMouseInput() {
    removeMouseInput(typingField);
    removeMouseInput(snippetPane);
    removeMouseInput(typingPane);
    removeMouseInput(typingPane.getViewport());
    removeMouseInput(snippetPan);
    removeMouseInput(snippetPan.getViewport());

    snippetPane.setFocusable(false);
    snippetPan.setFocusable(false);
    snippetPan.getViewport().setFocusable(false);
    typingPane.setFocusable(false);
    typingPane.getViewport().setFocusable(false);

    typingField.setDragEnabled(false);
    typingField.setSelectionColor(new Color(0, 0, 0, 0));
    typingField.setSelectedTextColor(new Color(0, 0, 0, 0));

    MouseAdapter keepCaretAtEnd =
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            resetCaretToEnd();
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            resetCaretToEnd();
          }

          @Override
          public void mouseDragged(MouseEvent e) {
            resetCaretToEnd();
          }
        };
    typingField.addMouseListener(keepCaretAtEnd);
    typingField.addMouseMotionListener(keepCaretAtEnd);
    typingPane.addMouseListener(keepCaretAtEnd);
    typingPane.getViewport().addMouseListener(keepCaretAtEnd);
    snippetPane.addMouseListener(keepCaretAtEnd);
    snippetPan.addMouseListener(keepCaretAtEnd);
    snippetPan.getViewport().addMouseListener(keepCaretAtEnd);
  }

  /*
   * Removes all mouse listeners of a component. This is used to disable the built-in caret movement
   * and text selection of the text panes and their scroll panes.
   */
  private void removeMouseInput(Component component) {
    for (MouseListener listener : component.getMouseListeners()) {
      component.removeMouseListener(listener);
    }
    for (MouseMotionListener listener : component.getMouseMotionListeners()) {
      component.removeMouseMotionListener(listener);
    }
  }

  /*
   * Puts the caret back at the end of the typed text after a mouse event. This runs later, so it
   * also corrects the caret when a look and feel handles the mouse event after our listener.
   */
  private void resetCaretToEnd() {
    SwingUtilities.invokeLater(() -> {
      if (currentPanel == PanelState.RUNNING) {
        typingField.requestFocusInWindow();
        typingField.setCaretPosition(typingField.getDocument().getLength());
      }
    });
  }

  /*
   * Replaces the normal behaviour of the Enter key. Normally Enter only inserts a newline, which
   * would leave the caret at the start of the next line, in front of its indentation. The user
   * would then have to type all the spaces/tabs of that indentation before reaching the first word.
   * This binding inserts the newline and that indentation in one go, so the caret jumps straight to
   * the first character of the next line, like in a code editor.
   */
  private void installEnterKeyBinding() {
    InputMap inputMap = typingField.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterToNextLine");
    typingField
        .getActionMap()
        .put(
            "enterToNextLine",
            new AbstractAction() {
              @Override
              public void actionPerformed(ActionEvent evt) {
                if (!insertEnterToNextLine()) {
                  // Not at the end of a line, so Enter keeps its normal behaviour
                  Action defaultAction =
                      typingField.getActionMap().get(DefaultEditorKit.insertBreakAction);
                  if (defaultAction != null) {
                    defaultAction.actionPerformed(evt);
                  }
                }
              }
            });
  }

  /**
   * Inserts the newline and the indentation of the next snippet line as one block. Returns false
   * when the caret is not at the end of a snippet line, in that case the caller inserts a normal
   * newline.
   */
  private boolean insertEnterToNextLine() {
    if (currentPanel != PanelState.RUNNING || currentRace == null) {
      return false;
    }
    // The game always appends text at the end, so only auto indent when the caret is there too
    if (typingField.getSelectionStart() != typingField.getSelectionEnd()
        || typingField.getCaretPosition() != typingField.getDocument().getLength()) {
      return false;
    }
    String insertion = currentRace.getEnterInsertion();
    if (insertion == null) {
      return false;
    }
    try {
      typingField.getDocument().insertString(typingField.getCaretPosition(), insertion, null);
    } catch (BadLocationException ex) {
      Logger.getLogger(RacePanel.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
    // Places the caret after the indentation, so it is on the first word of the next line
    typingField.setCaretPosition(typingField.getDocument().getLength());
    return true;
  }

  // Simple function that just uses the getter in RaceEnginge
  private void updateWPM() {
    if (currentPanel == PanelState.RUNNING) {
      wpmLabel.setText("WPM" + " " + currentRace.getWPM());
    }
  }

  // Same here
  private void updateLabels() {
    if (currentPanel == PanelState.RUNNING) {
      jLabel1.setText("Time" + " " + currentRace.getTime());
      mistakesLabel.setText("Mistakes" + " " + currentRace.getMistakes());
      accuracyLabel.setText("Accuracy" + " " + currentRace.getAccuracy() + "%");
    }
  }

  // a ListCellRenderer created to fix the combo box displey
  class DarkComboRenderer extends DefaultListCellRenderer {

    private final Color normalBg = new Color(10, 5, 15);
    private final Color selectedBg = new Color(120, 20, 80);
    private final Color textColor = new Color(255, 105, 180);

    @Override
    public java.awt.Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      setOpaque(true);
      setBackground(isSelected ? selectedBg : normalBg);
      setForeground(textColor);
      setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
      return this;
    }
  }

  /*
   * We need to use the class below because a document listener is the only way for the program to
   * understand the changes in the text the user types
   */
  // Heart of the code but this is familiar to you already
  class MyDocumentListener implements DocumentListener {

    private void update(DocumentEvent e) {

      if (currentPanel == PanelState.RUNNING) {

        try {

          String typedsnip = typingField.getText(0, e.getDocument().getLength());
          // calls a method in RaceEngine that returns true or false
          boolean isAccepted = currentRace.updateTypedSnip(typedsnip);

          // research what this does!
          SwingUtilities.invokeLater(() -> {
            if (currentRace.getState() == RaceEngine.RaceState.FINISHED) {
              colorTypingField();
              gameOver();

              // updateText(currentRace.getWPM(), currentRace.getKeystrokes(),
              // currentRace.getMistakes(), currentRace.getAccuracy());
            }
            if (!isAccepted) {

              // Calls a method in RaceEnginge that returns the typedsnip, so the text of the
              // typingfield is now the typedSnip
              typingField.setText(currentRace.getLastAcceptedText());

              // This is Java Swing code that moves the text cursor (caret) to the very end of a
              // text field.
              typingField.setCaretPosition(typingField.getText().length());
            }
            // calls the method that colours the text
            colorTypingField();
          });

        } catch (BadLocationException ex) {
          Logger.getLogger(MyDocumentListener.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      update(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      update(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {}
  }

  @Override
  public void doLayout() {
    super.doLayout(); // let the designer layout do its normal work first
    // The bounds of the snippet and the typing pane come from the designer (.form file), they are
    // only scaled here so that the layout keeps working when the window is resized.
    scaleLayout();
    // The button that is in use is centered in the panel, the other one is hidden
    if (currentPanel != PanelState.RUNNING) {
      int x = (getWidth() - newRaceButton.getWidth()) / 2;
      int y = (getHeight() - newRaceButton.getHeight()) / 2;
      newRaceButton.setLocation(x, y);
    } else {
      int x = (getWidth() - endRaceButton.getWidth()) / 2;
      int y = getHeight() - endRaceButton.getHeight() - BOTTOM_MARGIN;
      endRaceButton.setLocation(x, y);
    }
  }

  /**
   * Scales the layout of the designer (.form file) with the size of the panel, so the whole layout
   * grows and shrinks with the window instead of only the background.
   */
  private void scaleLayout() {
    if (baseSize == null || baseSize.width <= 0 || baseSize.height <= 0) {
      return;
    }
    double scaleX = (double) getWidth() / baseSize.width;
    double scaleY = (double) getHeight() / baseSize.height;
    if (scaleX == 1.0 && scaleY == 1.0) {
      return;
    }
    for (Component component : getComponents()) {
      Rectangle bounds = component.getBounds();
      component.setBounds(
          (int) Math.round(bounds.x * scaleX),
          (int) Math.round(bounds.y * scaleY),
          Math.max(1, (int) Math.round(bounds.width * scaleX)),
          Math.max(1, (int) Math.round(bounds.height * scaleY)));
    }
  }

  /**
   * Scales the font of the snippet pane so that the whole snippet is always visible without
   * scrolling. The font starts at the biggest size and is made smaller until the text fits in the
   * pane. The text is centered in the pane and the typing field uses the same font and the same
   * border, so that the text the user types lines up with the snippet.
   */
  private void fitSnippetFont() {
    String text = snippetPane.getText();
    if (text == null || text.isEmpty()) {
      return;
    }
    // Splits the snippet into its lines
    String[] lines = text.split("\n", -1);
    // The border is used to center the text, it is empty while the font is fitted
    snippetPane.setBorder(BorderFactory.createEmptyBorder());
    typingField.setBorder(BorderFactory.createEmptyBorder());
    // The snippet pane does not scroll, the font is scaled instead
    snippetPan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    snippetPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    // Makes the visible area of the pane match the new scrollbar policy
    snippetPan.doLayout();
    // Tries the biggest font first and makes it smaller until the snippet fits in the pane
    int size = MAX_SNIPPET_FONT;
    boolean fits = false;
    FontMetrics metrics = null;
    int textWidth = 0;
    while (size >= MIN_SNIPPET_FONT) {
      metrics = snippetPane.getFontMetrics(new Font(CODE_FONT, Font.PLAIN, size));
      textWidth = widestLine(metrics, lines);
      if (fitsInPane(metrics, textWidth, lines.length)) {
        fits = true;
        break;
      }
      size--;
    }
    if (!fits) {
      // The snippet is too big even for the smallest font, so the pane is allowed to scroll after
      // all
      size = MIN_SNIPPET_FONT;
      metrics = snippetPane.getFontMetrics(new Font(CODE_FONT, Font.PLAIN, size));
      textWidth = widestLine(metrics, lines);
      snippetPan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      snippetPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    // Applies the font to the snippet, the font is only set when it really changed to avoid a loop
    // of repaints
    Font snippetFont = new Font(CODE_FONT, Font.PLAIN, size);
    if (!snippetFont.equals(snippetPane.getFont())) {
      snippetPane.setFont(snippetFont);
    }
    // The typing field uses the same font size, so the text lines up with the snippet
    Font typingFont = new Font(CODE_FONT, Font.PLAIN, size);
    if (!typingFont.equals(typingField.getFont())) {
      typingField.setFont(typingFont);
    }
    // Centers the text of the snippet and of the typing field in their panes
    centerText(metrics, textWidth, lines.length);
  }

  /** Returns the width of the widest line of the snippet. */
  private int widestLine(FontMetrics metrics, String[] lines) {
    int width = 0;
    for (String line : lines) {
      width = Math.max(width, metrics.stringWidth(line));
    }
    return width;
  }

  /**
   * Centers the text in the snippet pane and in the typing field. This is done with an empty border
   * around the text, the text itself stays left aligned, so that the text the user types lines up
   * with the lines of the snippet.
   */
  private void centerText(FontMetrics metrics, int textWidth, int lineCount) {
    int textHeight = lineCount * metrics.getHeight();
    // Both panes have the same width, so the text starts at the same place in both of them
    Dimension snippetVisible = visibleSize(snippetPan);
    int left = Math.max(0, (snippetVisible.width - textWidth) / 2);
    // Every pane centers the text in its own height
    int snippetTop = Math.max(0, (snippetVisible.height - textHeight) / 2);
    snippetPane.setBorder(BorderFactory.createEmptyBorder(snippetTop, left, 0, 0));
    Dimension typingVisible = visibleSize(typingPane);
    int typingTop = Math.max(0, (typingVisible.height - textHeight) / 2);
    typingField.setBorder(BorderFactory.createEmptyBorder(typingTop, left, 0, 0));
  }

  /** Checks if the snippet fits in the visible area of the snippet pane with the given font. */
  private boolean fitsInPane(FontMetrics metrics, int textWidth, int lineCount) {
    // The size of the text that is visible inside the pane
    Dimension visible = visibleSize(snippetPan);
    // The border of the text pane is the space around the text itself
    Insets insets = snippetPane.getInsets();
    int availableWidth = visible.width - insets.left - insets.right;
    int availableHeight = visible.height - insets.top - insets.bottom;
    if (availableWidth <= 0 || availableHeight <= 0) {
      return false;
    }
    return textWidth <= availableWidth && lineCount * metrics.getHeight() <= availableHeight;
  }

  /** Returns the size of the area that is visible inside a scroll pane. */
  private Dimension visibleSize(JScrollPane scrollPane) {
    Dimension visible = scrollPane.getViewport().getExtentSize();
    if (visible.width <= 0 || visible.height <= 0) {
      // The pane has not been laid out yet, the size of the pane itself is used instead
      Insets insets = scrollPane.getInsets();
      visible =
          new Dimension(
              scrollPane.getWidth() - insets.left - insets.right,
              scrollPane.getHeight() - insets.top - insets.bottom);
    }
    return visible;
  }

  // Method that colours the text
  private void colorTypingField() {
    String acceptedText = currentRace.getLastAcceptedText();
    StyledDocument doc = snippetPane.getStyledDocument();

    // Default organge background
    SimpleAttributeSet orangeText = new SimpleAttributeSet();
    StyleConstants.setForeground(orangeText, Color.ORANGE);
    doc.setCharacterAttributes(0, acceptedText.length(), orangeText, true);

    if (!acceptedText.isEmpty() && currentRace.getState() != RaceState.FINISHED) {
      int thatPosition = acceptedText.length() - 1;

      // if you cant backspace the letter infront of you should always be orange no, not the one
      // thats green but the one infront of the cursor?

      if (!currentRace.canBackspace()) {
        StyleConstants.setForeground(orangeText, Color.ORANGE);
        doc.setCharacterAttributes(1, acceptedText.length(), orangeText, true);
      }

      if (currentRace.comparison(thatPosition)) {
        SimpleAttributeSet greenText = new SimpleAttributeSet();
        StyleConstants.setForeground(greenText, Color.GREEN);
        doc.setCharacterAttributes(thatPosition, 1, greenText, true);

        // if it was wrong but user pressed backspace it shoud be empty
      }
      if (!(currentRace.comparison(thatPosition)) && currentRace.canBackspace()) {
        SimpleAttributeSet redText = new SimpleAttributeSet();
        StyleConstants.setForeground(redText, Color.RED);
        doc.setCharacterAttributes(thatPosition, 1, redText, true);
      }
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    snippetPan = new javax.swing.JScrollPane();
    snippetPane = new javax.swing.JTextPane();
    newRaceButton = new javax.swing.JButton();
    typingPane = new javax.swing.JScrollPane();
    typingField = new javax.swing.JTextPane();
    wpmLabel = new javax.swing.JLabel();
    accuracyLabel = new javax.swing.JLabel();
    mistakesLabel = new javax.swing.JLabel();
    endRaceButton = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    jComboBox1 = new javax.swing.JComboBox<>();
    jLabel2 = new javax.swing.JLabel();

    setForeground(new java.awt.Color(0, 0, 0));
    setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
    setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    snippetPane.setEditable(false);
    snippetPane.setFont(new java.awt.Font("Consolas", 0, 28)); // NOI18N
    snippetPan.setViewportView(snippetPane);

    add(snippetPan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1200, 330));

    newRaceButton.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
    newRaceButton.setText("Start New Race");
    newRaceButton.setAlignmentX(0.5F);
    newRaceButton.addActionListener(this::newRaceButtonActionPerformed);
    add(newRaceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(518, 300, -1, 51));

    typingField.setFont(new java.awt.Font("Consolas", 0, 24)); // NOI18N
    typingPane.setViewportView(typingField);

    add(typingPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1200, 330));

    wpmLabel.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
    wpmLabel.setText("WPM");
    add(wpmLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, -1, -1));

    accuracyLabel.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
    accuracyLabel.setText("Time");
    add(accuracyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, -1, -1));

    mistakesLabel.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
    mistakesLabel.setText("Accuracy");
    add(mistakesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, -1));

    endRaceButton.setFont(new java.awt.Font("Consolas", 1, 18)); // NOI18N
    endRaceButton.setText("End Race");
    endRaceButton.addActionListener(this::endRaceButtonActionPerformed);
    add(endRaceButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 620, -1, -1));

    jLabel1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 18)); // NOI18N
    jLabel1.setText("Time");
    add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 10, -1, -1));

    jComboBox1.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
    jComboBox1.setModel(
        new javax.swing.DefaultComboBoxModel<>(
            new String[] {
              "Random", "Python", "Java", "JavaScript", "C++", "Rust", "Go", "Assembly"
            }));
    jComboBox1.addActionListener(this::jComboBox1ActionPerformed);
    add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 600, 150, 40));

    jLabel2.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
    jLabel2.setText("Select Language");
    add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 600, 190, 50));
  } // </editor-fold>//GEN-END:initComponents

  // Start button is pressed
  private void newRaceButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_newRaceButtonActionPerformed
    newGame();
  } // GEN-LAST:event_newRaceButtonActionPerformed

  // End Game button is pressed
  private void endRaceButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_endRaceButtonActionPerformed
    gameOver();
  } // GEN-LAST:event_endRaceButtonActionPerformed

  private void jComboBox1ActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jComboBox1ActionPerformed
    if (currentPanel == PanelState.RUNNING) {
      newGame();
    }
  } // GEN-LAST:event_jComboBox1ActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel accuracyLabel;
  private javax.swing.JButton endRaceButton;
  private javax.swing.JComboBox<String> jComboBox1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel mistakesLabel;
  private javax.swing.JButton newRaceButton;
  private javax.swing.JScrollPane snippetPan;
  private javax.swing.JTextPane snippetPane;
  private javax.swing.JTextPane typingField;
  private javax.swing.JScrollPane typingPane;
  private javax.swing.JLabel wpmLabel;
  // End of variables declaration//GEN-END:variables
}
