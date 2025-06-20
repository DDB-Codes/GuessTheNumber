import java.util.Scanner;
import java.util.Random;

public class GuessTheNumberV2 {

    private static final int EASY = 10;
    private static final int NORMAL = 100;
    private static final int HARD = 1000;
    private static final int IMPOSSIBLE = 1_000_000;

    private static int easyHS = Integer.MAX_VALUE;
    private static int normalHS = Integer.MAX_VALUE;
    private static int hardHS = Integer.MAX_VALUE;
    private static int impossibleHS = Integer.MAX_VALUE;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Random random = new Random();

        while (true) {
            System.out.println("Pick a Difficulty");
            System.out.println(" 1 - Easy | 2 - Normal | 3 - Hard | 4 - Impossible | 5 - HighScores | 6 - QUIT");
            int diffChoice = input.nextInt();
            System.out.println();

            switch (diffChoice) {
                case 1:
                    easyHS = playGame("Easy", EASY, easyHS, input, random);
                    break;
                case 2:
                    normalHS = playGame("Normal", NORMAL, normalHS, input, random);
                    break;
                case 3:
                    hardHS = playGame("Hard", HARD, hardHS, input, random);
                    break;
                case 4:
                    impossibleHS = playGame("Impossible", IMPOSSIBLE, impossibleHS, input, random);
                    break;
                case 5:
                    showHighScores();
                    break;
                case 6:
                    System.out.println("Thanks for playing!");
                    input.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.\n");
            }
        }
    }

    private static int playGame(String difficulty, int maxRange, int highScore, Scanner input, Random random) {
        int target = random.nextInt(maxRange) + 1;
        int attempts = 0;
        int closestGuess = -1;
        int closestDiff = Integer.MAX_VALUE;

        System.out.println("Pick a number between 1 and " + maxRange);

        while (true) {
            int guess = input.nextInt();
            System.out.println();
            attempts++;

            int currentDiff = Math.abs(target - guess);
            if (currentDiff < closestDiff) {
                closestDiff = currentDiff;
                closestGuess = guess;
            }

            if (guess == target) {
                System.out.println("You got the number right in " + attempts + " attempts!");
                if (attempts < highScore) {
                    System.out.println("You got a new HighScore!");
                    highScore = attempts;
                }
                System.out.println();
                break;
            } else {
                System.out.println("Incorrect. Your closest guess so far is: " + closestGuess);
                System.out.println("Try again:\n");
            }
        }
        return highScore;
    }

    private static void showHighScores() {
        System.out.println();
        if (easyHS < Integer.MAX_VALUE) System.out.println("Easy HighScore: " + easyHS + " attempts");
        if (normalHS < Integer.MAX_VALUE) System.out.println("Normal HighScore: " + normalHS + " attempts");
        if (hardHS < Integer.MAX_VALUE) System.out.println("Hard HighScore: " + hardHS + " attempts");
        if (impossibleHS < Integer.MAX_VALUE) System.out.println("Impossible HighScore: " + impossibleHS + " attempts");
        System.out.println();
    }
}
