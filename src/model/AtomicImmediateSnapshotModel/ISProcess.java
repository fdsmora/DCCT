package model.AtomicImmediateSnapshotModel;

import dctopology.Process;

public class ISProcess extends Process {
	
	public ISProcess(int id) {
		super(id);
	}

	public void write(String[] memory){
		memory[this.id]=this.getView();
	}
	
	public void snapshot(String[] memory){
		this.setView(memory.clone());
	}
}
