package hw2.pockeman;

import hw2.states.IState;
import hw2.states.Training;

public class PockemonA extends AbstractPockeman{
	
	public int hp;

    public PockemonA() {
        curState = new Training(this);
        hp = 100;
    }

    public void train() {
        hp -= 30;
        curState.train();
    }

    public void relax() {
    	hp += 70;
        curState.relax();
    }

    public void fight() {
    	hp -= 25;
        curState.fight();
    }

    public void SetState(IState state) {
        super.curState = state;
    }

}
