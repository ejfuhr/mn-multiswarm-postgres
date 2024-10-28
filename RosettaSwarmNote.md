
from 
[Particle swarm optimization](https://rosettacode.org/wiki/Particle_swarm_optimization)
<!-- TOC -->
return to [README](READdME.md)


- Particle Swarm Optimization (PSO) is an optimization method in which multiple candidate solutions
('particles') migrate through the solution space under the influence of local and global best known
positions. PSO does not require that the objective function be differentiable and can optimize over
very large problem spaces, but is not guaranteed to converge. The method should be demonstrated by
application of the functions recommended below, and possibly other standard or well-known optimization
test cases.

- The goal of parameter selection is to ensure that the global minimum is discriminated from any
local minima, and that the minimum is accurately determined, and that convergence is achieved
with acceptable resource usage. To provide a common basis for comparing implementations,
the following test cases are recommended:

- McCormick function - bowl-shaped, with a single minimum
function parameters and bounds (recommended):
<br>-1.5 < x1 < 4
  <br>-3 < x2 < 4
  <br>- search parameters (suggested):
  <br>omega = 0
  <br>phi p = 0.6
  <br>phi g = 0.3
  <br>number of particles = 100
  <br>number of iterations = 40
</br>
- Michalewicz function - steep ridges and valleys, with multiple minima
function parameters and bounds (recommended):
  <br>0 < x1 < pi
  <br>0 < x2 < pi
- search parameters (suggested):
  <br>omega = 0.3
  <br>phi p = 0.3
  <br>phi g = 0.3
  <br>number of particles = 1000
  <br>number of iterations = 30
  <br>References:
[Particle Swarm Optimization[1]]
[Virtual Library of Optimization Test Functions[2]]
