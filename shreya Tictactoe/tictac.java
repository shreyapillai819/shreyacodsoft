import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacToe {

    public static final int WIN = 1000;
    public static final int DRAW = 0;
    public static final int LOSS = -1000;

    public static final char AI_MARKER = 'O';
    public static final char PLAYER_MARKER = 'X';
    public static final char EMPTY_SPACE = '-';

    public static final int START_DEPTH = 0;

    // Print game state
    public static void printGameState(int state) {
        if (state == WIN) {
            System.out.println("WIN");
        } else if (state == DRAW) {
            System.out.println("DRAW");
        } else if (state == LOSS) {
            System.out.println("LOSS");
        }
    }

    // All possible winning states
    public static List<List<int[]>> winningStates = List.of(
            // Every row
            List.of(new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2}),
            List.of(new int[]{1, 0}, new int[]{1, 1}, new int[]{1, 2}),
            List.of(new int[]{2, 0}, new int[]{2, 1}, new int[]{2, 2}),
            // Every column
            List.of(new int[]{0, 0}, new int[]{1, 0}, new int[]{2, 0}),
            List.of(new int[]{0, 1}, new int[]{1, 1}, new int[]{2, 1}),
            List.of(new int[]{0, 2}, new int[]{1, 2}, new int[]{2, 2}),
            // Every diagonal
            List.of(new int[]{0, 0}, new int[]{1, 1}, new int[]{2, 2}),
            List.of(new int[]{2, 0}, new int[]{1, 1}, new int[]{0, 2})
    );

    // Print the current board state
    public static void printBoard(char[][] board) {
        System.out.println();
        for (int i = 0; i < 3; i++) {
            System.out.println(board[i][0] + " | " + board[i][1] + " | " + board[i][2]);
            if (i < 2) {
                System.out.println("---------");
            }
        }
        System.out.println();
    }

    // Get all available legal moves (spaces that are not occupied)
    public static List<int[]> getLegalMoves(char[][] board) {
        List<int[]> legalMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != AI_MARKER && board[i][j] != PLAYER_MARKER) {
                    legalMoves.add(new int[]{i, j});
                }
            }
        }
        return legalMoves;
    }

    // Check if a position is occupied
    public static boolean positionOccupied(char[][] board, int[] pos) {
        List<int[]> legalMoves = getLegalMoves(board);
        for (int[] legalMove : legalMoves) {
            if (pos[0] == legalMove[0] && pos[1] == legalMove[1]) {
                return false;
            }
        }
        return true;
    }

    // Get all board positions occupied by the given marker
    public static List<int[]> getOccupiedPositions(char[][] board, char marker) {
        List<int[]> occupiedPositions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == marker) {
                    occupiedPositions.add(new int[]{i, j});
                }
            }
        }
        return occupiedPositions;
    }

    // Check if the board is full
    public static boolean boardIsFull(char[][] board) {
        List<int[]> legalMoves = getLegalMoves(board);
        return legalMoves.isEmpty();
    }

    // Check if the game has been won
    public static boolean gameIsWon(List<int[]> occupiedPositions) {
        boolean gameWon;
        for (List<int[]> winState : winningStates) {
            gameWon = true;
            for (int[] pos : winState) {
                if (!occupiedPositions.contains(pos)) {
                    gameWon = false;
                    break;
                }
            }
            if (gameWon) {
                return true;
            }
        }
        return false;
    }

    public static char getOpponentMarker(char marker) {
        return marker == PLAYER_MARKER ? AI_MARKER : PLAYER_MARKER;
    }

    // Check if someone has won or lost
    public static int getBoardState(char[][] board, char marker) {
        char opponentMarker = getOpponentMarker(marker);
        List<int[]> occupiedPositions = getOccupiedPositions(board, marker);
        boolean isWon = gameIsWon(occupiedPositions);
        if (isWon) {
            return WIN;
        }
        occupiedPositions = getOccupiedPositions(board, opponentMarker);
        boolean isLost = gameIsWon(occupiedPositions);
        if (isLost) {
            return LOSS;
        }
        if (boardIsFull(board)) {
            return DRAW;
        }
        return DRAW;
    }

    // Apply the minimax game optimization algorithm
    public static int[] minimaxOptimization(char[][] board, char marker, int depth, int alpha, int beta) {
        int[] bestMove = {-1, -1};
        int bestScore = (marker == AI_MARKER) ? LOSS : WIN;
        if (boardIsFull(board) || DRAW != getBoardState(board, AI_MARKER)) {
            bestScore = getBoardState(board, AI_MARKER);
            return new int[]{bestScore, -1, -1};
        }
        List<int[]> legalMoves = getLegalMoves(board);
        for (int[] currMove : legalMoves) {
            board[currMove[0]][currMove[1]] = marker;
            if (marker == AI_MARKER) {
                int[] score = minimaxOptimization(board, PLAYER_MARKER, depth + 1, alpha, beta);
                if (bestScore < score[0]) {
                    bestScore = score[0] - depth * 10;
                    bestMove = currMove;
                    alpha = Math.max(alpha, bestScore);
                    board[currMove[0]][currMove[1]] = EMPTY_SPACE;
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else {
                int[] score = minimaxOptimization(board, AI_MARKER, depth + 1, alpha, beta);
                if (bestScore > score[0]) {
                    bestScore = score[0] + depth * 10;
                    bestMove = currMove;
                    beta = Math.min(beta, bestScore);
                    board[currMove[0]][currMove[1]] = EMPTY_SPACE;
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            board[currMove[0]][currMove[1]] = EMPTY_SPACE;
        }
        return new int[]{bestScore, bestMove[0], bestMove[1]};
    }

    // Check if the game is finished
    public static boolean gameIsDone(char[][] board) {
        return boardIsFull(board) || DRAW != getBoardState(board, AI_MARKER);
    }

    public static void main(String[] args) {
        char[][] board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY_SPACE;
            }
        }
        System.out.println("********************************\n\n\tTic Tac Toe AI\n\n********************************\n");
        System.out.println("Player = X\t AI Computer = O\n");

        printBoard(board);

        Scanner scanner = new Scanner(System.in);

        while (!gameIsDone(board)) {
            int row, col;
            System.out.print("Row play: ");
            row = scanner.nextInt();
            System.out.print("Col play: ");
            col = scanner.nextInt();
            System.out.println();

            if (positionOccupied(board, new int[]{row, col})) {
                System.out.println("The position (" + row + ", " + col + ") is occupied. Try another one...\n");
                continue;
            } else {
                board[row][col] = PLAYER_MARKER;
            }

            int[] aiMove = minimaxOptimization(board, AI_MARKER, START_DEPTH, LOSS, WIN);
            board[aiMove[1]][aiMove[2]] = AI_MARKER;

            printBoard(board);
        }

        System.out.println("********** GAME OVER **********\n");

        int playerState = getBoardState(board, PLAYER_MARKER);

        System.out.print("PLAYER ");
        printGameState(playerState);
    }
}
