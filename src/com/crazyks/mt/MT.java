package com.crazyks.mt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.crazyks.mt.MT.Position.Direction;

public class MT {
    
    protected final static int BOARD_EDGE_SIZE = 7;
    protected final static int BOARD_EDGE_WITH_WALL_SIZE = BOARD_EDGE_SIZE + 2;
    protected final static int BOARD_AREA = BOARD_EDGE_SIZE * BOARD_EDGE_SIZE;
    protected final static int BOARD_WITH_WALL_AREA = BOARD_EDGE_WITH_WALL_SIZE * BOARD_EDGE_WITH_WALL_SIZE;
    private final static Position CENTER_POSITION = new Position((BOARD_EDGE_SIZE + 1) >> 1, (BOARD_EDGE_SIZE + 1) >> 1);
    private final static Position LEFTTOP_POSITION = CENTER_POSITION.moveTo(-1, -1);
    private final static Position TOP_POSITION = CENTER_POSITION.moveTo(0, -1);
    private final static Position RIGHTTOP_POSITION = CENTER_POSITION.moveTo(1, -1);
    private final static Position LEFT_POSITION = CENTER_POSITION.moveTo(-1, 0);
    private final static Position RIGHT_POSITION = CENTER_POSITION.moveTo(1, 0);
    private final static Position LEFTBOTTOM_POSITION = CENTER_POSITION.moveTo(-1, 1);
    private final static Position BOTTOM_POSITION = CENTER_POSITION.moveTo(0, 1);
    private final static Position RIGHTBOTTOM_POSITION = CENTER_POSITION.moveTo(1, 1);
    private final static ArrayList<Position> SURROUND_POSITIONS = new ArrayList<Position>();
    private final static ArrayList<Position> AROUND_POSITIONS = new ArrayList<Position>();
    
    private final static Random mRandom = new Random(System.currentTimeMillis());
    
    protected final static int STATUS_WALL = -1;
    protected final static int STATUS_CLOSED = 0;
    protected final static int STATUS_FLAGED = 1;
    protected final static int STATUS_MARKED = 2;
    protected final static int STATUS_OPENED = 3;
    
    protected final static int CONTENT_EMPTY = 0;
    protected final static int CONTENT_NUM_1 = 1;
    protected final static int CONTENT_NUM_2 = 2;
    protected final static int CONTENT_NUM_3 = 3;
    protected final static int CONTENT_NUM_4 = 4;
    protected final static int CONTENT_NUM_5 = 5;
    protected final static int CONTENT_NUM_6 = 6;
    protected final static int CONTENT_NUM_7 = 7;
    protected final static int CONTENT_NUM_8 = 8;
    protected final static int CONTENT_MINE = 9;
    
    protected final static int RESID_MINE = 10;
    protected final static int RESID_CLOSED = 11;
    protected final static int RESID_FLAGED = 12;
    protected final static int RESID_MARKED = 13;
    protected final static int RESID_WALL = 14;
    protected final static int RESID_SAFETY = 15;
    
    protected final static int TYPE_COMMON = 0;
    protected final static int TYPE_SHOWMINE = 1;
    protected final static int TYPE_SHOWFLAG = 2;
    
    protected final static int ANSTYPE_CENTER = 0;
    protected final static int ANSTYPE_BOARD = 1;
    
    protected final static String KEY_LATEST_PLAY_DATE = "Date";
    protected final static String KEY_MODE = "Mode";
    protected final static String KEY_TIMEOUT = "Timeout";
    protected final static String KEY_ROUND = "Round";
    protected final static String KEY_GAMEOVERCONDITION = "GameOverCondition";
    protected final static String KEY_EMHANCEDMODE = "EnhancedMode";
    protected final static String KEY_PAUSEONERROR = "PauseOnError";
    protected final static String KEY_MARKIT = "MarkIt";
    protected final static String VALUE_MODE_EASY = "Easy";
    protected final static String VALUE_MODE_NORMAL = "Normal";
    protected final static String VALUE_MODE_HARD = "Hard";
    protected final static String VALUE_MODE_CUSTOM = "Custom";
    protected final static String VALUE_MODE_STUDY = "Study";
    
    static {
        initSurroundPositions();
        initAroundPositions();
    }
    
    protected static class Puzzle {
        final private byte[] board;
        final private boolean isCenterClear;
        final private boolean isBoardClear;
        final private int centerNumber;
        final private ArrayList<Integer> closedSafetyBlkPositions;
        
