# alpharisk

GameAgent for [SGE Risk](https://gitlab.com/StrategyGameEngine/sge-risk). Agent currently not functional!

This game agent is based on the muzero java implementation found on Github: https://github.com/enpasos/muzero

Using a Deep Learning approach like muzero requires finding solutions to the following challenges:

1. Observation space <br>
  (territory occupants & troop counts)
2. Network structure
3. Determine mapping of output to action space <br>
(Take the action spaces for each phase and concatenate all of them, then mask before selecting the best action for each phase)

The feasibility of this approach has yet to be determined.

## References

Schrittwieser, Julian, Ioannis Antonoglou, Thomas Hubert, Karen Simonyan, Laurent Sifre, Simon Schmitt, Arthur Guez, et al. 2020. ?Mastering Atari, Go, Chess and Shogi by Planning with a Learned Model.? Nature 588 (7839): 604?9. https://doi.org/10.1038/s41586-020-03051-4.
