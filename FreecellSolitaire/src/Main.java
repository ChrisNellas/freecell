
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Main {

	public static void main(String[] args) {// String[] args String meth, String probFileName, String solFileName
		long startTime = System.currentTimeMillis();
		long endTime = -1 ;
		String method = args[0];
		String problemFile = args[1];
		String solutionFileName = args[2];

		int numOfStack = 0; // has the stack's number
		int cardCounter = 0; // Maybe useless


		/*Runtime rt = Runtime.getRuntime();
		
		System.out.println("PROCESSORS:"+rt.availableProcessors());
		System.out.println("MAX MEMORY: "+rt.maxMemory());*/
		HashMap<Integer, Stack<Card>> rootNodeStacks = new HashMap<Integer, Stack<Card>>();

		TreeNode rootNode;

		System.out.println("method: " + method);
		System.out.println("problem's file name: " + problemFile);
		System.out.println("solution's file name: " + solutionFileName);

		// Reading all lines from problemFile
		//String method = "depth"; //depth  breadth
		try {
			File f = new File(problemFile);
			FileReader freader = new FileReader(f);
			BufferedReader reader = new BufferedReader(freader);

			String line = reader.readLine();

			Stack<Card> stack = new Stack<Card>(); // keep temporarily each stack's cards

			while (line != null) {

				String[] subStr = line.split(" ");
				cardCounter += subStr.length;
				for (int i = 0; i < subStr.length; i++) {
					stack.add(createCard(subStr[i]));
				}
				rootNodeStacks.put(numOfStack, (Stack<Card>) stack.clone());
				// System.out.println(stack.size());
				stack.clear();
				// System.out.println("TIME OF TRUTH:
				// "+rootNodeStacks.get(numOfStack).get(4).getValue());
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

		/* DELETE IT. TO CHECK IF CONVERT RIGHT DIFFERENT TYPES FROM Object 
		Object obj1 = rootNodeStacks.get(2); //new Stack<Card>();
		System.out.println(obj1.getClass());

		Stack<Card> stack1 = (Stack<Card>) obj1;

		System.out.println(rootNodeStacks.get(2).size());
		System.out.println(stack1.size());
		
		for(int i=0;i<stack1.size();i++) {
			System.out.println(rootNodeStacks.get(2).get(i).getTribe()+""+rootNodeStacks.get(2).get(i).getValue()+""+stack1.get(i).getTribe()+""+stack1.get(i).getValue());
		}
		
		Card[] cards = new Card[4]; 
		
		cards[0] = new Card('S', 4);
		cards[1] = new Card('D', 2);
		cards[2] = new Card('C', 7);
		cards[3] = new Card('H', 12);
		Object obj2 = cards;
		
		System.out.println(obj2.getClass().toString().equals("class [LCard;"));
		Card[] ca = (Card[]) obj2;
		
		System.out.println(cards.length);
		System.out.println(ca.length);
		
		for(int i=0;i<ca.length;i++) {
			System.out.println(cards[i].getTribe()+""+cards[i].getValue()+""+ca[i].getTribe()+""+ca[i].getValue());
		}
		*/
		
		rootNode = new TreeNode(rootNodeStacks, cardCounter);
		rootNodeStacks = null; // I DON'T NEED THIS ANYMORE COULD CHANGE rootNode stacks values

		
		// THE ALGORITHMS PART
		// The rootNode will not be check if it is solution 
		
		ArrayList<TreeNode> reversedSolutionPath = null ; // it will have all nodes that are part of solution path (solution node -> root node)
		
		LinkedList<TreeNode> frontier = new LinkedList<TreeNode>(); //keep the unsearched kids-paths
		
		frontier.addFirst(rootNode);
		
		boolean solved = false;
		boolean timeout = false;
		
		//delete it 
		int k=0;
		int givenTime = 300000; // the time i want to find a solution
		
		/*
		ArrayList<TreeNode> currNodeChildren = rootNode.findChildren();
		System.out.println(frontier.size());
		for(int i=0;i<frontier.size();i++) {
			System.out.println(frontier.get(i));
		}
		
		frontier.addAll(0, currNodeChildren);
		System.out.println(frontier.size());
		for(int i=0;i<frontier.size();i++) {
			System.out.println(frontier.get(i));
		}
		*/
		
		
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
							k=i; //delete it
							break;
						}
					}
					if(timeout) {
						
							//delete it
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
						frontier.addAll(0, currNodeChildren);  // the children will be inserted at front     FOR ME(they will be reversed at the end the children)
					}else{
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
					//Scanner sc = new Scanner(System.in);
					for(int i=0;i<currNodeChildren.size();i++) { 
						if(System.currentTimeMillis()-startTime<givenTime) {
							currNodeChildren.get(i).calculateCost(method);
							/*System.out.println(currNodeChildren.get(i).g);
							System.out.println(currNodeChildren.get(i).h);
							System.out.println(currNodeChildren.get(i).f);
							sc.nextLine();
							*/
							for(int j=0;j<frontier.size();j++) { //for every node in frontier , i compare the f with the f of currunt Node's i Child 
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
							k=i; //delete it
							break;
						}
					}
					if(timeout) {
						
						//delete it
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
					Scanner sc = new Scanner(System.in);
					for(int i=0;i<currNodeChildren.size();i++) { 
						System.out.println("i change child");
						
						currNodeChildren.get(i).calculateCost(method);
						System.out.println(currNodeChildren.get(i).g);
						System.out.println(currNodeChildren.get(i).h);
						System.out.println(currNodeChildren.get(i).f);
						sc.nextLine();
						if(System.currentTimeMillis()-startTime<givenTime) {
							//currNodeChildren.get(i).calculateCost(method);
							
							for(int j=0;j<frontier.size();j++) { //for every node in frontier , i compare the f with the f of currunt Node's i Child 
								//System.out.println("under limitation");
								if(frontier.get(j).getF()>currNodeChildren.get(i).getF()) {
									frontier.add(j, currNodeChildren.get(i));
									break;
								}else if(frontier.get(j).getF()==currNodeChildren.get(i).getF()&&currNodeChildren.get(i).getG()>frontier.get(j).getG()) { // if both node's cost are the same  //if(currNodeChildren.get(i).getG()>frontier.get(j).getG()) { // the node with the bigger g will be placed first in frontier 
									frontier.add(j, currNodeChildren.get(i));
									break;
								
								}
							}
							if(frontier.size()==0) { // it used only when i want to insert the first child of the root into the frontier
								frontier.addFirst(currNodeChildren.get(i));
							}
						} else {
							System.out.println("end of time");
							timeout = true;
							k=i; //delete it
							break;
						}	
					}
					if(timeout) {
						
						//delete it
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
						
						//System.out.println("H0".equals(rootNode.stacks.get(7).get(0).getTribe()+""+rootNode.stacks.get(7).get(0).getValue()));
						System.out.println();
						System.out.println("f:"+ currNodeChildren.get(k).f);
						System.out.println("g:"+ currNodeChildren.get(k).g);
						System.out.println("h:"+ currNodeChildren.get(k).h);
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
				// write the in the file
				writer.write(String.valueOf(reversedSolutionPath.size()));
				writer.write(System.lineSeparator());
				writer.write(System.lineSeparator());
				for(int i=reversedSolutionPath.size()-1;i>-1;i--) { //
					writer.write(reversedSolutionPath.get(i).description);
					writer.write(System.lineSeparator());
					//System.out.println(reversedSolutionPath.get(i).description);
				}
				//DELETE IT
				//writer.write((endTime-startTime)/1000.0 +" seconds");
				System.out.println((endTime-startTime)/1000.0 +" seconds");

				
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} else {
			System.out.println("failed to find a solution");
		}
		
		
		
		/*
		 // To see the children of a node
		ArrayList<TreeNode> children = rootNode.findChildren(); 	// try to find current's node children
		
		for(int i=0;i<children.size();i++) {
			
			System.out.println("child:"+i);
			System.out.println("DESCRIPTION: "+children.get(i).getDescription());
			System.out.print("freecells: ");
			for(int j=0;j<children.get(i).freeCells.length;j++) {
				if(children.get(i).freeCells[j]!=null) {
					System.out.print(children.get(i).freeCells[j].getTribe()+""+children.get(i).freeCells[j].getValue()+" ");
				}
			}
			System.out.println();
			System.out.print("foundation: ");
			for(int j=0;j<children.get(i).foundations.length;j++) {
				if(children.get(i).foundations[j]!=null) {
					System.out.print(children.get(i).foundations[j].getTribe()+""+children.get(i).foundations[j].getValue()+" ");
				}
			}
			System.out.println();
			for(int j=0;j<children.get(i).stacks.size();j++) {
				for(int k=0;k<children.get(i).stacks.get(j).size();k++) {
					System.out.print(children.get(i).stacks.get(j).elementAt(k).getTribe()+""+children.get(i).stacks.get(j).elementAt(k).getValue()+" ");
				}
				System.out.println();
			}
		}
		
	*/
		
		//+children.get(i).stacks.get(j).elementAt(k).getValue()
		/*
		try {
			File f = new File(solutionFileName);
			FileWriter writer = new FileWriter(f);
			// write the in the file

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	*/
		

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
