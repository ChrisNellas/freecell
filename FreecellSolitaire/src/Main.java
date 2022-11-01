
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Main {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		long endTime = -1 ;
		String method = args[0];
		String problemFile = args[1];
		String solutionFileName = args[2];

		int numOfStack = 0; // has the stack's number
		int cardCounter = 0; 
		int totalCardSum = 0;

		HashMap<Integer, Stack<Card>> rootNodeStacks = new HashMap<Integer, Stack<Card>>();

		TreeNode rootNode;

		System.out.println("method: " + method);
		System.out.println("problem's file name: " + problemFile);
		System.out.println("solution's file name: " + solutionFileName);

		try {
			File f = new File(problemFile);
			FileReader freader = new FileReader(f);
			BufferedReader reader = new BufferedReader(freader);

			String line = reader.readLine();

			Stack<Card> stack = new Stack<Card>(); // keep temporarily each stack's cards

			while (line != null) {
				String[] subStr = line.split(" "); // split the string whenever it finds a space
				cardCounter += subStr.length;
				for (int i = 0; i < subStr.length; i++) {
					Card card = createCard(subStr[i]);
					stack.add(card);
					totalCardSum+= card.getValue()+1;  // keeps the total value of cards (from 1 - to N+1  for example if i have 4 cards for every tribe then it will add (1+2+3+4)*4 = 40
					
				}
				rootNodeStacks.put(numOfStack, (Stack<Card>) stack.clone());
				stack.clear();
				line = reader.readLine();
				numOfStack++;
			}
			reader.close();
			freader.close();
			f = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		rootNode = new TreeNode(rootNodeStacks, cardCounter, totalCardSum);
		rootNodeStacks = null; 

		
		// THE ALGORITHMS PART
		// The rootNode will not be checked if it is the solution 
		
		ArrayList<TreeNode> reversedSolutionPath = null ; // it will have all nodes that are part of solution path (solution node -> root node)
		
		LinkedList<TreeNode> frontier = new LinkedList<TreeNode>(); //keep the unsearched nodes
		
		frontier.addFirst(rootNode); // insert the root node to the first position 
		
		boolean solved = false;
		boolean timeout = false;
		 
		int k=0; // is used to print the node which will be under processing when timeout will occur
		int givenTime = 300000; // maximum amount of milliseconds the program has to find a solution
				
		
		switch (method) {
			case "breadth":
				
				while(!solved) {
					TreeNode currNode = frontier.poll(); // Retrieves and removes the head (first element) of this list
					System.gc(); //calls the garbage collector
					ArrayList<TreeNode> currNodeChildren = currNode.findChildren(); 	// find current's node children
					
					for(int i=0;i<currNodeChildren.size();i++) { 
						if(System.currentTimeMillis()-startTime<givenTime) {
							if(currNodeChildren.get(i).isSolution()) { // check for solution before insert the nodes into frontier
								reversedSolutionPath = getReversedSolutionPath(currNodeChildren.get(i));
								solved = true;
								endTime = System.currentTimeMillis();
								break;
							} else {
								frontier.addLast(currNodeChildren.get(i)); 
							}
						} else {
							timeout = true;
							k=i; 
							break;
						}
					}
					if(timeout) {
						
							//print the under process node before timeout occur
							System.out.println("DESCRIPTION: "+currNodeChildren.get(k).description);
							System.out.print("freecells: ");
							for(int j=0;j<currNodeChildren.get(k).freeCells.length;j++) {
								if(currNodeChildren.get(k).freeCells[j]!=null) {
									System.out.print(currNodeChildren.get(k).freeCells[j].getTribe()+""+currNodeChildren.get(k).freeCells[j].getValue()+" ");
								}
							}
							System.out.println();
							System.out.print("foundation: ");
							for(int j=0;j<currNodeChildren.get(k).foundations.length;j++) {
								if(currNodeChildren.get(k).foundations[j]!=null) {
									System.out.print(currNodeChildren.get(k).foundations[j].getTribe()+""+currNodeChildren.get(k).foundations[j].getValue()+" ");
								}
							}
							System.out.println();
							for(int j=0;j<currNodeChildren.get(k).stacks.size();j++) {
								for(int f=0;f<currNodeChildren.get(k).stacks.get(j).size();f++) {
									System.out.print(currNodeChildren.get(k).stacks.get(j).elementAt(f).getTribe()+""+currNodeChildren.get(k).stacks.get(j).elementAt(f).getValue()+" ");
								}
								System.out.println();
							}
							
							
							System.out.println();
						break;
					}
				}
				break;
			case "depth":
				
				while(!solved) {
					TreeNode currNode = frontier.poll(); // Retrieves and removes the head (first element) of this list
					if(currNode.isSolution()) { // check for solution before the expansion of the node
						reversedSolutionPath = getReversedSolutionPath(currNode);
						solved = true;
						endTime = System.currentTimeMillis();
						break;
					} 
					
					ArrayList<TreeNode> currNodeChildren = currNode.findChildren(); 	// find current's node children
					
					if(System.currentTimeMillis()-startTime<givenTime) {
						frontier.addAll(0, currNodeChildren);  // the children will be inserted at front   
					}else { // TIMEOUT
						System.out.println("DESCRIPTION: "+currNodeChildren.get(k).description);
						System.out.print("freecells: ");
						for(int j=0;j<currNodeChildren.get(k).freeCells.length;j++) {
							if(currNodeChildren.get(k).freeCells[j]!=null) {
								System.out.print(currNodeChildren.get(k).freeCells[j].getTribe()+""+currNodeChildren.get(k).freeCells[j].getValue()+" ");
							}
						}
						System.out.println();
						System.out.print("foundation: ");
						for(int j=0;j<currNodeChildren.get(k).foundations.length;j++) {
							if(currNodeChildren.get(k).foundations[j]!=null) {
								System.out.print(currNodeChildren.get(k).foundations[j].getTribe()+""+currNodeChildren.get(k).foundations[j].getValue()+" ");
							}
						}
						System.out.println();
						for(int j=0;j<currNodeChildren.get(k).stacks.size();j++) {
							for(int f=0;f<currNodeChildren.get(k).stacks.get(j).size();f++) {
								System.out.print(currNodeChildren.get(k).stacks.get(j).elementAt(f).getTribe()+""+currNodeChildren.get(k).stacks.get(j).elementAt(f).getValue()+" ");
							}
							System.out.println();
						}
						System.out.println();
						break;
					}	
				}
				break;
			case "best":
				
				while(!solved) {
					TreeNode currNode = frontier.poll(); // Retrieves and removes the head (first element) of this list
					if(currNode.isSolution()) { // check for solution before the expansion of the node
						reversedSolutionPath = getReversedSolutionPath(currNode);
						solved = true;
						endTime = System.currentTimeMillis();
						break;
					} 
					
					ArrayList<TreeNode> currNodeChildren = currNode.findChildren(); 	// find current's node children
					for(int i=0;i<currNodeChildren.size();i++) { 
						if(System.currentTimeMillis()-startTime<givenTime) {
							currNodeChildren.get(i).calculateCost(method);
														
							for(int j=0;j<frontier.size();j++) { //for every node in frontier , i compare the f with the f of current node's i Child 
								if(frontier.get(j).getF()>currNodeChildren.get(i).getF()) {
									frontier.add(j, currNodeChildren.get(i));
									break;
								}
							}
							if(frontier.size()==0) { // it used only when i want to insert the first child of the root into the frontier
								frontier.addFirst(currNodeChildren.get(i));
							}	
						}else {
							timeout = true;
							k=i;
							break;
						}
					}
					
					if(timeout) {
						
						//print the under process node before timeout occur
						System.out.println("DESCRIPTION: "+currNodeChildren.get(k).description);
						System.out.print("freecells: ");
						for(int j=0;j<currNodeChildren.get(k).freeCells.length;j++) {
							if(currNodeChildren.get(k).freeCells[j]!=null) {
								System.out.print(currNodeChildren.get(k).freeCells[j].getTribe()+""+currNodeChildren.get(k).freeCells[j].getValue()+" ");
							}
						}
						System.out.println();
						System.out.print("foundation: ");
						for(int j=0;j<currNodeChildren.get(k).foundations.length;j++) {
							if(currNodeChildren.get(k).foundations[j]!=null) {
								System.out.print(currNodeChildren.get(k).foundations[j].getTribe()+""+currNodeChildren.get(k).foundations[j].getValue()+" ");
							}
						}
						System.out.println();
						for(int j=0;j<currNodeChildren.get(k).stacks.size();j++) {
							for(int f=0;f<currNodeChildren.get(k).stacks.get(j).size();f++) {
								System.out.print(currNodeChildren.get(k).stacks.get(j).elementAt(f).getTribe()+""+currNodeChildren.get(k).stacks.get(j).elementAt(f).getValue()+" ");
							}
							System.out.println();
						}
						
						break;
					}
				
				}
				break;
			case "astar":
			
				while(!solved) {
					TreeNode currNode = frontier.poll(); // Retrieves and removes the head (first element) of this list
					if(currNode.isSolution()) { // check for solution before the expansion of the node
						reversedSolutionPath = getReversedSolutionPath(currNode);
						solved = true;
						endTime = System.currentTimeMillis();
						break;
					} 
					
					ArrayList<TreeNode> currNodeChildren = currNode.findChildren(); 	// find current's node children
					for(int i=0;i<currNodeChildren.size();i++) { 

						currNodeChildren.get(i).calculateCost(method); // calculate f of this node
						
						if(System.currentTimeMillis()-startTime<givenTime) {
							
							for(int j=0;j<frontier.size();j++) { //for every node in frontier , i compare the f with the f of currunt Node's i Child 
								if(frontier.get(j).getF()>currNodeChildren.get(i).getF()) {
									frontier.add(j, currNodeChildren.get(i));
									break;
								}else if(frontier.get(j).getF()==currNodeChildren.get(i).getF()&&currNodeChildren.get(i).getG()<=frontier.get(j).getG()) { // if the ratings of both nodes are the same  the node with the bigger g will be the last of them in frontier 
									frontier.add(j, currNodeChildren.get(i));
									break;
								}
							}
							if(frontier.size()==0) { // it used only when i want to insert the first child of the root into the frontier
								frontier.addFirst(currNodeChildren.get(i));
							}
						} else {
							timeout = true;
							k=i; 
							break;
						}	
					}
					if(timeout) {
						
						//print the under process node before timeout occur
						System.out.println("DESCRIPTION: "+currNodeChildren.get(k).description);
						System.out.print("freecells: ");
						for(int j=0;j<currNodeChildren.get(k).freeCells.length;j++) {
							if(currNodeChildren.get(k).freeCells[j]!=null) {
								System.out.print(currNodeChildren.get(k).freeCells[j].getTribe()+""+currNodeChildren.get(k).freeCells[j].getValue()+" ");
							}
						}
						System.out.println();
						System.out.print("foundation: ");
						for(int j=0;j<currNodeChildren.get(k).foundations.length;j++) {
							if(currNodeChildren.get(k).foundations[j]!=null) {
								System.out.print(currNodeChildren.get(k).foundations[j].getTribe()+""+currNodeChildren.get(k).foundations[j].getValue()+" ");
							}
						}
						System.out.println();
						for(int j=0;j<currNodeChildren.get(k).stacks.size();j++) {
							for(int f=0;f<currNodeChildren.get(k).stacks.get(j).size();f++) {
								System.out.print(currNodeChildren.get(k).stacks.get(j).elementAt(f).getTribe()+""+currNodeChildren.get(k).stacks.get(j).elementAt(f).getValue()+" ");
							}
							System.out.println();
						}
						break;
					}
				}
				break;
			default: 
				System.out.println("INVALID METHOD");	
		}
		
		// if the program found a solution
		if(reversedSolutionPath!=null) {
			
			try {
				File f = new File(solutionFileName);
				FileWriter writer = new FileWriter(f);

				writer.write(String.valueOf(reversedSolutionPath.size()));
				writer.write(System.lineSeparator());
				writer.write(System.lineSeparator());
				for(int i=reversedSolutionPath.size()-1;i>-1;i--) { // the elements of this arraylist will be writen from last to first
					writer.write(reversedSolutionPath.get(i).description);
					writer.write(System.lineSeparator());
				}
				System.out.println((endTime-startTime)/1000.0 +" seconds");

				
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} else {
			System.out.println("failed to find a solution");
		}
	}

	public static  Card createCard(String str) {
		char tr = str.charAt(0); // get the character
		int val = Integer.parseInt(str.substring(1)); // use of subString because the number may be greater than 9
		return new Card(tr, val);
	}
	
	public static ArrayList<TreeNode> getReversedSolutionPath(TreeNode solution) {
		
		ArrayList<TreeNode> solutionPathReversed = new ArrayList<TreeNode>();
		TreeNode cNode = solution;
		
		
		while(cNode.parent!=null) {
			solutionPathReversed.add(cNode);
			cNode = cNode.parent;
		}
		
		return solutionPathReversed;
	}	
}
