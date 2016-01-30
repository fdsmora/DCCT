package unam.dcct.view.commands;

import unam.dcct.view.View;

/**
 * Command for notifying views that a the user is about to create a new protocol complex.
 * @author Fausto
 *
 */
public class NewProtocolComplexCommand extends Command {

	public NewProtocolComplexCommand(View receiver) {
		super(receiver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		receiver.creatingNewProtocolComplex();
	}

}
