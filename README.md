# AutoSnek

This is snake, written in Java. 

This is primarily a way for me to try writing pathfinding algorithms. You can play the game normally with the arrow keys, or let it play itself by toggling on a pathfinding algorithm with one of the following keys:

| Key | Pathfinding |
| --- | ----------- |
| D | **D**epth First Search (DFS) |
| B | **B**readth First Search (BFS) |
| M | Breadth First Search (BFS), but ignoring segments of tail that would have moved on by the time the head of the snake reaches them (worked out using [**M**anhattan](https://en.wiktionary.org/wiki/Manhattan_distance)/taxicab distance) |

TODO:
* Djkstra (as this is an undirected graph, this will end up being equivalent to BFS)
* A*

Other options include:

| Key  | Option |
| ---- | ------ |
| F | **F**ast mode |
| C | **C**olourful mode - new food will be randomly coloured, and will be added to the end of the snake's tail when eaten |
