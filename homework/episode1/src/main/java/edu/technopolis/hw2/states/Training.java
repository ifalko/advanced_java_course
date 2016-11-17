package hw2.states;

import hw2.pockeman.AbstractPockeman;

public class Training implements IState {
	
	AbstractPockeman pockeman;
	
	public Training(AbstractPockeman pockeman) {
		this.pockeman = pockeman;
	}

	@Override
	public void train() {
		System.out.println("Надеюсь ты хорошо отдохнул!");
		pockeman.setState(new Fighting(pockeman));
	}

	@Override
	public void fight() {
		System.out.println("Рано еще драться, ты должен тренироваться");

	}

	@Override
	public void relax() {
		System.out.println("Не отлынивай! потом отдохнешь");
	}

}
