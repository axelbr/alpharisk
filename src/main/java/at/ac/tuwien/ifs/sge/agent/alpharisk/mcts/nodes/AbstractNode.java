package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.Game;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public abstract class AbstractNode implements Node {

    private int plays, wins;
    private RiskAction action;
    private Risk state;
    private final Phase phase;

    public AbstractNode(final Risk game, Phase phase) {
        this.state = game;
        this.phase = phase;
    }

    public RiskAction getAction() {
        return action;
    }


    @Override
    public int getPlays() {
        return plays;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public void update(double value) {
        this.plays += 1;
        if (value > 0) {
            this.wins += 1;
        }
    }

    @Override
    public Game<RiskAction, ?> getGame() {
        return state;
    }

    @Override
    public void setGame(Game<RiskAction, ?> game) {
        this.state = (Risk) game;
    }
}
