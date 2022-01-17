# AlphaRisk

In this work we designed a modular algorithm based on classic Upper Confidence Tree Search which is enhanced by domain specific heuristics. 


## Details

### **Search Algorithm**

We experimented with various modifications of standard MCTS, as presented in [1] and [2]. Finally, we ended up with two approaches: [Heuristic UCT Search](https://github.com/axelbr/alpharisk/blob/423605904f48e45419186287c8049b1f17fc06c5/src/main/java/at/ac/tuwien/ifs/sge/agent/alpharisk/mcts/algorithms/HeuristicUCTSearch.java) and [RAVE](https://github.com/axelbr/alpharisk/blob/423605904f48e45419186287c8049b1f17fc06c5/src/main/java/at/ac/tuwien/ifs/sge/agent/alpharisk/mcts/algorithms/rave/RapidActionValueEstimationSearch.java) [3].

For both algorithms, we biased the tree policy by injecting domain kowledge via heuristics. The heuristic gives an estimate of the node value and is added to the standard UCT score. Similar to the exploration term, the heuristic value decays (linearly) with the number of visits to the node.
We experimented with multiple heuristics:
- Territory Ratio: ratio of territories that are occupied by the player.
- Border Security Threat Ratio [5]: gives an estimate which territories have a high risk of being occupied.
- Bonus Ratio: relative share of reinforcements that are obtained in the next turn.

### Action Pruning
In order to reduce the branching factor, we heavily restrict the number of possible actions via action pruning strategies, such as those described in [4].
- Reinforce Phase: Only territories with neighbouring enemy territories may be reinforced. Reinforcements are split among the candidates via a denomination function to reduce the number of possibilites (currently, all reinforcements are placed on a single territory).
- Attack Phase: Only attacks with full force are allowed. Furthermore, only territories with less troops than the attacking territory may be attacked.
- Occupy Phase: Territories can be occupied by either 1 unit, 3 units or all mobile units.
- Fortify Phase: Only territories with neighbouring territories can be fortified.

## Literature
### Monte Carlo Tree Search
[1] [A Survey of Monte Carlo Tree Search Methods](http://www.incompleteideas.net/609%20dropbox/other%20readings%20and%20resources/MCTS-survey.pdf)

[2] [Monte Carlo Tree Search: A Review of Recent Modifications and Applications](https://arxiv.org/abs/2103.04931)

[3] [Monte-Carlo Tree Search and Rapid Action Value Estimation in Computer Go](https://www.cs.utexas.edu/~pstone/Courses/394Rspring13/resources/mcrave.pdf)

### Risk Heuristics
[4] [Monte Carlo Tree Search for Risk](https://www.sto.nato.int/publications/STO%20Meeting%20Proceedings/STO-MP-SAS-OCS-ORA-2020/MP-SAS-OCS-ORA-2020-WCM-01.pdf)

[5] [Evaluating Heuristics in the Game Risk
An Aritifical Intelligence Perspective](https://project.dke.maastrichtuniversity.nl/games/files/bsc/Hahn_Bsc-paper.pdf)

[6] [An Automated Technique for Drafting Territories in the Board Game Risk](https://www.researchgate.net/publication/220978458_An_Automated_Technique_for_Drafting_Territories_in_the_Board_Game_Risk)
