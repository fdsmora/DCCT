package dcct.topology;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dcct.process.*;
import dcct.process.Process;
import dcct.combinatorics.*;

import org.apache.commons.collections4.iterators.PermutationIterator;
public class AtomicImmediateSnapshot implements CommunicationModel{
	
	public Set<Simplex> generateNewSimplices(Set<Simplex> simplices){
		Set<Simplex> newSimplices = new LinkedHashSet<Simplex>();
		
		for(Simplex s: simplices){
			String allDelimitations = DelimiterGenerator.generate(s.Dimension());
			PermutationIterator<Process> iter = new PermutationIterator<Process>(s.processes);
			String[] groups = allDelimitations.split("|");
			for (String g: groups){
				int[] indices = toIndices(g);
				while(iter.hasNext())
				{
					List<Process> permut = iter.next();
					Simplex newSimplex = performExecution(permut,indices);
					newSimplices.add(newSimplex);
				}
			}
		}
		return newSimplices;
	}
	
	protected Simplex performExecution(List<Process> permut, int[] indices) {
		Process[] newProcesses = new Process[permut.size()];
		String[] memory = new String[permut.size()];
		int i;
		for(i=0;i<indices.length;i++){
			Process p = (Process)permut.get(indices[i]).clone();
			p.write(memory);
			newProcesses[i]=p;
		}
		for (Process p: newProcesses){
			p.snapshot(memory);
		}
		return new Simplex(newProcesses);
	}
	
	protected int[] toIndices(String group){
		int n = group.length();
		int[] indices = new int[n];
		for(int i=0; i<n; i++)
			indices[i]=group.charAt(i);
		return indices;
	}
}


