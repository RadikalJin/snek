# AutoSnek

This is snake, written in Java. 

This is primarily a way for me to try out writing pathfinding algorithms. You can play the game normally, or toggle on the following algorithms by pressing the corresponding key:

| Key | Pathfinding |
| --- | ----------- |
| D | Depth First Search (DFS) |
| B | Breadth First Search (BFS) |
| M | Breadth First Search (BFS), but ignoring segments of tail that would have moved on by the time the head of the snake reaches them |

TODO: Djkstra (as this is an undirected graph, this will end up being equivalent to BFS)
TODO: A*

Other options include:

| Key  | Option |
| ---- | ------ |
| F | Fast mode |
| C | Colourful mode - new food will be randomly coloured, and will be added to the end of the snake's tail when eaten |