        public Puzzle(byte[] board, boolean isCenterClear, boolean isBoardClear, int centerNumber, ArrayList<Integer> posList) {
            this.board = board;
            this.isCenterClear = isCenterClear;
            this.isBoardClear = isBoardClear;
            this.centerNumber = centerNumber;
            this.closedSafetyBlkPositions = posList;
        }
        
        public int getCenterNumber() {
        	return centerNumber;
        }
        
        public ArrayList<Integer> getClosedSafetyBlkPositions() {
        	return closedSafetyBlkPositions;
        }
        
        public byte[] getBoard() {
            return this.board;
        }
        
        public boolean isCenterClear() {
            return this.isCenterClear;
        }
        
        public boolean isBoardClear() {
            return this.isBoardClear;
        }
    }
    
    protected static class Position {
        final private int x;
        final private int y;
        final private int index;
        
        public enum Direction {
            LEFTABOVE,
            ABOVE,
            RIGHTABOVE,
            LEFT,
            CENTER,
            RIGHT,
            LEFTBELOW,
            BELOW,
            RIGHTBELOW,
            UNKNOWN
        }
        
        public Position() {
            x = 0;
            y = 0;
            index = formatToIndex();
        }
        
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
            index = formatToIndex();
        }
        
        public Position(Position other) {
            this.x = other.x;
            this.y = other.y;
            index = formatToIndex();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Position) {
                return by((Position) obj) == Direction.CENTER;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
        
        private int formatToIndex() {
            return y * BOARD_EDGE_WITH_WALL_SIZE + x;
        }
        
        public Position moveTo(int offsetX, int offsetY) {
            return new Position(x + offsetX, y + offsetY);
        }
        
        public int getIndex() {
            return index;
        }

        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public Direction by(Position other) {
            if (other == null) {
                return Direction.UNKNOWN;
            } else {
                if (this.x < other.x) {
                    if (this.y < other.y) {
                        return Direction.LEFTABOVE;
                    } else if (this.y == other.y) {
                        return Direction.LEFT;
                    } else if (this.y > other.y) {
                        return Direction.LEFTBELOW;
                    }
                } else if (this.x == other.x) {
                    if (this.y < other.y) {
                        return Direction.ABOVE;
                    } else if (this.y == other.y) {
                        return Direction.CENTER;
                    } else if (this.y > other.y) {
                        return Direction.BELOW;
                    }
                } else if (this.x > other.x) {
                    if (this.y < other.y) {
                        return Direction.RIGHTABOVE;
                    } else if (this.y == other.y) {
                        return Direction.RIGHT;
                    } else if (this.y > other.y) {
                        return Direction.RIGHTBELOW;
                    }
                }
            }
            return Direction.UNKNOWN;
        }
        
    }
    
    protected static class Board {
        private Cell[] cells = null;

        public Board(Cell[] cells) {
            if (cells != null) {
                this.cells = cells;
                for (int i = 0; i < BOARD_EDGE_WITH_WALL_SIZE; i++) {
                    cells[i * BOARD_EDGE_WITH_WALL_SIZE + 0].status = STATUS_WALL;
                    cells[i * BOARD_EDGE_WITH_WALL_SIZE + BOARD_EDGE_SIZE + 1].status = STATUS_WALL;
                }
                for (int i = 0; i < BOARD_EDGE_WITH_WALL_SIZE; i++) {
                    cells[i].status = STATUS_WALL;
                    cells[(BOARD_EDGE_SIZE + 1) * BOARD_EDGE_WITH_WALL_SIZE + i].status = STATUS_WALL;
                }
                /*for (int y = 1; y <= BOARD_EDGE_SIZE; y++) {
                    for (int x = 1; x <= BOARD_EDGE_SIZE; x++) {
                        int myIndex = y * BOARD_EDGE_WITH_WALL_SIZE + x;
                        System.out.print(String.format("%d ", cells[myIndex].what));
                    }
                    debug();
                }*/
            } else {
                debug("Warning: Try to generate the board with an illegal cell array!");
            }
        }
        
        public Cell[] getCells() {
            return this.cells;
        }
    }
    
    protected static class Cell {
        public int status = STATUS_CLOSED;
        public int what = CONTENT_EMPTY;
        
        private Cell(int what) {
            status = STATUS_CLOSED;
            this.what = what;
        }
        
        public static Cell findCell(Cell[] cells, Position position) {
            if (cells == null || position == null) {
                return null;
            }
            int index = position.y * BOARD_EDGE_WITH_WALL_SIZE + position.x;
            if (index < 0 || index >= cells.length) {
                return null;
            } else {
                return cells[index];
            }
        }
        
