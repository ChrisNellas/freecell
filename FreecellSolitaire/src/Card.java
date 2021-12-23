
public class Card {
	private char tribe;
	private int value;

	public Card(char tr, int val) {
		tribe = tr;
		value = val;
	}

	public void setValue(int v) {// delete this method
		value = v;
	}

	public char getTribe() {
		return tribe;
	}

	public int getValue() {
		return value;
	}

	public boolean isColorDifferent(Card c) {
		if ((tribe == 'S' || tribe == 'C') && (c.tribe == 'H' || c.tribe == 'D')
				|| (tribe == 'H' || tribe == 'D') && (c.tribe == 'S' || c.tribe == 'C')) {
			return true;
		}
		return false;
	}
	
	public boolean canMove(Card lastCardOfDestStack) {
		if(this.value+1==lastCardOfDestStack.value && isColorDifferent(lastCardOfDestStack)) {
			return true;
		}
		return false;
	}
	
	public boolean isTheSame(Card c) {
		if(tribe==c.tribe && value==c.value) {
			return true;
		} else {
			return false;
		}
	}
}