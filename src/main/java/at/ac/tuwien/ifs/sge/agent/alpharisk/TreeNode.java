package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.game.Game;
import at.ac.tuwien.ifs.sge.util.node.GameNode;

import java.util.Objects;

public class TreeNode<A> implements GameNode<A>
{
    private Game<A, ?> game;
    private int wins;
    private int plays;
    
    public TreeNode() {
        this(null);
    }
    
    public TreeNode(final Game<A, ?> game) {
        this(game, 0, 0);
    }
    
    public TreeNode(final Game<A, ?> game, final A action) {
        this(game.doAction(action));
    }
    
    public TreeNode(final Game<A, ?> game, final int wins, final int plays) {
        this.game = game;
        this.wins = wins;
        this.plays = plays;
    }
    
    public Game<A, ?> getGame() {
        return this.game;
    }
    
    public void setGame(final Game<A, ?> game) {
        this.game = game;
    }
    
    public int getWins() {
        return this.wins;
    }
    
    public void setWins(final int wins) {
        this.wins = wins;
    }
    
    public void incWins() {
        ++this.wins;
    }
    
    public int getPlays() {
        return this.plays;
    }
    
    public void setPlays(final int plays) {
        this.plays = plays;
    }
    
    public void incPlays() {
        ++this.plays;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TreeNode<?> treeNode = (TreeNode<?>)o;
        return this.wins == treeNode.wins && this.plays == treeNode.plays && this.game.equals(treeNode.game);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.game, this.wins, this.plays);
    }
}
