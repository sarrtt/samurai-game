import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class FallingSprite extends Sprite {

	final int WEIGHT = 10;
	int jump_dx = 0;

	Animation chand_anim;

	boolean gotten = false;



	public FallingSprite(Animation anim) {
		super(anim);
		load();
	}
	
	
	
	public void load () {
		chand_anim = new Animation();
		chand_anim.loadAnimationFromSheet("images/chand.png",4,1,400);

		//setX();
		//setY();
		setVelocityX(5);
	}



	public void getGot(float targetX, float targetY, boolean fromRight, boolean fromLeft) {

		int leftTip = (int) targetX - 128;
		int leftHilt = (int) targetX - 64;
		int rightTip = (int) targetX + 192;
		int rightHilt = (int) targetX + 128;


		if (fromLeft) {
			int myleft = (int) this.getX();
			int myright = myleft + 	192;

			if ((rightTip >= myleft) && (rightHilt <= myleft)) {
				gotten = true;
			}

		} else if (fromRight) {
			int myleft = (int) this.getX();
			int myright = myleft + 192;

			if ((leftTip <= myright) && (leftHilt >= myright)) {
				gotten = true;
			}
		} else gotten = false;

		if (gotten) {
			while (getY() < 640) {
				shiftY(jump_dx);
				jump_dx -= WEIGHT;
			}
		}
	}



	public void attackKatana() {
	}
	
	
	public void update(long elapsed) {
		chand_anim.update(elapsed);
	}


	public boolean getStatus() {
		return gotten;
	}
}
