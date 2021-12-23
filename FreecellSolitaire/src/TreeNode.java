import java.util.*;

public class TreeNode implements Cloneable {

	Card[] freeCells;
	Card[] foundations; // i keep the last card in every foundation
	HashMap<Integer, Stack<Card>> stacks;
	TreeNode parent;
	int g, h, f;
	String description; //it will have the movement which led to this node 
	int outOfFoundation;
	// Stack<String> r = new Stack<String>(); // gia testing

	
	// CONSTRUCTORS
	public TreeNode(TreeNode par, String descr, int step, int oOF) { // is used only when i create the children of a node
		freeCells = new Card[4];
		foundations = new Card[4];
		stacks = new HashMap<Integer, Stack<Card>>();
		parent = par;
		description = descr;
		g = step+1;
		outOfFoundation = oOF;
	}

	public TreeNode(HashMap<Integer, Stack<Card>> s, int amountOfCards) {
		stacks = s;
		parent = null;
		freeCells = new Card[4];
		foundations = new Card[4];
		description = "root";
		outOfFoundation = amountOfCards;
		g = 0;
		for(int i=0;i<foundations.length;i++) {
			foundations[i]=null;
			freeCells[i]=null;
		}
	}	
	public String getDescription() {
		return description;
	}
	
	public int getF() {
		return f;
	}
	
