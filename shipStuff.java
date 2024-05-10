import java.util.Random;

public class shipStuff {
    // Data fields
    private static final int GRID_SIZE = 10;
    private static final int EMPTY = 0;
    private static final int OCCUPIED = 1;
    private static final int[] SHIPS = {5, 4, 3, 3, 2};
    private static final Random random = new Random();

    // Generates 2D array containing ship coordinates
    public static int[][] generateBoard() {
        // Grid to represent the battleship board
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];

        // Initialize grid with empty cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = EMPTY;
            }
        }

        // Place all ships
        for (int length : SHIPS) {
            boolean placed = false;

            // Keep trying until the ship is placed
            while (!placed) {
                placed = placeShip(length, grid);
            }
        }
        return grid;
    }

    // Attempts to place a ship on the grid
    private static boolean placeShip(int length, int[][] grid) {
        // Randomly choose orientation: horizontal or vertical
        boolean horizontal = random.nextBoolean();

        // Determine starting coordinates based on orientation
        int startRow = horizontal ? random.nextInt(GRID_SIZE) : random.nextInt(GRID_SIZE - length + 1);
        int startCol = horizontal ? random.nextInt(GRID_SIZE - length + 1) : random.nextInt(GRID_SIZE);

        // Check if the ship can be placed at the selected coordinates without overlapping
        for (int i = 0; i < length; i++) {
            int row = horizontal ? startRow : startRow + i;
            int col = horizontal ? startCol + i : startCol;

            // When coordinate is occupied by another ship
            if (grid[row][col] != EMPTY) {return false;}
        }

        // Place the ship on the grid
        for (int i = 0; i < length; i++) {
            int row = horizontal ? startRow : startRow + i;
            int col = horizontal ? startCol + i : startCol;

            grid[row][col] = OCCUPIED;
        }

        // Once ship is placed, return
        return true;
    }
}