        public static Cell[] createCells(Position[] positions) {
            if (positions == null) return null;
            int cells[] = new int[BOARD_WITH_WALL_AREA];
            int n = 0;
            for (n = 0; n < BOARD_WITH_WALL_AREA; n++) {
                cells[n] = CONTENT_EMPTY;
            }
            for (n = 0; n < positions.length; n++) {
                if (positions[n] == null) {
                    return null;
                }
                cells[positions[n].y * BOARD_EDGE_WITH_WALL_SIZE + positions[n].x] = CONTENT_MINE;
            }
            int i, j, x, y, count;
            for (i = 1; i <= BOARD_EDGE_SIZE; i++) {
                for (j = 1; j <= BOARD_EDGE_SIZE; j++) {
                    if (cells[i * BOARD_EDGE_WITH_WALL_SIZE + j] != CONTENT_MINE) {
                        count = 0;
                        for (x = i - 1; x <= i + 1; x++) {
                            for (y = j - 1; y <= j + 1; y++) {
                                if (cells[x * BOARD_EDGE_WITH_WALL_SIZE + y] == CONTENT_MINE) {
                                    count++;
                                }
                            }
                        }
                        cells[i * BOARD_EDGE_WITH_WALL_SIZE + j] = count;
                    }
                }
            }
            Cell[] finalCells = new Cell[BOARD_WITH_WALL_AREA];
            for (n = 0; n < BOARD_WITH_WALL_AREA; n++) {
                finalCells[n] = new Cell(cells[n]);
            }
            return finalCells;
        }
    }
    
    protected static class Counter {
        private int value;
        
        public Counter(int value) {
            this.value = value;
        }
        