	public int getG() {
		return g;
	}
	
	
	public ArrayList<TreeNode> findChildren() { // ITS WORKING FOR N=8
		ArrayList<TreeNode> childTrNodeList = new ArrayList<TreeNode>(); //keeps the child-nodes
		
		
		//check for children's node with a move from a stack
		for(int i=0;i<stacks.size();i++) {// for every stack of this treeNode 
			if(!(stacks.get(i).isEmpty())) {
				Card lastCard = stacks.get(i).lastElement();
				
				for(int founIndex=0;founIndex<foundations.length;founIndex++) { // Check for children with a move to foundations
					
					//if there is no card in foundation position and the under processing card is 0 
					//movement from stack to foundation
					if((foundations[founIndex]==null&&lastCard.getValue()==0)) {
			
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						pottenChild.moveToArray(lastCard, pottenChild.foundations, founIndex);
						pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
						//pottenChild.outOfFoundation = this.outOfFoundation-1;
						
						//pottenChild.makeAMove(pottenChild.stacks.get(i), pottenChild.foundations, lastCard);
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
						
						break; // i don't want to insert to another foundation slot the same card
						
					 //if the specific card and the current foundation slot has the same tribe and the card has bigger value by 1
					} else if(foundations[founIndex]!=null&&foundations[founIndex].getTribe()==lastCard.getTribe() && foundations[founIndex].getValue()+1==lastCard.getValue()) { 
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						pottenChild.moveToArray(lastCard, pottenChild.foundations, founIndex);
						pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
						//pottenChild.outOfFoundation = this.outOfFoundation-1;
						
						
						//pottenChild.makeAMove(pottenChild.stacks.get(i), pottenChild.foundations, lastCard);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
					}
				}
				
				
				//checks for child by moving a card to another stack
				//movement from stack to stack
				for(int j=0;j<stacks.size();j++) {
					if(j!=i) { //if the two pointers does not refer to the same stack
						if(stacks.get(j).isEmpty()) {
							if(stacks.get(i).size()!=1) { // if the current stack of the moving card has only this card, i don't want to move to an empty stack because will occur the same treenode (change the position of an empty stack)
								TreeNode pottenChild = this.createIdenticalTreeNode("newstack "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation); //create a copy of this treeNode
								
								pottenChild.moveToStack(lastCard, pottenChild.stacks.get(j));
								pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
								
								//pottenChild.makeAMove(pottenChild.stacks.get(i), pottenChild.stacks.get(j), lastCard);
								
								if(!pottenChild.alreadyExists()) {
									childTrNodeList.add(pottenChild);
								}
							}
						} else { // if the the pointed by j stack is not empty
							Card destLastCard =stacks.get(j).lastElement();
							if(lastCard.canMove(destLastCard)) {
								TreeNode pottenChild = this.createIdenticalTreeNode("stack "+lastCard.getTribe()+""+lastCard.getValue()+" "+destLastCard.getTribe()+""+destLastCard.getValue(), outOfFoundation); //create a copy of this treeNode
								
								pottenChild.moveToStack(lastCard, pottenChild.stacks.get(j));
								pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
								
								
								//pottenChild.makeAMove(pottenChild.stacks.get(i), pottenChild.stacks.get(j), lastCard);
								
								if(!pottenChild.alreadyExists()) {
									childTrNodeList.add(pottenChild);
								}
							}
						}
					}
				}
				
				// movement from stack to freecell
				for(int k=0;k<freeCells.length;k++) {
					if(freeCells[k]==null) {
						TreeNode pottenChild = this.createIdenticalTreeNode("freecell "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation); //create a copy of this treeNode
						//DELETE IT
						/*System.out.println("PARENT"+pottenChild.parent);
						for (int t=0;t<pottenChild.parent.stacks.size();t++) {
							for(int y=0;y<pottenChild.parent.stacks.get(t).size();y++) {
								System.out.print(pottenChild.parent.stacks.get(t).get(y).getTribe()+""+pottenChild.parent.stacks.get(t).get(y).getValue()+" ");
							}
							System.out.println();
						}*/
						//delete it
						//if(pottenChild.alreadyExists()) {
						//	System.out.println("is same with the parent  BEFORE");
						//}
						
						pottenChild.moveToArray(lastCard, pottenChild.freeCells, k);
						pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
						
						//pottenChild.makeAMove(pottenChild.stacks.get(i), pottenChild.freeCells, lastCard);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						} 
						break; // if i find 1 free freeCell i don't want to check the remaining freecells position
					}
				}
				
			}
		} // end of search for children from stack movement
		
		
		
		for(int i=0;i<freeCells.length;i++) { // search for children from freeCell card movement
			if(freeCells[i]!=null) {
		
				//movement from freecell to foundation
				for(int founIndex=0;founIndex<foundations.length;founIndex++) { // Check for childs with a move to foundations
					//if there is no card and the under processing card is 0 
					//or if the specific card and the current foundation slot has the same tribe and the card has bigger value by 1
					//DOES IT WORKS????
					//movement from stack to foundation
					if((foundations[founIndex]==null&&freeCells[i].getValue()==0)) {
			
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+freeCells[i].getTribe()+""+freeCells[i].getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						pottenChild.moveToArray(freeCells[i], pottenChild.foundations, founIndex);
						pottenChild.moveFromArray(pottenChild.freeCells, i);
						//pottenChild.outOfFoundation = this.outOfFoundation-1;
						
						//pottenChild.makeAMove(pottenChild.freeCells, pottenChild.foundations, freeCells[i]);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
						
						break; // i don't want to insert to another foundation slot the same card
						
					} else if(foundations[founIndex]!=null&&foundations[founIndex].getTribe()==freeCells[i].getTribe() && foundations[founIndex].getValue()+1==freeCells[i].getValue()) { 
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+freeCells[i].getTribe()+""+freeCells[i].getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						pottenChild.moveToArray(freeCells[i], pottenChild.foundations, founIndex);
						pottenChild.moveFromArray(pottenChild.freeCells, i);
						//pottenChild.outOfFoundation = this.outOfFoundation-1;
						
						//pottenChild.makeAMove(pottenChild.freeCells, pottenChild.foundations, freeCells[i]);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
					}
				}
				// movement from freeCell to stack
				for(int j=0;j<stacks.size();j++) {
					if(stacks.get(j).isEmpty()) {
						TreeNode pottenChild = this.createIdenticalTreeNode("newstack "+freeCells[i].getTribe()+""+freeCells[i].getValue(), outOfFoundation); //create a copy of this treeNode
						
						pottenChild.moveToStack(freeCells[i], pottenChild.stacks.get(j));
						pottenChild.moveFromArray(pottenChild.freeCells, i);
						
						
						//pottenChild.makeAMove(pottenChild.freeCells, pottenChild.stacks.get(j), freeCells[i]);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
						
					}else{ // if the the pointed by j stack is not empty
						Card destLastCard =stacks.get(j).lastElement();
						if(freeCells[i].canMove(destLastCard)) {
							TreeNode pottenChild = this.createIdenticalTreeNode("stack "+freeCells[i].getTribe()+""+freeCells[i].getValue()+" "+destLastCard.getTribe()+""+destLastCard.getValue(), outOfFoundation); //create a copy of this treeNode
							
							pottenChild.moveToStack(freeCells[i], pottenChild.stacks.get(j));
							pottenChild.moveFromArray(pottenChild.freeCells, i);
							
							
							//pottenChild.makeAMove(pottenChild.freeCells, pottenChild.stacks.get(j), freeCells[i]);
							
							if(!pottenChild.alreadyExists()) {
								childTrNodeList.add(pottenChild);
							}
						}
					}
				}
			}
		}
		return childTrNodeList;
	}
	
