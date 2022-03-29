This project was implemented as a homework for the AI course that my university has in 5th semester. 

This project solves freecell solitaire's problems with different number of cards.

It takes the problem from the given file and create the root node. After that it runs the specified by the user algorithm to find the solution. If the program can not find a solution in less than 5 minutes then it returns a failure message and stop the search for solution. If the program find a solution then returns the passed time and writes the second given file.  

It is called from the cmd with the following pattern: java Main <algorithm> <file1> <file2>
<algorithm>: 
           1) breadth :breadth first search
           2) depth :depth first search
           3) best :best first search
           4) astar :A* first search
  <file1>: the file that has the solitaire problem with the same format as the testSet#_#.txt 
  <file2>: the file that you want the solution to be saved.
  
