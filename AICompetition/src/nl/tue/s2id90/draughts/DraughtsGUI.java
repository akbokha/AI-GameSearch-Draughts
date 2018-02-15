package nl.tue.s2id90.draughts;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nl.tue.s2id90.contest.GameGUI;
import nl.tue.s2id90.contest.GameGuiListener;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import nl.tue.s2id90.game.Game;
import nl.tue.s2id90.game.Player;
import org10x10.dam.game.BoardState;
import org10x10.dam.game.Move;
import org10x10.dam.game.MoveBoardListener;
import org10x10.dam.game.MoveSelector;
import org10x10.dam.game.MoveSelectorAdvanced;
import org10x10.dam.game.SetupListener;
import org10x10.dam.ui.Board;
import org10x10.dam.ui.DefaultFieldDecorator;
import org10x10.dam.ui.LastMoveListener;
import org10x10.dam.ui.MoveListener;
import org10x10.dam.ui.swing.SwingBoardPanel;
import org10x10.dam.ui.swing.movelist.JMoveList;
import org10x10.dam.ui.swing.movelist.MoveListModel;

/**
 *
 * @author huub
 */
public class DraughtsGUI 
    implements GameGUI<DraughtsState,DraughtsPlayer,Move>
{

    private SwingBoardPanel boardPanel;
    private JMoveList moveList;
    private JLabel numberOfPiecesLabel;
    private JPanel settings;
    JCheckBox allowEditingCheckBox;
    JCheckBox beginStateCheckBox;
    SetupListener setUpListener;
    JButton swapButton;
    private MoveListModel moves = new MoveListModel(new ArrayList<>());
    private MoveBoardListener moveBoardListener;
    private DraughtsState ds;
    MoveListManager mlm;
    private Game currentGame;

    public DraughtsGUI() {
        this.listeners = new ArrayList<>();
    }

    @Override
    public SwingBoardPanel getBoardPanel() {
        if (boardPanel == null) {
            boardPanel = new SwingBoardPanel();
            boardPanel.setScalable(true);
            boardPanel.setPreferredSize(new Dimension(400, 400));
            Board board = boardPanel.getBoard();
            MoveSelector ms = new MoveSelectorAdvanced(board.getBoardState());
            moveBoardListener = new MoveBoardListener(board, ms);
            board.addBoardListener(moveBoardListener);
            moveBoardListener.setEnabled(false);
            board.addMoveListener(new LastMoveListener(new DefaultFieldDecorator()));
        }
        return boardPanel;
    }

    @Override
    public List<JComponent> getPanels() {
        if (moveList == null) {
            moveList = new JMoveList();
            moveList.setModel (moves);
            moveList.setName("moves"); // name as used in tabbedPane
            
            mlm = new MoveListManager(moveList, boardPanel.getBoard()) {

                @Override
                public void gotoMove(int index, boolean animateMoves) {
                    super.gotoMove(index, animateMoves); 
                    reset(null,board.getBoardState(),false); //notify rest of the world that bs changed
                }

                @Override
                protected void setBoardState(BoardState bs, boolean b) {
                    super.setBoardState(bs, b);
                    reset(null,board.getBoardState(),false); //notify rest of the world that bs changed
                }
                
                
            };
            
            numberOfPiecesLabel=new JLabel("-");
            numberOfPiecesLabel.setHorizontalAlignment(JLabel.CENTER);
            numberOfPiecesLabel.setName("progress");
            
            settings = new JPanel();
            settings.setName("options");
            beginStateCheckBox = new JCheckBox("start in begin state");
            beginStateCheckBox.setSelected(true);
            settings.add(beginStateCheckBox);
            
            allowEditingCheckBox = new JCheckBox("allow editing of boardState");
            allowEditingCheckBox.setSelected(false);
            setUpListener = new SetupListener(boardPanel.getBoard());
            boardPanel.getBoard().addBoardListener(setUpListener);
            allowEditingCheckBox.addActionListener((ActionEvent e) -> {
                setUpListener.setEnabled(allowEditingCheckBox.isSelected());
            });
            
            settings.add(allowEditingCheckBox);
            
            swapButton = new JButton("swap starting player");
            swapButton.addActionListener((ActionEvent e) -> {
                Board b = boardPanel.getBoard();
                BoardState bs = b.getBoardState();
                b.startUpdate();
                bs.switchPlayer();
                b.endUpdate();
                reset(currentGame,bs,true);
            });
            settings.add(swapButton);
            
            JCheckBox flipBoardCheckBox = new JCheckBox("flip board");
            flipBoardCheckBox.setSelected(false);
            flipBoardCheckBox.addActionListener(e-> {
                boolean flipped = flipBoardCheckBox.isSelected();
                Board b = boardPanel.getBoard();
                b.setSwitchSides(flipped);
                boardPanel.repaint();
            });
            settings.add(flipBoardCheckBox);
        }
        List<JComponent> panelList = new ArrayList<>();
        panelList.add(moveList);
        panelList.add(numberOfPiecesLabel);
        panelList.add(settings);
        return panelList;
    }

    @Override
    public void show(DraughtsState gs) {
        Board board = boardPanel.getBoard();
        BoardState bs = convert(gs, board.getBoardState());
        board.startUpdate();
        board.setBoardState(bs);
        board.endUpdate();
        
        updatePieceCount(gs);
        if (moves.size()>0) {
            moveList.setSelectedIndex(moves.size() - 1);
            moveList.repaint();
        }     
    }
    
    private void setOptionsEnabled(boolean enabled) {        
        if (beginStateCheckBox!=null) {
            beginStateCheckBox.setEnabled(enabled);
            allowEditingCheckBox.setEnabled(enabled); 
            allowEditingCheckBox.setSelected(false); // prevents accidental editing
            swapButton.setEnabled(enabled);
            setUpListener.setEnabled(enabled&&allowEditingCheckBox.isSelected());
            moveList.setEnabled(enabled);
        }
    }
    
    static private BoardState convert(DraughtsState ds, BoardState target) {
        BoardState bs = target==null?new BoardState(10,10) : target;
        bs.setPieces(ds.getPieces());
        bs.setWhiteToMove(ds.isWhiteToMove());
        return bs;
    }
    
    private void updatePieceCount(DraughtsState gs) {
        int[] pieces = gs.getPieces();
        
        int whites=0, blacks=0;
        for(int f=1; f<pieces.length; f=f+1) {
            int piece = pieces[f];
            if (Draughts.isWhite(piece)) whites++;
            else if (Draughts.isBlack(piece)) blacks++;
        }
        String status = ""+whites + " - " + blacks;
        numberOfPiecesLabel.setText(status);
    }

    @Override
    public DraughtsState getCurrentGameState() {
        return ds;
    }

    private DraughtsState getDraughtsState(BoardState bs) {
        return new DraughtsState(bs) {
            @Override
            public void doMove(Move m) { // automatically update movelist when
                // gamestate is updated
                super.doMove(m);
                moves.add(m);
            }
            
            @Override
            public void reset() { // reset movelist when gamestate is updated
                super.reset();
                moves.clear();
            }
        };
    }
    
    private void reset(Game game) {
        BoardState bs = new BoardState(10,10);
        if (beginStateCheckBox.isSelected()) {
            bs = new BoardState(10,10);
            bs.setBegin();
        } 
        else {
            bs = (BoardState) boardPanel.getBoard().getBoardState(); // we need to clone it, don't no why!
        }
        reset(game, bs,true);
    }
    
    private void reset(Game game, BoardState bs, boolean clearMoves) {
        ds = getDraughtsState(bs); 
        if (clearMoves) {
            moveList.getModel().clear();
            moveList.setModel(moves=new MoveListModel((BoardState) bs.clone(), new ArrayList<>()));
        }
        updatePieceCount(ds);
        notifyGameGuiListeners(ds); // notify that game state changed
        if (game!=null) {
            setHumanToMove(game); // only necessary after start of game
        }
    }

//    @Override
//    /** enables one human move and calls the listener when that move has been
//     * played.
//     */
//    public void enableASingleHumanMove(final HumanMoveListener<Move> hml) {
//        if (hml!=null) {
//            moveBoardListener.setEnabled(true);
//            Board board = boardPanel.getBoard();
//
//            /**
//             * add move listener that provides a callback
//             * after a human made a move on the board and then
//             * also de-registers itself.
//             */
//            final MoveListener ml = new MoveListener() {
//                @Override
//                public void onMoveForward(Board b, Move m) {
//                    b.removeMoveListener(this); // note: this==ml
//                    moveBoardListener.setEnabled(false);
//                    hml.onMove(m);    // callback
//                    notifyGameGuiListeners(m); // notify that a human move has been done
//                }
//
//                @Override
//                public void onMoveBackward(Board b, Move m) {  }
//
//                @Override
//                public boolean isEnabled() { return true; }
//            };
//            
//            board.addMoveListener(ml);
//
//        }
//    }

    private void animateMove(Move m) {
        final int ANIMATION_TIME=400; // milliseconds
        DraughtsState gameState = getCurrentGameState();
        getBoardPanel().getBoard().animateMoveForward(m, ANIMATION_TIME);
        gameState.doMove(m);
        
        notifyGameGuiListeners(gameState); // notify that something changed
    }
    
    private boolean isGameGoingOn() {
        return currentGame!=null;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="game gui listener stuff">
    private final List<GameGuiListener<DraughtsState,Move>> listeners;
    
    public void add(GameGuiListener l) {
        listeners.add(l);
    }
    
    public void remove(GameGuiListener l) {
        listeners.remove(l);
    }
    
    private void notifyGameGuiListeners(Move m) {
        for(GameGuiListener<DraughtsState,Move> l : listeners) {
            l.onHumanMove(m);
        }
    }
    
    private void notifyGameGuiListeners(DraughtsState ds) {
        for(GameGuiListener<DraughtsState,Move> l : listeners) {
            l.onNewGameState(ds);
        }
    }

//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CompetitionListener methods">
    @Override
    public void onAIMove(Move m) {
        animateMove(m);
        setHumanToMove(currentGame);
    }
    
    @Override
    public  void onStopGame(Game g) {
        currentGame=null;
        setOptionsEnabled(true);
        setHumanMovesEnabled(false);
    }
    
    @Override
    public  void onStartGame(Game g) {
        currentGame=g;
        reset(currentGame);
        setOptionsEnabled(false);
    }
//</editor-fold>
    
    private void setHumanToMove(Game game) {
        if (game!=null) {
            boolean w2m = ds.isWhiteToMove();
            Player p = w2m? game.first: currentGame.second;
            setHumanMovesEnabled(p.isHuman());
        }
    }
    
    MoveListener ml = new MoveListener() {
        @Override
        public void onMoveForward(Board board, Move move) {
            // after a move disable another human move
            setHumanMovesEnabled(false);
            //animateMove(move);
            
            DraughtsState gameState = getCurrentGameState();
            gameState.doMove(move);
            
            notifyGameGuiListeners(move); // notify that a human move has been done
        }

        @Override
        public void onMoveBackward(Board board, Move move) {
            
        }

        @Override
        public boolean isEnabled() {
            return true;
        }        
    };
            
    private void setHumanMovesEnabled(boolean enable) {
        Board board = boardPanel.getBoard(); 
        moveBoardListener.setEnabled(enable);
        if (enable) {         
            board.addMoveListener(ml);
        } else {
            board.removeMoveListener(ml);
        }
    }
}   
