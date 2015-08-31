package unam.dcct.model.ImmediateSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class generates all permutations of the set {0,..,n-1}.
 * It is an implementation of the Trotter-Johnson algorithm 
 * described in the book Constructive Combinatorics by D. Stanton and D. White. 
 * @author Fausto Salazar
 *
 */
public class PermutationGenerator implements Iterable<List<Integer>>{

	private int n=0;
		
	public static void main(String[] args){
		PermutationGenerator perm = new PermutationGenerator(5);
		for (List<Integer> p : perm){
			System.out.println(p);
		}
	}
	
	public PermutationGenerator(int n){
		this.n = n;
	}

	public Iterator<List<Integer>> iterator() {
		return new PermutationIterator(n);
	}
	
	private class PermutationIterator implements Iterator<List<Integer>>{
		boolean done=false;
		boolean first=true;
		int n=0;
		// Contains the actual permutation being built. Positions 0 and n+1 are 'boundary positions'
		List<Integer> pi = new ArrayList<Integer>(n+2);
		// Stores the current position of each element in pi
		List<Integer> _pi = new ArrayList<Integer>(n+2);
		// Stores the direction in which the current element is being moved.
		List<Integer> d = new ArrayList<Integer>(n+2);
		// Stores the elements that are moving. 
		Set<Integer> A = new HashSet<Integer>(n);
		
		private PermutationIterator(int n){
			this.n = n;
			initialize();
		}
		
		private void initialize(){
			for (int i=0; i<=n+1; i++){
				pi.add(i);
				_pi.add(i);
				d.add(-1);
			}
			pi.set(0,n+1);
			for (int i = 2; i<= n; i++)
				A.add(i);
		}
		
		public boolean hasNext() {
			if (first){
				first = false;
			}
			else 
				if (!A.isEmpty()){
					int m = Collections.max(A);
					int j = _pi.get(m);
					// move m
					pi.set(j, pi.get(j+d.get(m)));
					pi.set(j+d.get(m), m);
					// update positions
					_pi.set(m, _pi.get(m)+d.get(m));
					_pi.set(pi.get(j), j);
					// check if close enough to boundary position
					if (m < pi.get(j+2*d.get(m))){
						d.set(m, -d.get(m));
						A.remove(m);
					}
					Set<Integer> T = new HashSet<Integer>(n-m);
					for (int i = m+1; i<= n; i++)
						T.add(i);
					A.addAll(T);
				} 
				else done=true;
			return !done;
		}

		public List<Integer> next() {	
			// return sublist so that boundary positions are not included
			return pi.subList(1, n+1);
		}
	}
}
