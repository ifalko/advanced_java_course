package hw2.states;

import hw2.pockeman.AbstractPockeman;

public class Fighting implements IState {

	AbstractPockeman pockeman;
	
	public Fighting(AbstractPockeman pockeman){
		this.pockeman = pockeman;
	}
	@Override
	public void train() {
		System.out.println("Перед смертью не надышишься");
	}

	@Override
	public void fight() {
		System.out.println("Покажи всё, на что способен");
		pockeman.setState(new Relaxation(pockeman));
	}

	@Override
	public void relax() {
		System.out.println("Лёгкой прогулочкой этот бой не назовёшь, должен драться!");
	}

}
