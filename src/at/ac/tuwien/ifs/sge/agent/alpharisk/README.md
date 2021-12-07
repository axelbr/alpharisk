# AlphaRisk

## Overview

This project is a basic DQN agent capable of playing the Risk board game.

It integrates [Deep Java Library (DJL)](https://github.com/awslabs/djl) to uses DQN to train an agent. The pretrained model are trained with 3M steps on a single GPU.


## Deep Q-Network Algorithm

The pseudo-code for the Deep Q Learning algorithm, as given in [Human-level Control through Deep Reinforcement Learning. Nature](https://www.nature.com/articles/nature14236), can be found below:
```
Initialize replay memory D to size N
Initialize action-value function Q with random weights
for episode = 1, M do
    Initialize state s_1
    for t = 1, T do
        With probability ϵ select random action a_t
        otherwise select a_t=max_a  Q(s_t,a; θ_i)
        Execute action a_t in emulator and observe r_t and s_(t+1)
        Store transition (s_t,a_t,r_t,s_(t+1)) in D
        Sample a minibatch of transitions (s_j,a_j,r_j,s_(j+1)) from D
        Set y_j:=
            r_j for terminal s_(j+1)
            r_j+γ*max_(a^' )  Q(s_(j+1),a'; θ_i) for non-terminal s_(j+1)
        Perform a gradient step on (y_j-Q(s_j,a_j; θ_i))^2 with respect to θ
    end for
end for
```


This work is based on the following repo:

 [kingyuluk/RL-FlappyBird](https://github.com/kingyuluk/RL-FlappyBird)

## License
[MIT](License) © Kingyu Luk
