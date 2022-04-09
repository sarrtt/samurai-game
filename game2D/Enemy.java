import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Enemy extends Sprite {

	final int WEIGHT = 3;
	int jump_dx = 0;

	int health = 2;
	int range = 256;
	int ownMovement = 0;
	int timer = 0;
	int reloadRate = 100; // used in a modulo operation

	Image cr_sh;
	Image st_sh;

	Animation stand_anim;
	Animation crouch_anim;
	Animation gotten_anim;
	Animation shoot_stand_anim;
	Animation shoot_crouch_anim;

	boolean crouching = false;
	boolean gotten = false;



	public Enemy(Animation anim) {
		super(anim);
		load();
	}
	
	
	
	public void load () {
		stand_anim = new Animation();
		crouch_anim = new Animation();
		gotten_anim = new Animation();
		shoot_stand_anim = new Animation();
		shoot_crouch_anim = new Animation();
		stand_anim.loadAnimationFromSheet("images/rmanwalk.png",4,1,400);
		crouch_anim.loadAnimationFromSheet("images/rman_crouch.png",1,1,400);
		gotten_anim.loadAnimationFromSheet("images/rman_gotten.png",1,1,400);
		shoot_stand_anim.loadAnimationFromSheet("images/rman_shoot_stand.png", 2, 1, 200);
		shoot_crouch_anim.loadAnimationFromSheet("images/rman_shoot_crouch.png", 2, 1, 200);

		setVelocityX(5);
	}



	public void attackChassepot(float targetX, float targetY) {
		if (gotten) return;

		if (!crouching) {
			if (getX() > targetX + range) {
				stand_anim.play();
				ownMovement = ownMovement - 5;
			} else if (getX() < targetX - range) {
				stand_anim.play();
				ownMovement = ownMovement + 5;
			} else {
				stand_anim.pauseAt(1);
				if (timer % reloadRate == 0) shoot();
			}
		}
	
		if (targetY <= 512) {
			crouching = true;
			setAnimation(crouch_anim);
			if (timer % reloadRate == 0) shoot();
		}

		if (targetY > 512 || (Math.abs(targetX - this.getX()) + range > 768 - targetY)) {
			crouching = false;
			setAnimation(stand_anim);
		}

		timer++;
	}



	private void shoot() {
		System.out.println("BANG!");

		if (crouching) {
			setAnimation(shoot_crouch_anim);
		} else {
			setAnimation(shoot_stand_anim);
		}
	}
	
	
	
	public void getGot(float targetX, float targetY, boolean fromRight, boolean fromLeft) {

		int leftTip = (int) targetX - 128;
		int leftHilt = (int) targetX - 64;
		int rightTip = (int) targetX + 192;
		int rightHilt = (int) targetX + 128;


		if (fromLeft) {
			int myleft = (int) this.getX() + 64;
			int myright = myleft + 64;

			if ((rightTip >= myleft) && (rightHilt <= myleft)) {
				gotten = true;
			}

		} else if (fromRight) {
			int myleft = (int) this.getX();
			int myright = myleft + 64;

			if ((leftTip <= myright) && (leftHilt >= myright)) {
				gotten = true;
			}
		} else gotten = false;

		if (gotten) {
			health--;
			setAnimation(gotten_anim);
			gotten_anim.pause();
			
			if (health < 1) {
				while (getY() < 800) {
					shiftY(jump_dx * -1);
					jump_dx -= WEIGHT;
				}
				hide();
			}
		}
	}



	public void attackKatana() {
	}
	
	
	public void update(long elapsed) {
		stand_anim.update(elapsed);
		crouch_anim.update(elapsed);
		gotten_anim.update(elapsed);
	}

	public int getOwnMovement() {
		return ownMovement;
	}

	public boolean getStatus() {
		return gotten;
	}
}
