package dcct.combinatorics;

import java.util.Iterator;
/**
 * This class represents a set that holds integer values. Whether an element belongs to the set or not
 * is represented by the binary value of the 'set' attribute, which is of type 'int'. Set operations such 
 * adding an element to the set, removing an element from the set or element membership are implemented
 * as binary operations, thus the methods add, remove and contains each take O(1). Iterable and Iterator 
 * interfaces are implemented so that the set can be iterated in for each loops. 
 * @author Fausto
 * 
 */
class CSet implements Iterable<Integer>{
		private int current = 0;
		private int cardinality = 0;
		private int set = 0;
		/**
		 * A copy constructor
		 * @param original The CSet instance that will be copied. 
		 */
		public CSet(CSet original){
			this.cardinality = original.cardinality;
			this.set = original.set;
		}
		
		public CSet(int n){
			cardinality = n;
			this.set = (int) (Math.pow(2,n)-1);
		}
		public void add(int j){
			set = set | (1<<j);
		}
		public void remove (int j){
			set = set - (1<<j);
		}
		public boolean contains(int j){
			return (1 & (set>>j))>0;
		}
		/** 
		 * Returns a set containing all integers i contained in this set such that i>j
		 * @param j An integer
		 * @return The set that contains only integers in this set greater than j. 
		 */
		public CSet B(int j){
			CSet tSet = new CSet(this.cardinality);
			int aux = (1<<(j+1))-1;
			tSet.setContentInteger((set|aux)-aux);
			return tSet;
		}
		public Iterator<Integer> iterator() {
			this.current=0;
			return new CSetIterator(this);
		}
		public int cardinality() {
			return cardinality;
		}
		public int getContentInteger() {
			return set;
		}
		/**
		 * Sets the integer that encodes a set containing integers to this CSet instance. 
		 * @param set The int whose binary representation represents the elements contained in a set. 
		 */
		public void setContentInteger(int set) {
			this.set = set;
		}

		class CSetIterator implements Iterator<Integer>{
			private CSet base;
			public CSetIterator(CSet base){
				this.base = base;
			}	
			public boolean hasNext() {		
				int temp = base.current;
				for (; !base.contains(temp) &&  temp < base.cardinality; temp++);
				return temp < cardinality;
			}
			public Integer next() {			
				for (; base.current<cardinality; base.current++){
					if (base.contains(base.current))
						return base.current++;
				}
				return null;
			}
			
		}
	}