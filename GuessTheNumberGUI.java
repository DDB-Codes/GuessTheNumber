import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GuessTheNumberGUI extends JFrame {
    private final Random random = new Random();
    private int target, attempts, maxRange, closestGuess = -1, closestDiff = Integer.MAX_VALUE;
    private String difficulty = "";
    private JLabel promptLabel, feedbackLabel, scoreLabel;
    private JTextField guessField;
    private JButton submitButton, highScoreButton, playAgainButton, changeDifficultyButton;

    private static final int EASY = 10;
    private static final int NORMAL = 100;
    private static final int HARD = 1000;
    private static final int IMPOSSIBLE = 1_000_000;

    private static final Color DARK_BG = new Color(30, 30, 30);
    private static final Color FIELD_DEFAULT = new Color(50, 50, 50);
    private static final Color GREEN = new Color(0, 200, 0);
    private static final Color RED = Color.RED;

    private static Map<String, Integer> highScores = new HashMap<>();
    private static final String SAVE_FILE = "highscores.txt";

    public GuessTheNumberGUI() {
        setTitle("Guess The Number");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 1));
        setLocationRelativeTo(null);

        getContentPane().setBackground(DARK_BG);
        UIManager.put("OptionPane.background", new Color(45, 45, 45));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Panel.background", DARK_BG);

        Font font = new Font("SansSerif", Font.BOLD, 16);

        promptLabel = new JLabel("", SwingConstants.CENTER);
        promptLabel.setFont(font);
        promptLabel.setForeground(Color.WHITE);

        feedbackLabel = new JLabel("Enter your guess below.", SwingConstants.CENTER);
        feedbackLabel.setFont(font);
        feedbackLabel.setForeground(Color.LIGHT_GRAY);

        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setFont(font);
        scoreLabel.setForeground(Color.LIGHT_GRAY);

        guessField = new JTextField();
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setFont(font);
        guessField.setBackground(FIELD_DEFAULT);
        guessField.setForeground(Color.WHITE);
        guessField.setCaretColor(Color.WHITE);

        submitButton = createButton("Submit", font);
        highScoreButton = createButton("Show High Scores", font);
        playAgainButton = createButton("Play Again", font);
        changeDifficultyButton = createButton("Change Difficulty", font);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkGuess();
            }
        });

        highScoreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHighScores();
            }
        });

        playAgainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame(difficulty);
            }
        });

        changeDifficultyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseNewDifficulty();
            }
        });

        add(promptLabel);
        add(feedbackLabel);
        add(scoreLabel);
        add(guessField);
        add(submitButton);
        add(highScoreButton);
        add(playAgainButton);
        add(changeDifficultyButton);

        playAgainButton.setEnabled(false);
        changeDifficultyButton.setEnabled(false);

        setVisible(true);
        loadHighScores();
        chooseNewDifficulty();
    }

    private JButton createButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        return button;
    }

    private void flashField(Color color) {
        guessField.setBackground(color);
        Timer timer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guessField.setBackground(FIELD_DEFAULT);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void checkGuess() {
        try {
            int guess = Integer.parseInt(guessField.getText());
            attempts++;

            int diff = Math.abs(target - guess);
            if (diff < closestDiff) {
                closestDiff = diff;
                closestGuess = guess;
            }

            if (guess == target) {
                feedbackLabel.setText("Correct! You guessed it in " + attempts + " attempts.");
                feedbackLabel.setForeground(GREEN);
                flashField(GREEN);

                if (attempts < highScores.getOrDefault(difficulty, Integer.MAX_VALUE)) {
                    scoreLabel.setText("New High Score for " + difficulty + " mode!");
                    highScores.put(difficulty, attempts);
                    saveHighScores();
                } else {
                    scoreLabel.setText("");
                }

                submitButton.setEnabled(false);
                playAgainButton.setEnabled(true);
                changeDifficultyButton.setEnabled(true);
            } else {
                feedbackLabel.setText("Incorrect. Closest guess so far: " + closestGuess);
                feedbackLabel.setForeground(RED);
                flashField(RED);
            }
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Please enter a valid number.");
            feedbackLabel.setForeground(RED);
            flashField(RED);
        }
    }

    private void showHighScores() {
        StringBuilder sb = new StringBuilder("Current High Scores:\n\n");

        String[] orderedModes = {"Easy", "Normal", "Hard", "Impossible"};
        for (String level : orderedModes) {
            int score = highScores.getOrDefault(level, Integer.MAX_VALUE);
            sb.append(level).append(": ");
            sb.append(score == Integer.MAX_VALUE ? "---" : score + " attempts").append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetGame(String difficultyLevel) {
        if (difficultyLevel.equals("Easy")) maxRange = EASY;
        else if (difficultyLevel.equals("Normal")) maxRange = NORMAL;
        else if (difficultyLevel.equals("Hard")) maxRange = HARD;
        else maxRange = IMPOSSIBLE;

        target = random.nextInt(maxRange) + 1;
        attempts = 0;
        closestGuess = -1;
        closestDiff = Integer.MAX_VALUE;
        guessField.setText("");
        promptLabel.setText("Guess a number between 1 and " + maxRange);
        feedbackLabel.setText("Enter your guess below.");
        feedbackLabel.setForeground(Color.LIGHT_GRAY);
        scoreLabel.setText("");
        guessField.setBackground(FIELD_DEFAULT);
        submitButton.setEnabled(true);
        playAgainButton.setEnabled(false);
        changeDifficultyButton.setEnabled(false);
    }

    private void chooseNewDifficulty() {
        String[] options = {"Easy", "Normal", "Hard", "Impossible"};
        String choice = (String) JOptionPane.showInputDialog(this, "Select Difficulty:",
                "Difficulty", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice != null) {
            difficulty = choice;
            resetGame(difficulty);
        }
    }

    private void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String level = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    highScores.put(level, score);
                }
            }
        } catch (IOException | NumberFormatException e) {
            highScores.put("Easy", Integer.MAX_VALUE);
            highScores.put("Normal", Integer.MAX_VALUE);
            highScores.put("Hard", Integer.MAX_VALUE);
            highScores.put("Impossible", Integer.MAX_VALUE);
        }
    }

    private void saveHighScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save high scores.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GuessTheNumberGUI();
            }
        });
    }
}