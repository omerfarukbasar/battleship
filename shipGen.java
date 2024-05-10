import java.util.Random;

public class shipGen {
    // Data fields
    private static final int BOARD_SIZE = 10;
    private static final int EMPTY = 0;
    private static final int OCCUPIED = 1;
    private static final int[] SHIPS = {5, 4, 3, 3, 2};
    private static final Random random = new Random();

    // Generates 2D array containing ship coordinates
    public static int[][] generateBoard() {
        // Initialize board with all empty spots
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = EMPTY;

        // For every ship in the legion
        for (int length : SHIPS) {
            // Attempt to place a ship on the board
            boolean placed = false;
            while (!placed)
                placed = placeShip(length, board);
        }
        // Return board filled with ships
        return board;
    }

    // Attempts to place a ship on the board
    private static boolean placeShip(int length, int[][] board) {
        // Randomly choose if orientation is horizontal
        boolean horizontal = random.nextBoolean();

        // Determine starting coordinates based on orientation
        // Horizontal ships are constructed from left to right
        // Vertical ships are constructed from top to bottom
        // Bounds are ensured by lowering possible choices for coordinates depending on orientation and ship size
        // Ex: For a vertical ship of size 5, max possible row can be 6 because the ship occupies slots 6 through 10.
        int startRow = horizontal ? random.nextInt(BOARD_SIZE) : random.nextInt(BOARD_SIZE - length + 1);
        int startCol = horizontal ? random.nextInt(BOARD_SIZE - length + 1) : random.nextInt(BOARD_SIZE);

        // Check if the ship can be placed at the selected coordinates without overlapping
        for (int i = 0; i < length; i++) {
            int row = horizontal ? startRow : (startRow + i);
            int col = horizontal ? (startCol + i) : startCol;

            // When coordinate is occupied by another ship
            if (board[row][col] != EMPTY)
                return false;
        }

        // Place the ship on the board
        for (int i = 0; i < length; i++) {
            int row = horizontal ? startRow : (startRow + i);
            int col = horizontal ? (startCol + i) : startCol;
            // Mark on board
            board[row][col] = OCCUPIED;
        }

        // Once ship is placed
        return true;
    }
}
