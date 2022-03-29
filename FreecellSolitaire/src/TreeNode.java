import java.util.*;

public class TreeNode implements Cloneable {

	Card[] freeCells;
	Card[] foundations; // i keep the last card in every foundation
	HashMap<Integer, Stack<Card>> stacks;
	TreeNode parent;
	int g, h, f;
	String description; //it will keep the movement which led to this node 
	int outOfFoundation; // amount of cards that are not in foundation slots
	
	int totalCards;
	double averageCardValue;
	int cardsValueSum; // sum of the value of all cardss
	
	// CONSTRUCTORS
	public TreeNode(TreeNode par, String descr, int step, int oOF, double avgCardValue, int totCards, int cValueSum) { // is used only when i create the children of a node
		freeCells = new Card[4];
		foundations = new Card[4];
		stacks = new HashMap<Integer, Stack<Card>>();
		parent = par;
		description = descr;
		g = step+1;
		outOfFoundation = oOF;
		
		averageCardValue = avgCardValue;
		totalCards = totCards;
		cardsValueSum = cValueSum;
	}

	public TreeNode(HashMap<Integer, Stack<Card>> s, int amountOfCards, int cValueSum) { // it used only for the root node 
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
		
		totalCards = amountOfCards;
		cardsValueSum = cValueSum;
		averageCardValue = (double)cardsValueSum / totalCards;  
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
	
	
	public ArrayList<TreeNode> findChildren() { 
		ArrayList<TreeNode> childTrNodeList = new ArrayList<TreeNode>(); //keeps the child-nodes
		
		//check for children node by a movement from a stack
		for(int i=0;i<stacks.size();i++) {// for every stack of this treeNode 
			if(!(stacks.get(i).isEmpty())) { // if it is not empty
				Card lastCard = stacks.get(i).lastElement(); // get the last card
				
				//checks for child by moving a card to foundation
				for(int founIndex=0;founIndex<foundations.length;founIndex++) { // for every foundation slot
					if((foundations[founIndex]==null&&lastCard.getValue()==0)) { //if there is no card in foundation slot and the under process card is 0 
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						//make the movements 
						pottenChild.moveToArray(lastCard, pottenChild.foundations, founIndex);
						pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
												
						if(!pottenChild.alreadyExists()) { 
							childTrNodeList.add(pottenChild);
						}
						break; // i don't want to insert the same card to another foundation slot 
						
					 //if the specific card and the current foundation slot has the same tribe and the card has bigger value by 1
					} else if(foundations[founIndex]!=null&&foundations[founIndex].getTribe()==lastCard.getTribe() && foundations[founIndex].getValue()+1==lastCard.getValue()) { 
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						//make the movements 
						pottenChild.moveToArray(lastCard, pottenChild.foundations, founIndex);
						pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
					}
				}
				
				
				//checks for child by moving a card to another stack
				for(int j=0;j<stacks.size();j++) {
					if(j!=i) { //if the two pointers does not refer to the same stack
						if(stacks.get(j).isEmpty()) {
							if(stacks.get(i).size()!=1) { // if the current stack of the moving card has only this card, i don't want to move to an empty stack because will occur the same treenode (change to the position of empty stacks)
								TreeNode pottenChild = this.createIdenticalTreeNode("newstack "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation); //create a copy of this treeNode
								
								//make the movements 
								pottenChild.moveToStack(lastCard, pottenChild.stacks.get(j));
								pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
								
								if(!pottenChild.alreadyExists()) {
									childTrNodeList.add(pottenChild);
								}
							}
						} else { // if the the pointed by j stack is not empty
							Card destLastCard =stacks.get(j).lastElement();
							if(lastCard.canMove(destLastCard)) {
								TreeNode pottenChild = this.createIdenticalTreeNode("stack "+lastCard.getTribe()+""+lastCard.getValue()+" "+destLastCard.getTribe()+""+destLastCard.getValue(), outOfFoundation); //create a copy of this treeNode
								
								//make the movements 
								pottenChild.moveToStack(lastCard, pottenChild.stacks.get(j));
								pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
								
								if(!pottenChild.alreadyExists()) {
									childTrNodeList.add(pottenChild);
								}
							}
						}
					}
				}
				
				// checks for child by a movement from stack to freecell
				for(int k=0;k<freeCells.length;k++) {
					if(freeCells[k]==null) {
						TreeNode pottenChild = this.createIdenticalTreeNode("freecell "+lastCard.getTribe()+""+lastCard.getValue(), outOfFoundation); //create a copy of this treeNode
						
						//make the movements 
						pottenChild.moveToArray(lastCard, pottenChild.freeCells, k);
						pottenChild.moveFromStack(lastCard, pottenChild.stacks.get(i));
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						} 
						break; // if i find 1 free freeCell, i don't want to check the remaining freecells position
					}
				}
			}
		} // end of search for children from stack movement
		
		
		// checks for children by a movement from freecell
		for(int i=0;i<freeCells.length;i++) { 
			if(freeCells[i]!=null) {
		
				//movement from freecell to foundation
				for(int founIndex=0;founIndex<foundations.length;founIndex++) { 
					
					if((foundations[founIndex]==null&&freeCells[i].getValue()==0)) { // if foundation slot is empty and the freecell's card has value of 0 
			
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+freeCells[i].getTribe()+""+freeCells[i].getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						//make the movements
						pottenChild.moveToArray(freeCells[i], pottenChild.foundations, founIndex);
						pottenChild.moveFromArray(pottenChild.freeCells, i);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
						
						break; // i don't want to insert to another foundation slot the same card
						
						//if the foundation slot is not empty and freecell's card can be inserted to this slot
					} else if(foundations[founIndex]!=null&&foundations[founIndex].getTribe()==freeCells[i].getTribe() && foundations[founIndex].getValue()+1==freeCells[i].getValue()) { 
						TreeNode pottenChild = this.createIdenticalTreeNode("source "+freeCells[i].getTribe()+""+freeCells[i].getValue(), outOfFoundation-1); //create a copy of this treeNode 
						
						//make the movements
						pottenChild.moveToArray(freeCells[i], pottenChild.foundations, founIndex);
						pottenChild.moveFromArray(pottenChild.freeCells, i);
						
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
					}
				}
				// movement from freeCell to stack
				for(int j=0;j<stacks.size();j++) {
					if(stacks.get(j).isEmpty()) {
						TreeNode pottenChild = this.createIdenticalTreeNode("newstack "+freeCells[i].getTribe()+""+freeCells[i].getValue(), outOfFoundation); //create a copy of this treeNode
						
						//make the movements
						pottenChild.moveToStack(freeCells[i], pottenChild.stacks.get(j));
						pottenChild.moveFromArray(pottenChild.freeCells, i);
												
						if(!pottenChild.alreadyExists()) {
							childTrNodeList.add(pottenChild);
						}
						
					} else{ // if the the pointed by j stack is not empty
						Card destLastCard =stacks.get(j).lastElement();
						if(freeCells[i].canMove(destLastCard)) {
							TreeNode pottenChild = this.createIdenticalTreeNode("stack "+freeCells[i].getTribe()+""+freeCells[i].getValue()+" "+destLastCard.getTribe()+""+destLastCard.getValue(), outOfFoundation); //create a copy of this treeNode
							
							//make the movements
							pottenChild.moveToStack(freeCells[i], pottenChild.stacks.get(j));
							pottenChild.moveFromArray(pottenChild.freeCells, i);
							
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
	
	public TreeNode createIdenticalTreeNode(String descr, int notInFoundation) {
		TreeNode newTreeNode = new TreeNode(this, descr, g, notInFoundation, averageCardValue, totalCards, cardsValueSum);	// create a child node
		for(int i=0;i<this.stacks.size();i++) {
			if(i<4) {
				newTreeNode.foundations[i]=this.foundations[i];
				newTreeNode.freeCells[i]=this.freeCells[i];
			}
			newTreeNode.stacks.put(i, (Stack<Card>) this.stacks.get(i).clone());
		}
		return newTreeNode;
	}
		
	public void moveToArray(Card card, Card[] dest, int pos) {
		dest[pos] = card;
	}
	public void moveToStack(Card card, Stack<Card> dest) {
		dest.push(card);
	}
	public void moveFromStack(Card card, Stack<Card> source) {
		source.removeElement(card);
	}
	public void moveFromArray(Card[] source, int pos) {
		source[pos] = null;
	}
	
	//check for an already existing node to this path
	public boolean alreadyExists() {
		TreeNode selectedNode = this.parent;
		
		while(selectedNode!=null) {
			
			boolean isSame = sameStacks(selectedNode.stacks);
			
			// i don't check freecell slots because if all stacks and foundation slots are the same, then and freecell slots will be the same  maybe with other order (still same)
			if(isSame) { // if the 2 nodes have the same cards in their stacks 
				for(int j=0;j<foundations.length;j++) {  // compare the cards in foundation slots
					if((foundations[j]!=null && selectedNode.foundations[j]!=null && (!foundations[j].isTheSame(selectedNode.foundations[j]))) // if in that positions are cards and they are different 
							|| (foundations[j]!=null && selectedNode.foundations[j]==null) ) { // or if parent's foundation slot is empty and child foundation slot has a card  
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
		
		if(selectedNode!=null) { // if i have not check all the nodes up to the root node, i found a duplicate node
			return true;
		} else {
			return false;
		}
	}
	
	
	// checks if all stacks from one node exists to another even if with other order	
	public boolean sameStacks(HashMap<Integer, Stack<Card>> foreignStacks) {		
		for(int i=0;i<stacks.size();i++) {  // index for this node's stacks
			boolean found = false;
			
			for(int j=0;j<foreignStacks.size();j++) { // index for foreign's node stacks
				if(stacks.get(i).size()==foreignStacks.get(j).size()) { // if they have the same amount of cards in indexed stack then i compare all the cards which are located in the same stack position
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
	
	//this method return a number that indicate how untrapped the cards which can move to foundation are
	//for every card of 4 which is totally untrapped(freecell or last card at stack: degree+= 1;
	//for every card of 4 which is trapped by one card only: degree+= 0.5;
	public double degreeOfImprovement() {

		ArrayList<String> nextCardsToFoundation = new ArrayList<String>();
		char[] remainingTribes = new char[]{'S', 'H', 'D', 'C'}; 
		double degree = 0;
		
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
		
		
		//search each card which can move to freecell
		for(int i=0;i<nextCardsToFoundation.size();i++) {
			boolean found = false;
			for(int j=0;j<stacks.size();j++) { //for every stack
				for(int k=0;k<stacks.get(j).size();k++) { // for every card inside this stack
					if(nextCardsToFoundation.get(i).equals(stacks.get(j).get(k).getTribe()+""+stacks.get(j).get(k).getValue())) { // if i find one of the cards that can be inserted into foundation
						if(stacks.get(j).size()-1 == k) { // if the card is the last of the stack
							degree +=1;  
						} else if(stacks.get(j).size()-2 == k) {
							degree += 0.5;
						}
						found = true;
						break;
					}
				}
				if(found) { //if i find this card in the stacks
					break; // stop searching the remaining stacks 
				} 
			}
			
			// if the specified card is not located into the stacks
			if(!found) {
				for(int j=0;j<freeCells.length;j++) {
					if(freeCells[j]!=null&&nextCardsToFoundation.get(i).equals(freeCells[j].getTribe()+""+freeCells[j].getValue())) {
						degree +=1; 
						found = true;
						break;
					}
				}
			}
		}
		return degree;
	}
	
	//h = ceil of sum value of all cards - number of collected cards * average card value  - floor of degree of improvement * half of average card value 
	public void calculateCost(String method) {
		h= (int) ((Math.ceil(cardsValueSum - (totalCards-outOfFoundation)*averageCardValue)) - (Math.floor(degreeOfImprovement()*0.5*averageCardValue)));  		
		f=h;
		if(method.equals("astar")) {
			f+=g;
		} 
	}
}