        public synchronized void add() {
            value++;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    private static void initSurroundPositions() {
        SURROUND_POSITIONS.clear();
        SURROUND_POSITIONS.add(LEFTTOP_POSITION);
        SURROUND_POSITIONS.add(TOP_POSITION);
        SURROUND_POSITIONS.add(RIGHTTOP_POSITION);
        SURROUND_POSITIONS.add(LEFT_POSITION);
        SURROUND_POSITIONS.add(RIGHT_POSITION);
        SURROUND_POSITIONS.add(LEFTBOTTOM_POSITION);
        SURROUND_POSITIONS.add(BOTTOM_POSITION);
        SURROUND_POSITIONS.add(RIGHTBOTTOM_POSITION);
    }
    
    private static void initAroundPositions() {
        AROUND_POSITIONS.clear();
        for (int i = 1; i <= BOARD_EDGE_SIZE; i++) {
            for (int j = 1; j <= BOARD_EDGE_SIZE; j++) {
                AROUND_POSITIONS.add(new Position(i, j));
            }
        }
        AROUND_POSITIONS.remove(CENTER_POSITION);
        AROUND_POSITIONS.removeAll(SURROUND_POSITIONS);
    }
    
    private static int getPositiveInt(int oneSide, int otherSize) {
        if (oneSide < 0 || otherSize < 0) {
            throw new IllegalArgumentException("The range CANNOT be negative!");
        } else if (oneSide == otherSize) {
            throw new IllegalArgumentException("The two sides CANNOT be the same!");
        }
        int mode = Math.abs(oneSide - otherSize) + 1;
        int offset = Math.min(oneSide, otherSize);
        return Math.abs(mRandom.nextInt() + 1) % mode + offset;
    }
    
    private static Position getPosition() {
        return new Position(getPositiveInt(1, BOARD_EDGE_SIZE), getPositiveInt(1, BOARD_EDGE_SIZE));
    }

    public static boolean judge(final Puzzle puz, final int answerType, final boolean answer) {
        if (puz != null) {
            switch (answerType) {
            case ANSTYPE_CENTER:
                return puz.isCenterClear() == answer;
            case ANSTYPE_BOARD:
                return puz.isBoardClear() == answer;
            default:
                break;
            }
        }
        return false;
    }
    
    public static Puzzle getPuzzle() {
        boolean isCenterClear = false;
        boolean isBoardClear = false;
        final int totalNumber = getPositiveInt(10, 20);
        final Counter openedCounter = new Counter(0);
        ArrayList<Position> positions = new ArrayList<Position>(totalNumber);
        for (int i = 0; i < totalNumber;) {
            Position pos = getPosition();
            if (pos.by(CENTER_POSITION) != Direction.CENTER && !positions.contains(pos)) {
                positions.add(pos);
                i++;
            }
        }
        Cell[] cells = new Board(Cell.createCells(positions.toArray(new Position[totalNumber]))).getCells();
        int center = 0;
        ArrayList<Integer> posList = new ArrayList<Integer>();
        if (cells != null) {
            HashSet<Position> digPositionList = new HashSet<Position>();
            digPositionList.add(CENTER_POSITION);
            int numMissingSurround = 0;
            int numDigSurroundMax = 8 - cells[CENTER_POSITION.getIndex()].what;
            if (numDigSurroundMax > 0) {
                numMissingSurround = getPositiveInt(0, 1); // missing one mine at most!
                missIt(cells, digPositionList, SURROUND_POSITIONS, numMissingSurround);
            }
            int numMissingAround = getPositiveInt(0, isCenterClear ? 2 : 1); // make sure the range of missing mine is 0~2
            missIt(cells, digPositionList, AROUND_POSITIONS, numMissingAround);
            for (Position pos : digPositionList) {
                dig(openedCounter, cells, pos.getX(), pos.getY());
            }
            // debug("Opened: " + openedCounter.getValue() + "; Mines: " + totalNumber);
            int closedSurround = 0;
            for (Position p : SURROUND_POSITIONS) {
                if (cells[p.getIndex()].status == STATUS_CLOSED) {
                    closedSurround++;
                }
            }
            center = cells[CENTER_POSITION.getIndex()].what;
            isCenterClear = cells[CENTER_POSITION.getIndex()].what == closedSurround;
            isBoardClear = BOARD_AREA - totalNumber == openedCounter.getValue();
            for (int i = 0 ; i < cells.length; i++) {
            	Cell c = cells[i]; 
            	if (c.status == STATUS_CLOSED && c.what >= 0 && c.what <= 8) {
            		posList.add(i);
            	}
            }
        }
        return new Puzzle(cellsToBytes(cells, TYPE_COMMON), isCenterClear, isBoardClear, center, posList);
    }
    
    private static void missIt(final Cell[] cells, final HashSet<Position> set, final ArrayList<Position> list, final int numMissing) {
        int numMissingRemain = numMissing;
        for (Position pos : list) {
            boolean missThis = false;
            if (numMissingRemain > 0) {
                missThis = getPositiveInt(0, 1) == 1;
                if (missThis) {
                    numMissingRemain--;
                }
            }
            if (cells[pos.getIndex()].what != CONTENT_MINE && !missThis) {
                set.add(pos);
            }
        }
    }
    
    private static boolean canDig(Cell[] cells, int x, int y) {
        int myStatus = cells[y * BOARD_EDGE_WITH_WALL_SIZE + x].status;
        return (myStatus == STATUS_CLOSED || myStatus == STATUS_MARKED);
    }
    
    private static void dig(final Counter opendCounter, Cell[] cells, int x, int y) {
        if (cells != null && canDig(cells, x, y)) {
            Cell theCell = cells[y * BOARD_EDGE_WITH_WALL_SIZE + x];
            theCell.status = STATUS_OPENED;
            opendCounter.add();
            if (theCell.what == CONTENT_EMPTY) {
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (i >= 1 && i <= BOARD_EDGE_SIZE && j >= 1 && j <= BOARD_EDGE_SIZE) {
                            dig(opendCounter, cells, i, j);
                        }
                    }
                }
            }
        }
    }
    
    private static byte[] cellsToBytes(Cell[] cells, int type) {
        if (cells == null) {
            return null;
        }
        int len = cells.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            switch (cells[i].status) {
            case STATUS_WALL:
                bytes[i] = (byte) (RESID_WALL & 0xff);
                break;
            case STATUS_CLOSED:
                bytes[i] = (byte) (RESID_CLOSED & 0xff);
                switch (type) {
                case TYPE_SHOWMINE:
                    if (cells[i].what == CONTENT_MINE) {
                        bytes[i] = (byte) (RESID_MINE & 0xff);
                    }
                    break;
                case TYPE_SHOWFLAG:
                    if (cells[i].what == CONTENT_MINE) {
                        bytes[i] = (byte) (RESID_FLAGED & 0xff);
                    }
                    break;
                default:
                    break;
                }
                break;
            case STATUS_FLAGED:
                bytes[i] = (byte) (RESID_FLAGED & 0xff);
                break;
            case STATUS_MARKED:
                bytes[i] = (byte) (RESID_MARKED & 0xff);
                break;
            case STATUS_OPENED:
                bytes[i] = (byte) (cells[i].what & 0xff);
                break;
            default:
                bytes[i] = (byte) (RESID_CLOSED & 0xff);
                break;
            }
        }
        return bytes;
    }
    
    protected static void debug() {
        System.out.println();
    }
    
    protected static void debug(String msg) {
        System.out.println(msg);
    }
    
}
