package com.maze.entity;

public class Board {
    public static final char WALL = '*';
    public static final char START = 'S';
    public static final char END = 'E';
    public static final char PATH = '-';
    public static final char EMPTY = '0';

    private int[] maze_size;
    private int[] current_location;
    private String status;
    private int levels_completed;
    private int total_levels;

    private char[][] maze;
    //use this to determine which direction to go if only multiple passed paths can go, the less passed should b e choosen
    private int[][] passCount;

    public Board() {
        this.maze_size = new int[2];
        current_location = new int[2];
    }

    public void createBoard() {
        if (maze_size[0] == 0 || maze_size[1] == 0) {
            System.err.println("Maze size is 0");
            return;
        }
        System.out.println("Maze width: " + maze_size[0] + " Maze highth: " + maze_size[1]);
        maze = new char[maze_size[0]][maze_size[1]];
        passCount = new int[maze_size[0]][maze_size[1]];

        for (int i = 0; i < maze_size[1]; i++) {
            for (int j = 0; j < maze_size[0]; j++) {
                maze[j][i] = EMPTY;
            }
        }
        maze[current_location[0]][current_location[1]] = START;
        System.out.println("Starting at: " + current_location[0] + " " + current_location[1]);
    }

    //get the next move direction based on the current location
    public String getNextMove() {
        String direction = null;

        if (current_location[0] < maze_size[0] - 1) {
            char right = maze[current_location[0]+1][current_location[1]];
            //if the path has never passed before, it should should be choosen
            if (right == EMPTY)
                return Action.RIGHT;

            //if it has been passed before, wait to see if there is other direction that never passed
            if (right == PATH)
                direction = Action.RIGHT;
        }

        if (current_location[1] < maze_size[1] - 1) {
            char down = maze[current_location[0]][current_location[1]+1];
            if (down == EMPTY)
                return Action.DOWN;

            if (down == PATH) {
                if (direction == null)
                    direction = Action.DOWN;
                //choose the less passed path
                else {
                    int[] position1 = getNextPosition(direction);
                    int[] position2 = getNextPosition(Action.DOWN);
                    direction = passCount[position1[0]][position1[1]] < passCount[position2[0]][position2[1]] ? direction : Action.DOWN;
                }
            }
        }

        if (current_location[0] > 0) {
            char left = maze[current_location[0]-1][current_location[1]];
            if (left == EMPTY)
                return Action.LEFT;

            if (left == PATH) {
                if (direction == null)
                    direction = Action.LEFT;
                else {
                    int[] position1 = getNextPosition(direction);
                    int[] position2 = getNextPosition(Action.LEFT);
                    direction = passCount[position1[0]][position1[1]] < passCount[position2[0]][position2[1]] ? direction : Action.LEFT;
                }
            }
        }

        if (current_location[1] > 0) {
            char up = maze[current_location[0]][current_location[1]-1];
            if (up == EMPTY)
                return Action.UP;

            if (up == PATH) {
                if (direction == null)
                    direction = Action.UP;
                else {
                    int[] position1 = getNextPosition(direction);
                    int[] position2 = getNextPosition(Action.UP);
                    direction = passCount[position1[0]][position1[1]] < passCount[position2[0]][position2[1]] ? direction : Action.UP;
                }
            }
        }

        return direction;
    }

    //increment the pass count, and set the value to be path
    public void setPass(int[] position) {
        passCount[position[0]][position[1]]++;
        setValue(position, PATH);
    }

    public void setValue(int[] position, char value) {
        maze[position[0]][position[1]] = value;
    }

    //get the position based on direction
    public int[] getNextPosition(String movedDircetion) {
        int[] position = new int[2];
        position[0] = current_location[0];
        position[1] = current_location[1];

        if (movedDircetion.equals(Action.UP))
            position[1] = current_location[1] - 1;
        else if (movedDircetion.equals(Action.DOWN))
            position[1] = current_location[1] + 1;
        else if (movedDircetion.equals(Action.LEFT))
            position[0] = current_location[0] - 1;
        else if (movedDircetion.equals(Action.RIGHT))
            position[0] = current_location[0] + 1;

        return position;
    }

    //print the board
    public void printBoard() {
        for (int i = 0; i < maze_size[1]; i++) {
            for (int j = 0; j < maze_size[0]; j++) {
                System.out.print(maze[j][i] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //print the board
    public void printBoardWithPassCount() {
        for (int i = 0; i < maze_size[1]; i++) {
            for (int j = 0; j < maze_size[0]; j++) {
                if (maze[j][i] == PATH) {
                    System.out.print(passCount[j][i] + " ");
                } else {
                    System.out.print(maze[j][i] + " ");
                }
            }
            System.out.println();
            //System.out.println("Current level is " + (levels_completed + 1) + " Total level: " + total_levels);
        }
    }

    public int[] getMaze_size() {
        return maze_size;
    }

    public void setMaze_size(int[] maze_size) {
        this.maze_size = maze_size;
    }

    public int[] getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(int[] current_location) {
        this.current_location = current_location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLevels_completed() {
        return levels_completed;
    }

    public void setLevels_completed(int levels_completed) {
        this.levels_completed = levels_completed;
    }

    public int getTotal_levels() {
        return total_levels;
    }

    public void setTotal_levels(int total_levels) {
        this.total_levels = total_levels;
    }
}
