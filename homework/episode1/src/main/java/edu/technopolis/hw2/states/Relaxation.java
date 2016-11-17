package hw2.states;

import hw2.pockeman.AbstractPockeman;

public class Relaxation implements IState{
	
	AbstractPockeman pockeman;
	
	public Relaxation(AbstractPockeman pockeman) {
		this.pockeman = pockeman;
	}

	@Override
	public void train() {
		System.out.println("Эй, ты сейчас должен отдыхать!");
		
	}

	@Override
	public void fight() {
		System.out.println("После драки кулаками не машут");
		
	}

	@Override
	public void relax() {
		System.out.println("Даааа, отдыхай, ты сегдня молодец");
		pockeman.setState(new Training(pockeman));
	}

}
