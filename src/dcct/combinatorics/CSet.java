package dcct.combinatorics;

import java.util.Iterator;
/**
 * This class represents a set that holds integer values. Whether an element belongs to the set or not
 * is represented by the binary value of the 'set' attribute, which is of type 'int'. Set operations such 
 * as adding an element to the set, removing an element from the set or checking element membership are implemented
 * as binary operations, thus the methods add, remove and contains each take O(1). Iterable and Iterator 
 * interfaces are implemented so that the set can be iterated in for each loops. 
 * @author Fausto
 * 
 */
class CSet implements Iterable<Integer>{
		private int current = 0;
		private int cardinality = 0;
		private int set = 0;
		private int capacity=0;
		/**
		 * A copy constructor
		 * @param original The CSet instance that will be copied. 
		 */
		public CSet(CSet original){
			this.cardinality = original.cardinality;
			this.capacity = original.capacity;
			this.set = original.set;
		}
		
		/** 
		 * Creates a CSet with n elements.
		 * @param n Must be a non-negative integer, otherwise an IlegalArgumentException is thrown.
		 */
		public CSet(int n){
			if (n<0) 
				throw new IllegalArgumentException("n must be non-negative");
			cardinality = capacity = n;
			this.set = (int) (Math.pow(2,n)-1);
		}
		/**
		 * Sets the bit at position j to 1 to indicate that element j is added to the set. 
		 * @param j index of the bit that will be set to 1. 
		 */
		public void add(int j){
			if (j<0) 
				throw new IllegalArgumentException("j must be non-negative");
			int temp = set;
			set = set | (1<<j);
			if (set != temp){ // check if add operation really took effect.
				cardinality++;
				capacity = (int) Math.ceil(Math.log(set)/Math.log(2)); // If added element is beyond capacity, re-adjust capacity
			}
		}
		/**
		 * Sets the bit at position j to 0 to indicate that element j is removed from the set. 
		 * @param j index of the bit that will be set to 0. 
		 */
		public void remove(int j){
			if (j<0) 
				throw new IllegalArgumentException("j must be non-negative");
			int temp = set;
			set = set - (1<<j);
			if (set != temp) // check if remove operation really took effect.
				cardinality--;
		}
		/**
		 * Checks if the bit at position j is set to 1, which means element j is in the set. 
		 * @param j index of the bit that will checked. 
		 */
		public boolean contains(int j){
			if (j<0) 
				throw new IllegalArgumentException("j must be non-negative");
			return (1 & (set>>j))>0;
		}
		/** 
		 * Returns a set containing all integers i contained in this set such that i>j
		 * @param j A non-negative integer
		 * @return The set that contains only integers in this set greater than j. 
		 */
		public CSet B(int j){
			if (j<0) 
				throw new IllegalArgumentException("j must be non-negative");
			CSet tSet = new CSet(this);
			int aux = (1<<(j+1))-1;
			tSet.set = (set|aux)-aux;
			tSet.cardinality = 0;
			for (int i=j+1; i<capacity; i++)
				if (tSet.contains(i))
					tSet.cardinality++; 
			return tSet;
		}
		public Iterator<Integer> iterator() {
			this.current=0;
			return new CSetIterator(this);
		}
		public int cardinality() {
			return cardinality;
		}

//		private void setContentInteger(int set) {
//			if (set<0) 
//				throw new IllegalArgumentException("set must be non-negative");
//			this.set = set;
//			this.capacity = (int) Math.ceil(Math.log(set)/Math.log(2)); 
//		}

		class CSetIterator implements Iterator<Integer>{
			private CSet base;
			public CSetIterator(CSet base){
				this.base = base;
			}	
			public boolean hasNext() {	
				int temp = base.current;
				for (; !base.contains(temp) &&  temp < base.capacity; temp++);
				return temp < capacity;
			}
			public Integer next() {			
				for (; base.current<base.capacity; base.current++){
					if (base.contains(base.current))
						return base.current++;
				}
				return null;
			}
			
		}
	}