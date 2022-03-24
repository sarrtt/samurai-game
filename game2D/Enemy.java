public class Enemy extends Sprite {

	Animation anim;
	int health = 2;

	public Enemy(Animation anim) {
		super(anim);
		this.anim = anim;
	}

}