	//create a new treenode with the exact same values
	
	public TreeNode createIdenticalTreeNode(String descr, int notInFoundation) { //int g,
		TreeNode newTreeNode = new TreeNode(this, descr, g, notInFoundation);	// create a child so the g will be by 1 bigger
		for(int i=0;i<this.stacks.size();i++) {
			if(i<4) {
				newTreeNode.foundations[i]=this.foundations[i];
				newTreeNode.freeCells[i]=this.freeCells[i];
			}
			newTreeNode.stacks.put(i, (Stack<Card>) this.stacks.get(i).clone());
		}
		return newTreeNode;
	}
	
	/*
	public void makeAMove(Object source, Object destination, Card c) {
		
		
		if(destination.getClass().toString().equals("class java.util.Stack")) { // if destination is a stack
			Stack<Card> dest = (Stack<Card>) source;
			moveToStack(c, dest);
		} else if(destination.getClass().toString().equals("class [LCard;")) { // if destination is a array //if(destination.getClass().toString().equals("class [LCard;"))
			Card[] dest = (Card[]) source;
			moveToArray(c, dest);
		}
		
		if(source.getClass().toString().equals("class java.util.Stack")) { // if source is a stack
			Stack<Card> src = (Stack<Card>) source;
			moveFromStack(c, src);
		} else { // if source is a array //if(source.getClass().toString().equals("class [LCard;"))
			Card[] src = (Card[]) source;
			moveFromArray(c, src);
		}
	}
	*/
	
