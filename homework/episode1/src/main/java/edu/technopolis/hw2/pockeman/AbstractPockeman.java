package hw2.pockeman;

import hw2.states.IState;

public abstract class AbstractPockeman {

	protected IState curState;
	public void setState(IState iState){
		curState = iState;
	};
}
