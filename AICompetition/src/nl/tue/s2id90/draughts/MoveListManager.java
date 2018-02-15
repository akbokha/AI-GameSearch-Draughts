package nl.tue.s2id90.draughts;

import java.awt.event.KeyEvent;
import java.util.List;
import org10x10.dam.game.BoardState;
import org10x10.dam.game.Move;
import org10x10.dam.game.MoveGenerator;
import org10x10.dam.ui.Board;
import org10x10.dam.ui.awt.ListListener;
import org10x10.dam.ui.swing.movelist.JMoveList;
import org10x10.dam.ui.swing.movelist.MoveListKeyHandler;

/**
 *
 * @author huub
 */
public class MoveListManager {
    final Board board;

    private final JMoveList moveList;

    public MoveListManager(JMoveList list, Board board) {
        this.moveList = list;
        this.board = board;

        moveList.addListListener(new ListListener() {

            @Override
            public void onAction(int i) {
                gotoBoardState(i);
            }

            @Override
            public void onSelect(int i) {
                gotoBoardState(i);
            }
        });

        moveList.setMoveListKeyHandler(new DaoMoveListKeyHandler());
    }

    private void gotoBoardState(int index) {
        BoardState bs = moveList.getModel().getBoardState(index);
        setBoardState(bs, false);
    }
    
    protected void setBoardState(BoardState bs, boolean b) {
        board.startUpdate();
        board.setBoardState(bs);
        board.endUpdate();
    }
    
    private BoardState getBoardState() {
        return board.getBoardState();
    }
    
    private Board getBoard() {
        return board;
    }

    public void onMove(Move m) {
        boolean newMove = false;
        int selection = moveList.getSelectedIndex();
        if (selection == moveList.getModel().size() - 1) {
            newMove = true;
        } else if (!m.equals(moveList.getModel().get(selection + 1))) {
            moveList.getModel().subList(selection + 1, moveList.getModel().size()).clear(); // remove this range from moveModel
            newMove = true;
        }
        if (newMove) moveList.getModel().add(m);
        moveList.setSelectedIndex(selection + 1);

        if (newMove) {
            handleForcedMove();
        }
    }

    @SuppressWarnings("empty-statement")
    private void handleForcedMove() {
        MoveGenerator g = new MoveGenerator();
        BoardState bs = getBoardState();
        List<Move> moves = g.generateMoves(bs);
        if (moves.size() == 1) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            ;
            Move m = moves.get(0);
            getBoard().animateMoveForward(m, 200);
        }
    }

     //<editor-fold defaultstate="collapsed" desc="keyboard navigation">
    class DaoMoveListKeyHandler implements MoveListKeyHandler {

        public void keyPressed(KeyEvent evt) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_HOME:
                    gotoMove(-1, false);
                    break;
                case KeyEvent.VK_END:
                    gotoMove(moveList.getModel().size() - 1, false);
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_UP:
                    goBackward();
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_DOWN:
                    goForward();
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    goForward(10);
                    break;
                case KeyEvent.VK_PAGE_UP:
                    goBackward(10);
                    break;
                default:
            }
            //updateGUI();
            evt.consume();
        }
    }

    /**
     * Go to the position after the move with the given index (-1 <= index < moveList.moveModelSize()).
     * @param index
     * @param animateMoves
     */
    public void gotoMove(int index, boolean animateMoves) {
        final int captureDelay = 100;
        if ((index < -1) || (index >= moveList.getModel().size())) {
            return;
        }
        int moveListIndex = moveList.getSelectedIndex();
        getBoard().startUpdate();
        if (moveListIndex < index) {
            for (int j = moveListIndex + 1; j <= index; j++) {
                Move m = moveList.getModel().get(j);
                assert (getBoard().getBoardState().isWhiteToMove() == m.isWhiteMove());
                if (animateMoves) {
                    getBoard().animateMoveForward(m, captureDelay);
                } else {
                    getBoard().moveForward(m);
                }
            }
        } else if (moveListIndex > index) {
            for (int j = moveListIndex; j > index; j--) {
                Move m = moveList.getModel().get(j);
                assert (getBoard().getBoardState().isWhiteToMove() != m.isWhiteMove());
                getBoard().moveBackward(m);
            }
        }
        getBoard().endUpdate();
        moveList.setSelectedIndex(index);
    }

    void goBackward() {
        gotoMove(moveList.getSelectedIndex() - 1, false);
    }

    void goBackward(int n) {
        gotoMove(Math.max(moveList.getSelectedIndex() - n, -1), false);
    }

    void goBegin() {
        gotoMove(-1, false);
    }

    void goEnd() {
        gotoMove(moveList.getModel().size() - 1, false);
    }

    void goForward() {
        gotoMove(moveList.getSelectedIndex() + 1, false);
    }

    void goForward(int n) {
        gotoMove(Math.min(moveList.getSelectedIndex() + n, moveList.getModel().size() - 1), false);
    }
    //</editor-fold>

}
