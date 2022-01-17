# AlphaRisk

UCT Search with "bonus ratio heuristic" and Action Space pruning



We changed our approach from muzero to this Heuristic UCT approach due to scarcity of time.

## Literature
### Monte Carlo Tree Search
- Survey from 2012: [A Survey of Monte Carlo Tree Search Methods](http://www.incompleteideas.net/609%20dropbox/other%20readings%20and%20resources/MCTS-survey.pdf)
- A More recent survey: [Monte Carlo Tree Search: A Review of Recent Modifications and Applications](https://arxiv.org/abs/2103.04931)
### Risk Heuristics
- Risk action pruning approaches: [Monte Carlo Tree Search for Risk](https://www.sto.nato.int/publications/STO%20Meeting%20Proceedings/STO-MP-SAS-OCS-ORA-2020/MP-SAS-OCS-ORA-2020-WCM-01.pdf)
- Various Heuristics: [Evaluating Heuristics in the Game Risk
An Aritifical Intelligence Perspective](https://project.dke.maastrichtuniversity.nl/games/files/bsc/Hahn_Bsc-paper.pdf)
- Initial Select Heuristic: [An Automated Technique for Drafting Territories in the Board Game Risk](https://www.researchgate.net/publication/220978458_An_Automated_Technique_for_Drafting_Territories_in_the_Board_Game_Risk)


## Artifacts

### Algorithms
- RAVE 
- UCT

### utility functions
- -bon     bonusRatioHeuristic
- -MCbon   Math.random() < bonusRatioHeuristic
- -ter     territoryRatioHeuristic
- -MCter   Math.random() < sample(territoryRatioHeuristic)
