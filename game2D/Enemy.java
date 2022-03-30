import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Enemy extends Sprite {

	Animation anim;
	int health = 2;
	int range = 300;
	
	Image stand_img;
	Image crouch_img;
	
	Animation stand_anim;
	Animation crouch_anim;

	public Enemy(Animation anim) {
		super(anim);
		this.anim = anim;
		load();
	}
	
	
	
	public void load() {
		Image stand_img = loadImage("images/rifleman.png");
		Animation stand_anim = new Animation();
		stand_anim.addFrame(stand_img, 200);
		
		Image crouch_img = loadImage("images/rman_crouch.png");
		Animation crouch_anim = new Animation();
		crouch_anim.addFrame(crouch_img, 200);
	}



	public void attackChassepot(int indent, int xPos, int yPos) {
		setVelocityX(5);

		if ((getX() + 1537) > xPos + indent + range) {
			shiftX(- getVelocityX());
		} else if (((getX() + 1537)) < (xPos + indent) - range) {
			shiftX(getVelocityX());
		}
		
		if (yPos > 128) {
			setAnimation(crouch_anim);
			crouch_anim.play();
		} else {
			setAnimation(stand_anim);
			stand_anim.play();
		}
	}



	public void attackKatana() {
	}
}
