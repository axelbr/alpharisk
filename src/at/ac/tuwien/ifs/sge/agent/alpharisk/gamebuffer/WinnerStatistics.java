package at.ac.tuwien.ifs.sge.agent.alpharisk.gamebuffer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WinnerStatistics {
    long allGames;
    long winPlayerACount;
    long drawCount;
    long winPlayerBCount;
}
