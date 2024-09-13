


import java.util.Scanner;

public class Main {

    static char[][] board = {
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
    };
    static char player = 'X';
    static char computer = 'O';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printBoard();

        while (true) {
            playerMove(scanner);
            if (isGameOver()) {
                printBoard();
                break;
            }
            printBoard();
            computerMove();
            if (isGameOver()) {
                printBoard();
                break;
            }
            printBoard();
        }
        scanner.close();
    }

    static void printBoard() {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    static void playerMove(Scanner scanner) {
        int row, col;
        while (true) {
            System.out.print("Enter your move (row and column): ");
            row = scanner.nextInt();
            col = scanner.nextInt();
            if (row >= 0 && col >= 0 && row < 3 && col < 3 && board[row][col] == ' ') {
                board[row][col] = player;
                break;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }

    static void computerMove() {
        int[] bestMove = minimax(board, computer);
        board[bestMove[1]][bestMove[2]] = computer;
    }

    static int[] minimax(char[][] board, char currentPlayer) {
        char winner = checkWinner();
        if (winner == player) {
            return new int[]{-1};
        } else if (winner == computer) {
            return new int[]{1};
        } else if (isBoardFull()) {
            return new int[]{0};
        }

        int bestScore = (currentPlayer == computer) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = currentPlayer;
                    if (currentPlayer == computer) {
                        currentScore = minimax(board, player)[0];
                        if (currentScore > bestScore) {
                            bestScore = currentScore;
                            bestRow = i;
                            bestCol = j;
                        }
                    } else {
                        currentScore = minimax(board, computer)[0];
                        if (currentScore < bestScore) {
                            bestScore = currentScore;
                            bestRow = i;
                            bestCol = j;
                        }
                    }
                    board[i][j] = ' ';
                }
            }
        }
        return new int[]{bestScore, bestRow, bestCol};
    }

    static boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }



    static char checkWinner() {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != ' ') {
                return board[i][0];
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != ' ') {
                return board[0][i];
            }
        }
        // Check diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ') {
            return board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != ' ') {
            return board[0][2];
        }
        return ' ';
    }

    static boolean isGameOver() {
        return checkWinner() != ' ' || isBoardFull();
    }
}
