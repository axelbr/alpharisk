package at.ac.tuwien.ifs.sge.agent.alpharisk.agent.gamebuffer;

import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;
import at.ac.tuwien.ifs.sge.agent.alpharisk.environment.EnvironmentZeroSumBase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.environment.OneOfTwoPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class ZeroSumGame extends Game{
    public ZeroSumGame(@NotNull MuZeroConfig config) {
        super(config);
    }
    public ZeroSumGame(@NotNull MuZeroConfig config, GameDTO gameDTO) {
        super(config, gameDTO);
    }
    public Optional<OneOfTwoPlayer> whoWonTheGame() {
        if (this.getEnvironment().hasPlayerWon(OneOfTwoPlayer.PlayerA)) return Optional.of(OneOfTwoPlayer.PlayerA);
        if (this.getEnvironment().hasPlayerWon(OneOfTwoPlayer.PlayerB)) return Optional.of(OneOfTwoPlayer.PlayerB);
        return Optional.empty();
    }

    public boolean hasPositiveOutcomeFor(OneOfTwoPlayer player) {
         EnvironmentZeroSumBase env =  this.getEnvironment();
        // won or draw but not lost
        return !env.hasPlayerWon(OneOfTwoPlayer.otherPlayer(player));
    }

    public EnvironmentZeroSumBase getEnvironment() {
        if (!(environment instanceof EnvironmentZeroSumBase)) {
            throw new RuntimeException("Environment is expected to be of type EnvironmentZeroSumBase");
        }
        return (EnvironmentZeroSumBase)environment;
    }
}
