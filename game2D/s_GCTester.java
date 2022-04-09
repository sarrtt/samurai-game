/*
 * ROADMAP
 *
 * Sword collisions / enemy's health system
 * Gunshots / hero's health system
 * Collisions in latter half of level
 * Redraw arms, chandeliers
 * Katana enemies (have one in 1st screen, one at the end)
 * Falling chandeliers
 * Train tilemap
 * Toho & title screen
 */

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class s_GCTester extends GameCore {

	// Physics
	int elapsed = 0;
	final int WALK_SPEED = 10;
	final int RUN_SPEED = 15;
	final int WEIGHT = 3;
	int jump_dx = 0; // when jumping, the player rises by this height every time.

	// Heights of things from the ground
	final int SEVEN_BLOCKS 	= 192;
	final int SIX_BLOCKS 	= 256; // e.g. chandeliers
	final int FIVE_BLOCKS 	= 320;
	final int FOUR_BLOCKS   = 384;
	final int THREE_BLOCKS 	= 448; // e.g. piano
	final int TWO_BLOCKS 	= 512; // e.g. people
	final int ZERO_BLOCKS 	= 640; // e.g. floor
	int floor = ZERO_BLOCKS;

	// The width of a block
	final int BLOCK = 64;

	// Positioning
	int yPos = ZERO_BLOCKS; //  the bottom of the player character sprite
	int indent = THREE_BLOCKS; // distance from left side of the screen
	int c = (indent + 64) * -1;  // collision factor; basically the character's right side
	int xPos = 0; // this is for the other things in the world besides the player!

	int level = 0;

	// These declarations record the left-hand sides of sprites; where to draw them in the world.
	// They remain constant; it is the addition of xPos which lets them move around.
	int chand1_left = BLOCK * 10;
	int piano_left = (BLOCK * 14) + 32;
	int rifleman1_left = 1537;
	int chand2_left = BLOCK * 19;
	int chand3_left = BLOCK * 26;
	
	// Controls
	boolean goLeft = false;		// a
	boolean goRight = false;	// d
	boolean run = false;		// shift + a or d
	boolean jump = false;		// space
	boolean attackl = false;	// h
	boolean attackr = false;	// l

	boolean mayGoLeft = true;
	boolean mayGoRight = true;
	boolean mayAscend = true;
	boolean mayDescend = false;

	Image bg;
	Image arch_img;
	
	Animation shife_anim;
	Animation legs_anim;
	Animation legs_idle_anim;
	Animation legs_jump_anim;

	Animation chandTop_anim;
	Animation rifleman_anim;
	Animation screen_anim;

	// Token animations for obstacles; collisions need sprites and sprites need animations
	Animation chand_anim;
	Animation balcony_anim;
	Animation wall_anim;
	Animation handrail_anim;
	Animation archtop_anim;
	Animation branch_anim;
	Animation lantern_anim;
		
	// Player
	Sprite torso_spr;
	Sprite larm_spr;
	Sprite rarm_spr;
	Sprite legs_spr;

	// NPCs
	Enemy rifleman_spr;
	
	// Collision obstacles
	Sprite piano_spr;
	FallingSprite chand1_spr;
	FallingSprite chand2_spr;
	FallingSprite chand3_spr;
	Sprite chandTop1_spr;
	Sprite chandTop2_spr;
	Sprite chandTop3_spr;
	Sprite balcony_spr;
	Sprite wall1_spr;
	Sprite wall2_spr;
	Sprite wall3_spr;
	Sprite handrail1_spr;
	Sprite handrail2_spr;
	Sprite screen_spr;
	Sprite archtop_spr;
	Sprite branch_spr;
	Sprite lantern_spr;

	TileMap rokumeikan = new TileMap();



	public static void main(String[] args) {
		s_GCTester gct = new s_GCTester();
		gct.init();
		gct.level1();
		gct.run(false,1024,768);
	}


	
	public void debug() {
	}

	


	public void init() {
		// Player
		shife_anim = new Animation();
		shife_anim.loadAnimationFromSheet("images/shife.png",2,1,1200);
		torso_spr = new Sprite(shife_anim);
		torso_spr.setX(indent);
		torso_spr.setY(yPos - 128);

		Image larm_img = loadImage("images/larm.png");
		Animation larm_anim = new Animation();
		larm_anim.addFrame(larm_img, 200);
		larm_spr = new Sprite(larm_anim);
		larm_spr.setX(indent - 128);
		larm_spr.setY(yPos - 128);
		
		Image rarm_img = loadImage("images/rarm.png");
		Animation rarm_anim = new Animation();
		rarm_anim.addFrame(rarm_img, 200);
		rarm_spr = new Sprite(rarm_anim);
		rarm_spr.setX(indent + 64);
		rarm_spr.setY(yPos - 128);
		
		legs_idle_anim = new Animation();
		legs_jump_anim = new Animation();
		legs_anim = new Animation();
		legs_idle_anim.loadAnimationFromSheet("images/legs_idle.png",2,1,600);
		legs_jump_anim.loadAnimationFromSheet("images/legs_jump.png",2,1,100);
		legs_anim.loadAnimationFromSheet("images/runsheet.png",4,1,200);
		legs_spr = new Sprite(legs_idle_anim);
		legs_spr.setX(indent);
		legs_spr.setY(yPos - 64);
    }



	public void level1() {
		level = 1;

		// Loading images and maps.
		bg = new ImageIcon("images/shinhanga.jpg").getImage();	
		rokumeikan.loadMap("maps", "rokumeikan.txt");


		// Enemies
		rifleman_anim = new Animation();
		rifleman_spr = new Enemy(rifleman_anim);


		// Collision objects
		Animation piano_anim = new Animation();
		piano_anim.addFrame(loadImage("images/piano.png"), 200);
		piano_spr = new Sprite(piano_anim);
		piano_spr.setX(piano_left);
		piano_spr.setY(THREE_BLOCKS);
		
		Animation chand_anim = new Animation();
		chand_anim.addFrame(loadImage("images/chand.png"), 200);
		chand1_spr = new FallingSprite(chand_anim);	chand1_spr.setX(chand1_left);	chand1_spr.setY(SIX_BLOCKS);
		chand2_spr = new FallingSprite(chand_anim);	chand2_spr.setX(chand2_left);	chand2_spr.setY(SIX_BLOCKS);
		chand3_spr = new FallingSprite(chand_anim);	chand3_spr.setX(chand3_left);	chand3_spr.setY(SIX_BLOCKS);

		Animation balcony_anim = new Animation();
		balcony_anim.addFrame(loadImage("images/balcony.png"), 200);
		balcony_spr = new Sprite(balcony_anim);
		balcony_spr.setX(33 * BLOCK); balcony_spr.setY(SIX_BLOCKS);

		Animation wall_anim = new Animation();
		wall_anim.addFrame(loadImage("images/wall.png"), 200);
		wall1_spr = new Sprite(wall_anim);
		wall1_spr.setX(35 * BLOCK); wall1_spr.setY(SIX_BLOCKS);
		wall2_spr = new Sprite(wall_anim);
		wall2_spr.setX(35 * BLOCK); wall2_spr.setY(-128);
		wall3_spr = new Sprite(wall_anim);
		wall3_spr.setX(62 * BLOCK); wall3_spr.setY(SIX_BLOCKS);

		Animation handrailin_anim = new Animation();
		Animation handrailout_anim = new Animation();
		handrailin_anim.addFrame(loadImage("images/handrailin.png"), 200);
		handrailout_anim.addFrame(loadImage("images/handrailout.png"), 200);
		handrail1_spr = new Sprite(handrailin_anim);
		handrail1_spr.setX((33 * BLOCK) + 16); handrail1_spr.setY(SEVEN_BLOCKS);
		handrail2_spr = new Sprite(handrailout_anim);
		handrail2_spr.setX((39 * BLOCK) + 16); handrail2_spr.setY(SEVEN_BLOCKS);

		Animation branch_anim = new Animation();
		branch_anim.addFrame(loadImage("images/branch.png"), 200);
		branch_spr = new Sprite(branch_anim);
		branch_spr.setX(BLOCK * 52); branch_spr.setY(FIVE_BLOCKS);
		
		Animation lantern_anim = new Animation();
		lantern_anim.addFrame(loadImage("images/lantern.png"), 200);
		lantern_spr = new Sprite(lantern_anim);
		lantern_spr.setX(BLOCK * 49); lantern_spr.setY(TWO_BLOCKS);
		
		Animation archtop_anim = new Animation();
		archtop_anim.addFrame(loadImage("images/archTop.png"), 200);
		archtop_spr = new Sprite(archtop_anim);
		archtop_spr.setX(BLOCK * 54); archtop_spr.setY(SEVEN_BLOCKS);

		// Interactable objects
		Animation chandTop_anim = new Animation();
		chandTop_anim.addFrame(loadImage("images/chandTop.png"), 200);
		chandTop1_spr = new Sprite(chandTop_anim);	chandTop1_spr.setX(chand1_left + BLOCK);	chandTop1_spr.setY(0);
		chandTop2_spr = new Sprite(chandTop_anim);	chandTop2_spr.setX(chand2_left + BLOCK);	chandTop2_spr.setY(0);
		chandTop3_spr = new Sprite(chandTop_anim);	chandTop3_spr.setX(chand3_left + BLOCK);	chandTop3_spr.setY(0);

		Animation screen_anim = new Animation();
		screen_anim.addFrame(loadImage("images/screen.png"), 200);
		screen_spr = new Sprite(screen_anim);
		screen_spr.setX(34 * BLOCK); screen_spr.setY(TWO_BLOCKS);

		Image arch_img = new ImageIcon("images/arch.png").getImage();
	}



	public void draw(Graphics2D g) {
		Color black = new Color(0,0,0);
		g.setColor(black);
		g.fillRect(0,0,getWidth(),getHeight());
		g.drawImage(bg,(-xPos / 32),0,null);
		g.fillRect((-xPos - 8),0,8,getHeight());
		
		rokumeikan.draw(g,-xPos,0);

		if (level == 1) {
			// Obstacles
			g.drawImage(piano_spr.getImage(), piano_left - xPos, THREE_BLOCKS, null);
			g.drawImage(chand1_spr.getImage(), (int) chand1_left - xPos, (int) chand1_spr.getY(), null);
			g.drawImage(chand2_spr.getImage(), (int) chand2_left - xPos, (int) chand2_spr.getY(), null);
			g.drawImage(chand3_spr.getImage(), (int) chand3_left - xPos, (int) chand3_spr.getY(), null);
			g.drawImage(wall1_spr.getImage(), (int) wall1_spr.getX() - xPos, SIX_BLOCKS, null);
			g.drawImage(wall2_spr.getImage(), (int) wall2_spr.getX() - xPos, -128, null);
			g.drawImage(balcony_spr.getImage(), (int) balcony_spr.getX() - xPos, SIX_BLOCKS, null);
			g.drawImage(handrail1_spr.getImage(), (int) handrail1_spr.getX() - xPos, (int) handrail1_spr.getY(), null);
			g.drawImage(handrail2_spr.getImage(), (int) handrail2_spr.getX() - xPos, (int) handrail2_spr.getY(), null);
			g.drawImage(archtop_spr.getImage(), (int) archtop_spr.getX() - xPos, (int) archtop_spr.getY(), null);
			g.drawImage(branch_spr.getImage(), (int) branch_spr.getX() - xPos, (int) branch_spr.getY(), null);
			g.drawImage(lantern_spr.getImage(), (int) lantern_spr.getX() - xPos, (int) lantern_spr.getY(), null);

			wall3_spr.setScale(2.0f);
			wall3_spr.setX((62 * BLOCK) - xPos);
			wall3_spr.drawTransformed(g);

			// Interactable objects
			g.drawImage(chandTop1_spr.getImage(), chand1_left - xPos + BLOCK, 0, null);
			g.drawImage(chandTop2_spr.getImage(), chand2_left - xPos + BLOCK, 0, null);
			g.drawImage(chandTop3_spr.getImage(), chand3_left - xPos + BLOCK, 0, null);
			g.drawImage(screen_spr.getImage(), (34 * BLOCK) - xPos, TWO_BLOCKS, null);
			
			// Misc objects
			g.drawImage(arch_img, (BLOCK * 54 - xPos), SIX_BLOCKS, null);
		
			// Enemies
			rifleman_spr.setX(rifleman1_left - xPos + rifleman_spr.getOwnMovement());
			rifleman_spr.setY(TWO_BLOCKS);
			if (torso_spr.getX() < rifleman_spr.getX()) {
				g.drawImage(rifleman_spr.getImage(), (int) rifleman_spr.getX(), (int) rifleman_spr.getY(), null);
			} else {
				rifleman_spr.setScale(-1.0f, 1.0f);
				rifleman_spr.drawTransformed(g);
			}
		}


		// Player
		g.drawImage(torso_spr.getImage(), (int) torso_spr.getX(), (int) torso_spr.getY(), null);
		if (attackl) g.drawImage(larm_spr.getImage(), (int) larm_spr.getX(), (int) larm_spr.getY(), null);
		if (attackr) g.drawImage(rarm_spr.getImage(), (int) rarm_spr.getX(), (int) rarm_spr.getY(), null);
		


		if (run) { 
			legs_anim.setAnimationSpeed(2);
		} else {
			legs_anim.setAnimationSpeed(1);
		}
		
		if (goLeft) {
			legs_spr.setAnimation(legs_anim);
			legs_spr.setScale(-1.0f, 1.0f);
			legs_spr.setX(indent);
			legs_spr.setY(yPos - 64);
			legs_spr.drawTransformed(g);
		} else if (goRight) {
			legs_spr.setAnimation(legs_anim);
			legs_spr.setScale(1.0f, 1.0f);
			legs_spr.setX(indent);
			legs_spr.setY(yPos - 64);
			legs_spr.drawTransformed(g);
		} else if (jump_dx > 0 && mayDescend) {
			legs_spr.setAnimation(legs_jump_anim);
			g.drawImage(legs_spr.getImage(), indent, (yPos - 64), null);
		} else {
			legs_spr.setAnimation(legs_idle_anim);
			g.drawImage(legs_spr.getImage(), indent, (yPos - 64), null);
		}
	}



	public void update(long elapsed) {
		elapsed++;
		debug();


		// Walking and running
		if (indent <= 8) mayGoLeft = false;
		if (indent >= (64 * 64) - (1024 - indent)) mayGoRight = false;
		if (mayGoLeft) {
			if ((xPos < 0) || (indent > THREE_BLOCKS)) {
				if (goLeft && !run) {
					indent = indent - WALK_SPEED;
					torso_spr.shiftX(-WALK_SPEED);
					larm_spr.shiftX(-WALK_SPEED);
					rarm_spr.shiftX(-WALK_SPEED);
				}
				if (goLeft && run) {
					indent = indent - RUN_SPEED;
					torso_spr.shiftX(-RUN_SPEED);
					larm_spr.shiftX(-RUN_SPEED);
					rarm_spr.shiftX(-RUN_SPEED);
				}
			} else {
				if (goLeft && !run) {
					xPos = xPos - WALK_SPEED;
				//	torso_spr.shiftX(-WALK_SPEED);
				}
				if (goLeft && run) {
					xPos = xPos - RUN_SPEED;
				//	torso_spr.shiftX(-RUN_SPEED);
				}
			}
		}
		
		if (mayGoRight) {
			if ((indent < THREE_BLOCKS) || (xPos >= rokumeikan.getPixelWidth() - 1024)) {
				if (goRight && !run) {
					indent = indent + WALK_SPEED;
					torso_spr.shiftX(WALK_SPEED);
					larm_spr.shiftX(WALK_SPEED);
					rarm_spr.shiftX(WALK_SPEED);
				}
				if (goRight && run) {	
					indent = indent + RUN_SPEED;
					torso_spr.shiftX(RUN_SPEED);
					larm_spr.shiftX(RUN_SPEED);
					rarm_spr.shiftX(RUN_SPEED);
				}
			} else {
				if (goRight && !run) {
					xPos = xPos + WALK_SPEED;
				//	torso_spr.shiftX(WALK_SPEED); 
				}
				if (goRight && run) {	
					xPos = xPos + RUN_SPEED;
				//	torso_spr.shiftX(RUN_SPEED);
				}
			}	
		}



		// Jumping and falling
		if ((jump) && (yPos % 64 == 0)) {
			jump_dx = 40;
			mayDescend = true;
		}
		
		if (mayDescend) {
			yPos -= jump_dx;
			torso_spr.shiftY(-jump_dx);
			larm_spr.shiftY(-jump_dx);
			rarm_spr.shiftY(-jump_dx);
			jump_dx -= WEIGHT;
		} else {
			jump_dx = 0;
		}
	


		// Combat
		rifleman_spr.attackChassepot(torso_spr.getX(), torso_spr.getY());
		rifleman_spr.getGot(torso_spr.getX(), torso_spr.getY(), attackl, attackr);


		// Collisions
		if (yPos > floor) {
			yPos = floor;
			torso_spr.setY(floor - (BLOCK * 2));
			larm_spr.setY(floor - (BLOCK * 2));
			rarm_spr.setY(floor - (BLOCK * 2));
			mayDescend = false;
			jump = false;
		}
		
		if (level == 1) {
			collision(piano_spr, false);
			collision(chand1_spr, true);
			collision(chand2_spr, true);
			collision(chand3_spr, true);
			collision(balcony_spr, true);
			collision(wall1_spr, true);
			collision(wall2_spr, true);
			//collision(wall3_spr, false);
			collision(handrail1_spr, false);
			collision(handrail2_spr, false);
			collision(screen_spr, false);	
			collision(archtop_spr, true);
			collision(branch_spr, false);
			collision(lantern_spr, false);
		}

		// Precursor to the above function:
		/*if ((xPos >= piano_left + c) && (xPos <= piano_right + c)) {
			if (yPos > THREE_BLOCKS) {
				if (xPos < piano_right - 64 + c) {
					xPos = piano_left + c;
					mayGoRight = false;
				} else {
					xPos = piano_right + c;
					mayGoLeft = false;
				}
			} else {
				mayGoLeft = true;
				mayGoRight = true;
				floor = THREE_BLOCKS;
			}
		} else if ((xPos >= chand2_left + c) && (xPos <= chand2_right + c)) {
			if ((yPos > SIX_BLOCKS) && (yPos < FIVE_BLOCKS)) {
				if (xPos < chand2_right - 64 + c) {
					xPos = chand2_left + c;
					mayGoRight = false;
				} else {
					xPos = chand2_right + c;
					mayGoLeft = false;
				}
			} else {
				mayGoLeft = true;
				mayGoRight = true;
				if (yPos > SIX_BLOCKS) {
					floor = ZERO_BLOCKS;
				} else {
					floor = SIX_BLOCKS;
				}

			}
		} else { // "else if" statements would continue for every sprite with collision detection; collisions existed independently from sprites themselves.
				mayGoLeft = true;
				mayGoRight = true;
				floor = ZERO_BLOCKS;
		}
	
	*/
	
		// Update animations
		shife_anim.update(elapsed);
		legs_idle_anim.update(elapsed);
		legs_jump_anim.update(elapsed);
		legs_anim.update(elapsed);
		if (!rifleman_spr.getStatus()) rifleman_spr.update(elapsed);
	}



	public void collision(Sprite spr, boolean hasBottom) {

		int left = (int) spr.getX() + c;
		int right = left + (int) spr.getImage().getWidth(null) + BLOCK;
		int top = (int) spr.getY();
		int bottom = top + (int) spr.getImage().getHeight(null) + BLOCK;

		boolean inX;
		boolean inY;

		if ((xPos > left) && (xPos < right)) {
			inX = true;
		} else {
			inX = false;
		}

		if ((yPos > top) && (yPos < bottom)) {
			inY = true;
		} else {
			inY = false;
		}


		if (!inY) floor = ZERO_BLOCKS;

		if ((inX) && (yPos > bottom - 48) && (yPos <= bottom) && hasBottom) {
			int distL = xPos - left;
			int distR = right - xPos;


			// Which edge of the rectangle is closest to the player?
			// Move the player to whichever edge, and disable colliding further.
			if ((distL > 32) && (distR > 32)) {
				yPos = bottom + BLOCK + 1;
				torso_spr.setY(bottom - BLOCK + 1);
				larm_spr.setY(bottom - BLOCK + 1);
				rarm_spr.setY(bottom - BLOCK + 1);
				jump = false;
				jump_dx = 0;
				mayAscend = false;
			} else if (distL < distR) {
				xPos = left;
				mayGoRight = false;
			} else if (distR < distL) {
				xPos = right;
				mayGoLeft = false;
			}
		} else if ((inX) && (yPos == top)) {
			floor = top;
			yPos = top;
			jump = false;
			torso_spr.setY(top - (BLOCK * 2));
			larm_spr.setY(top - (BLOCK * 2));
			rarm_spr.setY(top - (BLOCK * 2));
			jump_dx = 0;
			mayDescend = false;
		} else if ((inX) && (inY)) {
			//jump_dx = 0;
			int distL = xPos - left;
			int distR = right - xPos;
			int distT = yPos - top;
			int distB = bottom - yPos;

			// Which edge of the rectangle is closest to the player?
			// Move the player to whichever edge, and disable colliding further.
			if ((distL < distR) && (distL < distT) && (distL < distB)) {  // towards the left
				xPos = left;
				mayGoRight = false;
			} else if ((distR < distL) && (distR < distT) && (distR < distB)) { // towards the right
				xPos = right;
				mayGoLeft = false;
			} else if ((distT < distL) && (distT < distR) && (distT < distB)) { // towards the top
				yPos = top;
				floor = top;
				jump = false;
				torso_spr.setY(top - (BLOCK * 2));
				larm_spr.setY(top - (BLOCK * 2));
				rarm_spr.setY(top - (BLOCK * 2));
				jump_dx = 0;
				mayDescend = false;
			} else if ((distB < distL) && (distB < distR) && (distB < distT) && hasBottom) { // towards the bottom
				yPos = bottom;
				torso_spr.setY(bottom - TWO_BLOCKS);
				larm_spr.setY(bottom - TWO_BLOCKS);
				rarm_spr.setY(bottom - TWO_BLOCKS);
				jump = false;
				jump_dx = 0;
				mayAscend = false;
			}
		}

		if ((xPos <= left + 10) || (xPos >= right - 10)) {
			floor = ZERO_BLOCKS;
			mayDescend = true;
		}

		// Missing the object
		if (xPos < left    || !inY) mayGoRight = true;
		if (xPos > right   || !inY) mayGoLeft = true;
		if (yPos < top    || !inX) mayDescend = true;
		if ((yPos > bottom || !inX) || !hasBottom) mayAscend = true;
		
	}



	public void keyPressed(KeyEvent e) { 
		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = true;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = true;
		if (e.isShiftDown()) run = true;
		if (e.getKeyCode() == KeyEvent.VK_SPACE) jump = true; 
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.out.println("Thanks for playing!");
			stop();
		}

		// Combat
		if ((e.getKeyCode() == KeyEvent.VK_H) && (!attackl) && (!attackr)) attackl = true;
		if ((e.getKeyCode() == KeyEvent.VK_L) && (!attackl) && (!attackr)) attackr = true;
	}



	public void keyReleased(KeyEvent e) {
		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = false;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = false;
		if (!e.isShiftDown()) run = false;
		
		// Combat
		if (e.getKeyCode() == KeyEvent.VK_H) attackl = false;
		if (e.getKeyCode() == KeyEvent.VK_L) attackr = false;
	}
}
