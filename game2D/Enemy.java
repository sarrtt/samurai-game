import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Enemy extends Sprite {

	String type;
	final int WEIGHT = 3;
	int jump_dx = 0;

	int health;
	int range;
	int ownMovement = 0;
	int timer = 0;

	boolean ready = false;
	boolean aiming = false;
	boolean hit = false;
	float deadlyY;
	int reloadRate; // used in a modulo operation

	Image cr_sh;
	Image st_sh;

	Animation stand_anim;
	Animation crouch_anim;
	Animation gotten_anim;
	Animation shoot_stand_anim;
	Animation shoot_crouch_anim;

	Animation screen_anim;
	Animation screen_torn_anim;

	Sound gunshot;
	Sound gunmiss;
	boolean die_sound_playing = false;

	TileMap rokumeikan = new TileMap();

	boolean crouching = false;
	boolean gotten = false;
	boolean dead = false;



	public Enemy(Animation anim, String who) {
		super(anim);

		type = who;
		if (type.equals("rifleman")) rifleman_load();
		if (type.equals("screen")) screen_load();
	}
	
	
	
	public void rifleman_load () {
		rokumeikan.loadMap("maps", "rokumeikan.txt");
		stand_anim = new Animation();
		crouch_anim = new Animation();
		gotten_anim = new Animation();
		shoot_stand_anim = new Animation();
		shoot_crouch_anim = new Animation();
		stand_anim.loadAnimationFromSheet("images/rifleman_walk.png",4,1,400);
		crouch_anim.loadAnimationFromSheet("images/rifleman_crouch.png",1,1,400);
		gotten_anim.loadAnimationFromSheet("images/rifleman_damage.png",1,1,400);
		shoot_stand_anim.loadAnimationFromSheet("images/rifleman_shoot_stand.png", 2, 1, 400);
		shoot_crouch_anim.loadAnimationFromSheet("images/rifleman_shoot_crouch.png", 2, 1, 400);

		health = 10;
		range = (int) (Math.random() * 4 + 2) * 64;
		reloadRate = (int) (Math.random() * 75 + 50);
		setVelocityX((float) (Math.random() * 13 + 7));
		setY(512);
	}

	public void screen_load () {
		screen_anim = new Animation();
		screen_torn_anim = new Animation();
		screen_anim.loadAnimationFromSheet("images/ballroom_screen.png",1,1,200);
		screen_torn_anim.loadAnimationFromSheet("images/ballroom_screen_damage.png",1,1,200);
		
		setAnimation(screen_anim);
		health = 3;
		setY(512);
	}



	public void attackChassepot(float targetX, float targetY) {
		if (dead) return;
		
		if (!crouching) {
			if (getX() > targetX + range) {
				ready = false;
				stand_anim.play();
				ownMovement = ownMovement - 5;
			} else if ((getX() < targetX - range)) {
				ready = false;
				stand_anim.play();
				ownMovement = ownMovement + 5;
			} else {
				stand_anim.pauseAt(1);
				ready = true;
			}
		}
	
		if (targetY <= 512) {
			crouching = true;
			setAnimation(crouch_anim);
			ready = true;
		}

		if (timer % reloadRate == 0) {
			if (!aiming) {
				Sound guncock = new Sound("sound/guncock.wav");
				guncock.start();
				aiming = true;
			} else {
				if (ready) shoot(targetX, targetY);
			}
		}
		

		if (targetY > 512 || (Math.abs(targetX - getX() + range - 64) > 768 - targetY)) {
			ready = false;
			crouching = false;
			setAnimation(stand_anim);
		}

		timer++;
	}



	private void shoot(float targetX, float targetY) {
		System.out.println("BANG!");

		if (crouching) {
			setAnimation(shoot_crouch_anim);
			deadlyY = 512 - (Math.abs(getX() - targetX));
		} else {
			setAnimation(shoot_stand_anim);
			deadlyY = 512 + 32;
		}

		int targetBottom = (int) targetY;
		int targetTop = targetBottom - 128;

		System.out.println(deadlyY + " " + targetTop + " " + targetBottom);
		
		aiming = false;
		
		if ((targetBottom > deadlyY) && (targetTop < deadlyY)) {
			gunshot = new Sound("sound/gunshot.wav");
			gunshot.start();
			hit = true;
			System.out.println("Got you!");	
		} else {
			gunmiss = new Sound("sound/gunmiss.wav");
			gunmiss.start();
		}
		
	}
	
	
	
	public void getGot(float targetX, float targetY, boolean fromRight, boolean fromLeft) {
		//if (dead) return;

		if (targetY < getY()) return;

		int leftTip = (int) targetX - 128;
		int leftHilt = (int) targetX - 64;
		int rightTip = (int) targetX + 192;
		int rightHilt = (int) targetX + 128;


		if (fromLeft) {
			int myleft = (int) getX() + 64;
			int myright = myleft + 64;

			if ((rightTip >= myleft) && (rightHilt <= myleft)) {
				gotten = true;
			}

		} else if (fromRight) {
			int myleft = (int) getX();
			int myright = myleft + 64;

			if ((leftTip <= myright) && (leftHilt >= myright)) {
				gotten = true;
			}
		} else gotten = false;

		if (gotten) {
			
			System.out.println("gotten");
			if (type.equals("rifleman")) {
				setAnimation(gotten_anim);
				gotten_anim.pause();
			}
			
			if (type.equals("screen")) {
				setAnimation(screen_torn_anim);
				screen_torn_anim.pause();
			}
			
			if (health < 1) die();

			health--;
		}
	}


	private void die() {
		dead = true;
		jump_dx = 40;
		hide();
		
		if (!die_sound_playing) {
			die_sound_playing = true;
			if (type.equals("rifleman")) {
				Sound swordmeat = new Sound("sound/swordmeat.wav");
				swordmeat.start();
			} else if (type.equals("screen")) {
				Sound rip = new Sound("sound/rip.wav");
				rip.start();
			}
		}
	}



	public void attackKatana() {
		if (dead) return;
	}
	
	
	public void update(long elapsed) {
		if (type.equals("rifleman")) {
			stand_anim.update(elapsed);
			crouch_anim.update(elapsed);
			gotten_anim.update(elapsed);
		} else if (type.equals("screen")) {
			screen_anim.update(elapsed);
			screen_torn_anim.update(elapsed);
		}

		if (dead) {	
			while (getY() < 800) {
				shiftY(jump_dx * -1);
				jump_dx -= WEIGHT;
			}
			//hide();
		}
	}

	public int getOwnMovement() {
		return ownMovement;
	}


	public void kill() {
		dead = true;
	}

	public boolean didYouHitMe() {
		return hit;
	}

	public void acknowledgeHit() {
		hit = false;
	}

	public boolean getStatus() {
		return dead;
	}
}