	public void moveToArray(Card card, Card[] dest, int pos) { //int pos
		dest[pos] = card;
	}
	public void moveToStack(Card card, Stack<Card> dest) {
		dest.push(card);
	}
	public void moveFromStack(Card card, Stack<Card> source) {
		source.removeElement(card);
	}
	public void moveFromArray(Card[] source, int pos) {//int pos
		source[pos] = null;
	}
	
//CHECK IT AGAIN
	public boolean alreadyExists() {
		TreeNode selectedNode = this.parent;
		
		while(selectedNode!=null) {
			
			boolean isSame = sameStacks(selectedNode.stacks);
			
			// i don't check freecells because if all stacks and foundation slots are the same, then and freecells will be the same  maybe with other order (still same)
			if(isSame) { // if the 2 nodes have the same cards in their stacks 
				for(int j=0;j<foundations.length;j++) {  // compare the cards in foundations and freecells
					if((foundations[j]!=null && selectedNode.foundations[j]!=null && (!foundations[j].isTheSame(selectedNode.foundations[j]))) // if in that positions are cards and they are different 
							|| (foundations[j]!=null && selectedNode.foundations[j]==null) ) { //if parent's foundation slot is empty and child foundation slot has a card  // ||(!freeCells[j].isTheSame(selectedNode.freeCells[j]))
						isSame = false;
						break;
					} 
				}
			}
			if(isSame) { // if the 2 nodes are exactly the same 
				break;
			}
			selectedNode = selectedNode.parent; 
		}
		
		if(selectedNode!=null) { // if i have not check all the nodes up to the root node, i find a duplicate node
			return true;
		} else {
			return false;
		}
	}
	
	
	// checks if all stacks from one node exists to another even if with other order	
	public boolean sameStacks(HashMap<Integer, Stack<Card>> foreignStacks) {
		//DELETE IT POSSIBLY
		/*
		for(int i=0;i<stacks.size();i++) {
			if(stacks.get(i).size()==selectedNode.stacks.get(i).size()) { // if the i stack of each node has the same amount of cards  
				for(int j=0;j<stacks.get(i).size();j++) { //check every card of the 2 stacks
					if(!stacks.get(i).get(j).isTheSame(selectedNode.stacks.get(i).get(j))) { //if is not the same card in the exact same position in the stacks
						isSame = false;
						break;
					}
				}
				// is usefull;;
				if(!isSame) { // it does not need to check the rest stacks if i find a difference between the 2 nodes
					break;
				} 
			} else { // if the 2 stacks have different amount of cards in their stacks
				isSame = false;
				break;
			}
		}*/
		
		
		for(int i=0;i<stacks.size();i++) {  // index for this node stacks
			boolean found = false;
			
			for(int j=0;j<foreignStacks.size();j++) { // index for foreign's node stacks
				if(stacks.get(i).size()==foreignStacks.get(j).size()) { // if they have the same amount of cards in their given stack then i compare all the cards whick are located in the same stack position
					found = true;
					for(int k=0;k<foreignStacks.get(j).size();k++) {
						if(!(stacks.get(i).get(k).isTheSame(foreignStacks.get(j).get(k)))) { // if the cards in position k of the stacks are different, i start searching the remaining foreign stacks
							found = false;
							break; // no need to compare the remaining cards of these stacks
						}
					}
				}
				if(found) { // if the program find the same stack in these stacks, it breaks the foreign stack's loop in order to check the remaining stacks
					break;
				}
			}
			
			if(!found) { // if the program did not find the same stack in these stacks, will return that these nodes are different
				return false;
			}
		}
		
		return true; // in order to execute this line, all stacks of this node have been found in the other node
	}
	
	
	public boolean isSolution() {
		//to be a solution, a node must have all stacks and freeCells empty
		boolean solution = true;
		
		for(int i=0;i<stacks.size();i++) { 
			if(!stacks.get(i).isEmpty()) {
				solution = false;
				break;
			}
		}
		//if all stacks are empty 
		if(solution) {
			for(int i=0;i<freeCells.length;i++) {
				if(freeCells[i]!=null) {
					solution = false;
					break;
				}
			}
		}
		return solution;
	}
	
	// this method returns the amount of cards that block the cards that can be inserted to foundation 
	// i don't bother if the cards are in a freecell slot because they are free to enter foundation slot
	public int degreeOfEntrapment() {
		ArrayList<String> nextCardsToFoundation = new ArrayList<String>();
		char[] remainingTribes = new char[]{'S', 'H', 'D', 'C'}; //if 
		int degree = 0;
		
		//initialize the arrayList
		for(int i=0;i<foundations.length;i++) { 
			if(foundations[i]!=null) {
				nextCardsToFoundation.add(foundations[i].getTribe()+""+(foundations[i].getValue()+1));
				for(int j=0;j<4;j++) {
					if(foundations[i].getTribe()==remainingTribes[j]) {
						remainingTribes[j] = ' ';
					}
				}
			} 
		}
		if(nextCardsToFoundation.size()!=4) {//if 1(or more) foundation slot(s) is(are) empty 
			for(int i=0;i<4;i++) {
				if(remainingTribes[i]!=' ') {
					nextCardsToFoundation.add(remainingTribes[i]+"0");
				}
			}
		}
		
		
		for(int i=0;i<nextCardsToFoundation.size();i++) {
			//search for this card
			boolean found = false;
			for(int j=0;j<stacks.size();j++) { //for every stack
				for(int k=0;k<stacks.get(j).size();k++) { // for every card inside this stack
					if(nextCardsToFoundation.get(i).equals(stacks.get(j).get(k).getTribe()+""+stacks.get(j).get(k).getValue())) { // i find one of the cards that can be inserted into foundation
						degree = degree + ((stacks.get(j).size() - (k+1))/4); // k+1 because starts from 0 
						found = true;
						break;
					}
				}
				if(found) { //if i find this card in the stacks
					break; // stop searching the remaining stacks 
				} 
			}	
		}
		
		
		
		return degree;
	}
	
	public void calculateCost(String method) {
		h=(this.outOfFoundation);//+this.degreeOfEntrapment()
		f=h;
		if(method.equals("astar")) {
			f+=g;
		} 
	}
		
	public void printData() {
		
	}
}
