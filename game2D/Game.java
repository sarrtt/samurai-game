//package game2D;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * THE SAMURAI
 * @author 2721415
 */
@SuppressWarnings("serial")

public class Game extends GameCore {

	static int screenWidth = 1024;
	static int screenHeight = 768;

	int elapsed = 0;
	int level = 0;


	// Positioning
	
	final int BLOCK = 64; // the width of my tiles
	
	// Heights of things from the ground
	final int SEVEN_BLOCKS 	= 192;
	final int SIX_BLOCKS 	= 256; // e.g. chandeliers
	final int FIVE_BLOCKS 	= 320;
	final int FOUR_BLOCKS   = 384;
	final int THREE_BLOCKS 	= 448; // e.g. piano
	final int TWO_BLOCKS 	= 512; // e.g. people
	final int ZERO_BLOCKS 	= 640; // e.g. floor
	int floor = ZERO_BLOCKS;

	// Collisions coping with relative movement
	int yPos = ZERO_BLOCKS; 	// the bottom of the player character sprite
	int indent = 448; 		// player's distance from left side of the screen
	int c = (indent + 64) * -1;  	// collision factor, or the player's right side
	int xPos = 0; 			// this is for the other things in the world besides the player!
	int leftmost;			// protag cannot walk left of this point, or he might get run over by a train
	int rightmost;			// protag has won by reaching this rightmost point of the stage

	// These declarations record the left-hand sides of sprites within the world.
	// They remain constant; it is the addition of xPos which lets them move around.
	int piano_left = (BLOCK * 14) + 32;
	int rifleman1_left = 1537;
	int rifleman2_left = BLOCK * 58;
	int screen_left = BLOCK * 34;


	// Player attributes
	int hp = 4;
	final int WALK_SPEED = 10;
	final int RUN_SPEED = 15;
	final int WEIGHT = 3;
	int jump_dx = 0; // when jumping, the player rises by this height every time.


	// Boolean flags
	boolean win = false;
	boolean lose = false;
	boolean musicPlaying = false;
	
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


	// Resources
	
	Image bg_ballroom;
	Image bg_train;
	Image owari;
	Image select_ballroom;
	Image select_train;
	
	// Characters
	Animation shife_anim;		// Character's torso at 4 HP,
	Animation hitonce_anim;		// 3 HP,
	Animation hittwice_anim;	// 2 HP,
	Animation hitthrice_anim;	// 1 HP,
	Animation down_anim;		// and his death animation.
	Animation legs_anim;		// Walking/running
	Animation legs_idle_anim;	// Standing, tapping foot
	Animation legs_jump_anim;
	Sprite torso_spr;
	Sprite larm_spr;
	Sprite rarm_spr;
	Sprite legs_spr;
	
	Animation rifleman_anim;
	Enemy rifleman_spr;
	Enemy rifleman2_spr;

	// Level 1: Ballroom
	Animation chand_anim;
	Animation chandTop_anim;
	Animation screen_anim;
	ArrayList<Sprite> chandeliers = new ArrayList<Sprite>();
	ArrayList<Sprite> chandelierTops = new ArrayList<Sprite>();
	Sprite piano_spr;

	TileMap ballroom_map = new TileMap();
	TileMap ballroom_collisions = new TileMap();
	
	// Level 2: Train

	TileMap train_map = new TileMap();
	TileMap tracks_map = new TileMap();
	TileMap train_collisions = new TileMap();



	// Token animations for obstacles; collisions need sprites and sprites need animations
	Animation balcony_anim;
	Animation wall_anim;
	Animation wallbig_anim;
	Animation handrail_anim;
	Animation branch1_anim;
	Animation branch2_anim;
		
	// Collision obstacles
	Sprite balcony_spr;
	Sprite wall1_spr;
	Sprite wall2_spr;
	Sprite wall3_spr;
	Sprite handrail1_spr;
	Sprite handrail2_spr;
	Sprite branch1_spr;
	Sprite branch2_spr;
	Enemy screen_spr;

	TileMap thisLevel;



	public static void main(String[] args) {
		Game gct = new Game();
		gct.init_load();
		gct.run(false, screenWidth, screenHeight);
	}


	
	public void debug() {
	}

	


	public void init_load () {
		System.out.println("Loading...");

		Sprite s;

		// Player

		// Torso and head: indicates damage
		shife_anim = new Animation();
		shife_anim.loadAnimationFromSheet("images/protag_hp4.png",2,1,1200);
		hitonce_anim = new Animation();
		hitonce_anim.loadAnimationFromSheet("images/protag_hp3.png",2,1,1200);
		hittwice_anim = new Animation();
		hittwice_anim.loadAnimationFromSheet("images/protag_hp2.png",2,1,1200);
		hitthrice_anim = new Animation();
		hitthrice_anim.loadAnimationFromSheet("images/protag_hp1.png",2,1,1200);
		torso_spr = new Sprite(shife_anim);

		// Arms and sword: for combat
		Image larm_img = loadImage("images/protag_arml.png");
		Animation larm_anim = new Animation();
		larm_anim.addFrame(larm_img, 200);
		larm_spr = new Sprite(larm_anim);
		
		Image rarm_img = loadImage("images/protag_armr.png");
		Animation rarm_anim = new Animation();
		rarm_anim.addFrame(rarm_img, 200);
		rarm_spr = new Sprite(rarm_anim);

		// Legs: indicates movement.  Also sole sprite for laying KO'd	
		legs_idle_anim = new Animation();
		legs_idle_anim.loadAnimationFromSheet("images/protag_legs_idle.png",2,1,600);
		legs_anim = new Animation();
		legs_anim.loadAnimationFromSheet("images/protag_legs_run.png",4,1,200);
		legs_jump_anim = new Animation();
		legs_jump_anim.loadAnimationFromSheet("images/protag_legs_jump.png",2,1,100);
		down_anim = new Animation();
		down_anim.loadAnimationFromSheet("images/protag_legs_dead.png",2,1,1200);
		legs_spr = new Sprite(legs_idle_anim);



		// Level select menu
		select_ballroom = new ImageIcon("images/select_ballroom.jpg").getImage();
		select_train = new ImageIcon("images/select_train.jpg").getImage();

		// Ballroom
		bg_ballroom = new ImageIcon("images/bg_ballroom.jpg").getImage();	
		ballroom_map.loadMap("maps", "rokumeikan.txt");
		ballroom_collisions.loadMap("maps", "lv1col.txt");
		
		rifleman_anim = new Animation();
		rifleman_spr = new Enemy(rifleman_anim, "rifleman");
		rifleman2_spr = new Enemy(rifleman_anim, "rifleman");

		Animation chand_anim = new Animation();
		Animation chandTop_anim = new Animation();
		chand_anim.addFrame(loadImage("images/ballroom_chandelier.png"), 200);
		chandTop_anim.addFrame(loadImage("images/ballroom_chandelierCord.png"), 200);
		for (int i = 0; i < 4; i++) {	
			s = new Sprite(chand_anim);
			s.show();
			chandeliers.add(s);
		}	
		for (int i = 0; i < 4; i++) {	
			s = new Sprite(chandTop_anim);
			s.show();
			chandelierTops.add(s);	
		}

		Animation piano_anim = new Animation();
		piano_anim.addFrame(loadImage("images/ballroom_piano.png"), 200);
		piano_spr = new Sprite(piano_anim);
		
		Animation balcony_anim = new Animation();
		balcony_anim.addFrame(loadImage("images/ballroom_balcony.png"), 200);
		balcony_spr = new Sprite(balcony_anim);

		Animation wall_anim = new Animation();
		Animation wallbig_anim = new Animation();
		wall_anim.addFrame(loadImage("images/ballroom_wall.png"), 200);
		wallbig_anim.addFrame(loadImage("images/ballroom_wall_xl.png"), 200);
		wall1_spr = new Sprite(wall_anim);
		wall2_spr = new Sprite(wall_anim);
		wall3_spr = new Sprite(wallbig_anim);

		Animation handrailin_anim = new Animation();
		Animation handrailout_anim = new Animation();
		handrailin_anim.addFrame(loadImage("images/ballroom_handrail_in.png"), 200);
		handrailout_anim.addFrame(loadImage("images/ballroom_handrail_out.png"), 200);
		handrail1_spr = new Sprite(handrailin_anim);
		handrail2_spr = new Sprite(handrailout_anim);

		Animation branch1_anim = new Animation();
		branch1_anim.addFrame(loadImage("images/garden_branch1.png"), 200);
		branch1_spr = new Sprite(branch1_anim);

		Animation branch2_anim = new Animation();
		branch2_anim.addFrame(loadImage("images/garden_branch2.png"), 200);
		branch2_spr = new Sprite(branch2_anim);
		
		screen_anim = new Animation();
		screen_spr = new Enemy (screen_anim, "screen");

		// Train
		bg_train = new ImageIcon("images/bg_train.jpg").getImage();	
		train_map.loadMap("maps", "train.txt");
		tracks_map.loadMap("maps", "tracks.txt");
		train_collisions.loadMap("maps", "lv2col.txt");

		System.out.println("Loaded!");


		init_game(0);
	}


	
	public void init_game(int select) {
		win = false;
		lose = false;
		hp = 4;
		
		floor = ZERO_BLOCKS;
		if (select == 2) floor = TWO_BLOCKS;
		xPos = 0;
		yPos = floor;
		indent = 448;
		c = (indent + 64) * -1;
		
		mayGoLeft = true;
		mayGoRight = true;
		mayAscend = true;
		
		// Arranging protag's body parts
		torso_spr.setX(indent);
		torso_spr.setY(yPos - 128);
		torso_spr.setAnimation(shife_anim);
		larm_spr.setX(indent - 128);
		larm_spr.setY(yPos - 128);
		rarm_spr.setX(indent + 64);
		rarm_spr.setY(yPos - 128);
		legs_spr.setX(indent);
		legs_spr.setY(yPos - 64);

		// Initialising levels
		if (select == 0) {
			level = 0;

			indent = (screenWidth / 2) - 32;		
		} else if (select == 1) {
			level = 1;

			int j = 448;
			for (Sprite s: chandeliers) {
				int i = chandeliers.indexOf(s);
				if (i <= 1) s.setX(192 + (i * j));
				if (i > 1) s.setX(192 + 128 + (i * j));
				s.setY(SIX_BLOCKS);
			}	
			for (Sprite s: chandelierTops) {
				int i = chandelierTops.indexOf(s);
				if (i <= 1) s.setX(192 + (i * j) + 64);
				if (i > 1) s.setX(192 + 128 + (i * j) + 64);
				s.setY(0);
			}	

			piano_spr.setX(piano_left);		piano_spr.setY(THREE_BLOCKS);
			balcony_spr.setX(33 * BLOCK); 		balcony_spr.setY(SIX_BLOCKS);
			wall1_spr.setX(35 * BLOCK); 		wall1_spr.setY(SIX_BLOCKS);
			wall2_spr.setX(35 * BLOCK); 		wall2_spr.setY(-128);
			wall3_spr.setX(62 * BLOCK); 		wall3_spr.setY(SIX_BLOCKS);
			handrail1_spr.setX((33 * BLOCK) + 16); 	handrail1_spr.setY(SEVEN_BLOCKS);
			handrail2_spr.setX((39 * BLOCK) + 16); 	handrail2_spr.setY(SEVEN_BLOCKS);
			branch1_spr.setX(BLOCK * 54); 		branch1_spr.setY(FOUR_BLOCKS);
			branch2_spr.setX(BLOCK * 56); 		branch2_spr.setY(FIVE_BLOCKS);
			screen_spr.setX(screen_left);


			// Starts the level's soundtrack
			try { if (!musicPlaying) soundtrack(level); } catch (Exception e) { System.out.println("MIDI error!"); }
		} else if (select == 2) {
			level = 2;

			floor = TWO_BLOCKS;
			yPos = floor;

			// Starts the level's soundtrack
			try { if (!musicPlaying) soundtrack(level); } catch (Exception e) { System.out.println("MIDI error!"); }
		}
		
	}



	private void soundtrack (int select) throws InvalidMidiDataException, IOException, MidiUnavailableException {
		Sequence debussy = MidiSystem.getSequence(new File("sound/debussy.mid")); // Debussy's Suite Bergmasque #4
		Sequence saintsaens = MidiSystem.getSequence(new File("sound/saintsaens.mid"));  // Saint-Saens' Concerto in G Minor #3
		
		Sequencer sqr = MidiSystem.getSequencer();
		sqr.open();
		
		if (select == 1) sqr.setSequence(debussy);
		if (select == 2) sqr.setSequence(saintsaens);

		sqr.start();
		musicPlaying = true;
	}



	public void draw(Graphics2D g) {
		Color black = new Color(0,0,0);
		g.setColor(black);
		g.fillRect(0,0,getWidth(),getHeight());
		
		// Backgrounds
		if (level == 0) { // Level select menu
			// A picture representing each level
			g.drawImage(select_ballroom, 0, 0, null);
			g.drawImage(select_train, 512, 0, null);
		} else if (level == 1) { // Ballroom
			g.drawImage(bg_ballroom, (-xPos / 32), 0, null);
			g.fillRect((-xPos - 8),0,8,getHeight());

			// Tilemaps
			ballroom_collisions.draw(g,-xPos,0);
			ballroom_map.draw(g,-xPos,0);
			
			// Obstacles			
			for (Sprite s: chandeliers) g.drawImage(s.getImage(), (int) s.getX() - xPos, (int) s.getY(), null);
			for (Sprite s: chandelierTops) g.drawImage(s.getImage(), (int) s.getX() - xPos, (int) s.getY(), null);

			g.drawImage(piano_spr.getImage(), piano_left - xPos, THREE_BLOCKS, null);
			g.drawImage(wall1_spr.getImage(), (int) wall1_spr.getX() - xPos, SIX_BLOCKS, null);
			g.drawImage(wall2_spr.getImage(), (int) wall2_spr.getX() - xPos, -128, null);
			g.drawImage(wall3_spr.getImage(), (int) wall3_spr.getX() - xPos, (int) wall3_spr.getY(), null);
			g.drawImage(balcony_spr.getImage(), (int) balcony_spr.getX() - xPos, SIX_BLOCKS, null);
			g.drawImage(handrail1_spr.getImage(), (int) handrail1_spr.getX() - xPos, (int) handrail1_spr.getY(), null);
			g.drawImage(handrail2_spr.getImage(), (int) handrail2_spr.getX() - xPos, (int) handrail2_spr.getY(), null);
			g.drawImage(branch1_spr.getImage(), (int) branch1_spr.getX() - xPos, (int) branch1_spr.getY(), null);
			g.drawImage(branch2_spr.getImage(), (int) branch2_spr.getX() - xPos, (int) branch2_spr.getY(), null);
		} else if (level == 2) { // Train
			g.drawImage(bg_train, -(xPos / 64), 0, null);

			train_collisions.draw(g,-xPos, 0);
			train_map.draw(g,-xPos, 0);
			tracks_map.draw(g,0,0);
		}


		// Player
		if (!lose) {
			g.drawImage(torso_spr.getImage(), (int) torso_spr.getX(), (int) torso_spr.getY(), null);
			if (attackl && mayGoLeft) g.drawImage(larm_spr.getImage(), (int) larm_spr.getX(), (int) larm_spr.getY(), null);
			if (attackr && mayGoRight) g.drawImage(rarm_spr.getImage(), (int) rarm_spr.getX(), (int) rarm_spr.getY(), null);
	
	
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
		} else {
			legs_spr.setAnimation(down_anim);
			g.drawImage(legs_spr.getImage(), indent, (yPos - 64), null);
		}



		// Foregrounds i.e. things to stick your sword into
		if (level == 1) {	
			if (!screen_spr.getStatus()) {
				g.drawImage(screen_spr.getImage(), (int) screen_spr.getX() - xPos, TWO_BLOCKS, null);
			}
			
			if (!rifleman_spr.getStatus()) {
				rifleman_spr.setX(rifleman1_left - xPos + rifleman_spr.getOwnMovement());
				if (torso_spr.getX() < rifleman_spr.getX()) {
					g.drawImage(rifleman_spr.getImage(), (int) rifleman_spr.getX(), (int) rifleman_spr.getY(), null);
				} else {
					rifleman_spr.setScale(-1.0f, 1.0f);
					rifleman_spr.drawTransformed(g);
				}
			}
			
			if (!rifleman2_spr.getStatus()) {
				rifleman2_spr.setX(rifleman2_left - xPos + rifleman2_spr.getOwnMovement());
				if (torso_spr.getX() < rifleman2_spr.getX()) {
					g.drawImage(rifleman2_spr.getImage(), (int) rifleman2_spr.getX(), (int) rifleman2_spr.getY(), null);
				} else {
					rifleman2_spr.setScale(-1.0f, 1.0f);
					rifleman2_spr.drawTransformed(g);
				}
			}
		}
		


		// Front: overlays
		if (win || lose) {
			Image owari = new ImageIcon("images/sub_owari.png").getImage();
			g.drawImage(owari, 0, 0, null);
		}
	}



	public void update(long elapsed) {
		elapsed++;
		debug();

		if (level == 1) {
			thisLevel = ballroom_map;
			leftmost = 64;
		} else if (level == 2) {
			thisLevel = train_map;
			leftmost = 256;
		}


		// Movement

	
		if ((jump) && (yPos % 64 == 0)) { // Not a safe way of doing things..
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
		
		// You can't move when the game is over (primitive cutscenes) and movement works differently on level 0, the single-screen menu.
		if (!isTheGameOver() && level != 0) {
			rightmost = thisLevel.getPixelWidth() - screenWidth;

			// Reaching the extremes of the level.  Of course, the start treats you differently compared to the goal.
			if (indent <= leftmost) {
				mayGoLeft = false;
			} else {
				mayGoLeft = true;
			}
			if (xPos >= rightmost) win();

			if (mayGoLeft) {
				if ((xPos < 0) || (indent > THREE_BLOCKS)) { // The protag is approaching the left side of the screen; he will move, not scroll the world.
					if (goLeft && !run) {
						indent -= WALK_SPEED;
						torso_spr.shiftX(-WALK_SPEED);
						larm_spr.shiftX(-WALK_SPEED);
						rarm_spr.shiftX(-WALK_SPEED);
					}
					if (goLeft && run) {
						indent -= RUN_SPEED;
						torso_spr.shiftX(-RUN_SPEED);
						larm_spr.shiftX(-RUN_SPEED);
						rarm_spr.shiftX(-RUN_SPEED);
					}
				} else { // Ordinary world scrolling.
					if (goLeft && !run) xPos -= WALK_SPEED;
					if (goLeft && run)  xPos -= RUN_SPEED;
				}
			}

			if (mayGoRight) {
				if (indent < THREE_BLOCKS) {
					if (goRight && !run) {
						indent += WALK_SPEED;
						torso_spr.shiftX(WALK_SPEED);
						larm_spr.shiftX(WALK_SPEED);
						rarm_spr.shiftX(WALK_SPEED);
					}
					if (goRight && run) {
						indent += RUN_SPEED;
						torso_spr.shiftX(RUN_SPEED);
						larm_spr.shiftX(RUN_SPEED);
						rarm_spr.shiftX(RUN_SPEED);
					}
				} else { 
					if (goRight && !run) xPos += WALK_SPEED;
					if (goRight && run)  xPos += RUN_SPEED;
				}
			}
	
		}

		// Movement within the level selection screen is limited.
		if (level == 0) {
			if (mayGoLeft) {
				if (goLeft && !run) {
					indent = indent - WALK_SPEED;
				}
				if (goLeft && run) {
					indent = indent - RUN_SPEED;
				}
			}
			
			if (mayGoRight) {
				if (goRight && !run) {
					indent = indent + WALK_SPEED;
				}
				if (goRight && run) {	
					indent = indent + RUN_SPEED;
				}
			}

			torso_spr.setX(indent);
			legs_spr.setX(indent);
			larm_spr.setX(indent - 128);
			rarm_spr.setX(indent + 64);

			if (indent <= 192) init_game(1);
			if (indent >= (screenWidth - 192)) init_game(2);
		}



		// When you win, the protag runs off the screen on his own.
		if (win) {
			indent = indent + RUN_SPEED;
			torso_spr.shiftX(RUN_SPEED);
			larm_spr.shiftX(RUN_SPEED);
			rarm_spr.shiftX(RUN_SPEED);
		}

	

		// Enemy AI
		if (level == 1) {
			// The enemy attacks when he's within two screen widths of the player, whether approaching from the left or the right.
			if ((Math.abs(torso_spr.getX() - rifleman_spr.getX())) < (screenWidth * 2)) rifleman_spr.attackChassepot(torso_spr.getX(), (float) yPos);
			if ((Math.abs(torso_spr.getX() - rifleman2_spr.getX())) < (screenWidth * 2)) rifleman2_spr.attackChassepot(torso_spr.getX(), (float) yPos);
		
			// Checks for attacks by the player: the player's position and whether the attack was from the left or the right.
			rifleman_spr.getGot(torso_spr.getX(), (float) yPos, attackl, attackr);
			rifleman2_spr.getGot(torso_spr.getX(), (float) yPos, attackl, attackr);
			screen_spr.getGot((float) xPos - c, (float) yPos, attackl, attackr);

			// Checks for the protag having been attacked.
			if (rifleman_spr.didYouHitMe()) {
				rifleman_spr.acknowledgeHit();
				takeHit();
			}
			
			if (rifleman2_spr.didYouHitMe()) {
				rifleman2_spr.acknowledgeHit();
				takeHit();
			}
		}



		// Collisions
		if (yPos > floor) {
			yPos = floor;
			torso_spr.setY(floor - (BLOCK * 2));
			larm_spr.setY(floor - (BLOCK * 2));
			rarm_spr.setY(floor - (BLOCK * 2));
			mayDescend = false;
			jump = false; // allows the player to jump
		}
		
		// No point checking collisions in a different level!
		if (level == 1) {
			for (Sprite s: chandeliers) {
				collision(s, true);	
			}

			collision(piano_spr, false);
			collision(balcony_spr, true);
			collision(wall1_spr, true);
			collision(wall2_spr, true);
			collision(wall3_spr, false);
			collision(handrail1_spr, false);
			collision(handrail2_spr, false);
			if (!screen_spr.getStatus()) collision(screen_spr, false);
			collision(branch1_spr, false);
			collision(branch2_spr, false);

			checkTileCollision(legs_spr, ballroom_collisions);
		}

		if (level == 2) {
			checkTileCollision(legs_spr, train_collisions);
		}

	
		// Update animations
		shife_anim.update(elapsed);
		legs_idle_anim.update(elapsed);
		legs_jump_anim.update(elapsed);
		legs_anim.update(elapsed);
		
		if (level == 1) {
			if (!rifleman_spr.getStatus()) rifleman_spr.update(elapsed);
			if (!rifleman2_spr.getStatus()) rifleman2_spr.update(elapsed);
		}
	}



	public void collision(Sprite spr, boolean hasBottom) {

		int left = (int) spr.getX() + c; // getX() matches xPos when the sprite is on the left edge of the screen.  c accounts for this.
		int right = left + spr.getImage().getWidth(null) + BLOCK; // the extra block is the protag's breadth; checks his left side, not his right.
		int top = (int) spr.getY();
		int bottom = top + spr.getImage().getHeight(null) + (BLOCK * 2);

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
		
		// If the object is completely missed, freedom and a hasty return
		if (xPos < left    || !inY) mayGoRight = true;
		if (xPos > right   || !inY) mayGoLeft = true;
		if (yPos < top    || !inX) mayDescend = true;
		if ((yPos > bottom || !inX) || !hasBottom) mayAscend = true;
		if (!inX && !inY) return;

		if ((inX) && (yPos > bottom - 48) && (yPos <= bottom) && hasBottom) { // bumping the bottom with your head a la a ? block
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
		} else if ((inX) && (yPos == top)) { // landing on top or walking onto the top
			floor = top;
			yPos = floor;
			jump = false;
			torso_spr.setY(top - (BLOCK * 2));
			larm_spr.setY(top - (BLOCK * 2));
			rarm_spr.setY(top - (BLOCK * 2));
			jump_dx = 0;
			mayDescend = false;
		} else if ((inX) && (inY)) { // simply ending up inside somehow
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
				floor = top;
				yPos = floor;
				jump = false;
				torso_spr.setY(top - (BLOCK * 2));
				larm_spr.setY(top - (BLOCK * 2));
				rarm_spr.setY(top - (BLOCK * 2));
				jump_dx = 0;
				mayDescend = false;

				Sound land = new Sound("sound/land.wav");
				land.start();
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

		if ((xPos <= left + 10) || (xPos >= right - 10)) { // allows for sliding down the edge of the sprite
			floor = ZERO_BLOCKS;
			mayDescend = true;
		}

	}



	public void checkTileCollision(Sprite s, TileMap tmap) {
	/*	// Take a note of a sprite's current position
    		
   	 	// Divide the sprite’s x coordinate by the width of a tile, to get
    		// the number of tiles across the x axis that the sprite is positioned at 
    		int	xtile = (int)(sx / tileWidth);
    		// The same applies to the y coordinate
    		int ytile = (int)(sy / tileHeight);
    	
    		// What tile character is at the top left of the sprite s?
    		char ch = tmap.getTileChar(xtile, ytile);

		sy = yPos; // Bottom...
    		xtile = (int)(sx / tileWidth); // ... left
    		ytile = (int)((sy / tileHeight));
    		ch = tmap.getTileChar(xtile, ytile);

     		if (ch != '.') {
			floor = ytile * 64;
		}
		*/
	

		// Take a note of a sprite's current position
	        int sx = xPos - c;
       		int sy = (int) s.getY(); // top of legs
     	   
       	 	// Find out how wide and how tall a tile is
       	 	float tileWidth = tmap.getTileWidth();
       		float tileHeight = tmap.getTileHeight();
       	 
       	 	// the number of tiles across the axes that the sprite is positioned at 
        	int xtile = (int)(sx / tileWidth);
        	int ytile = (int)(sy / tileHeight);
        	char ch = tmap.getTileChar(xtile, ytile); // which tile is at the top left ?
        
        
        	if (ch != '.') {
			System.out.println("top left");
		}
        
        	
        	// bottom left
        	xtile = (int)(sx / tileWidth);
        	ytile = (int)((sy + s.getHeight())/ tileHeight);
        	ch = tmap.getTileChar(xtile, ytile);
        
        	// If it's not empty space
        	if (ch != '.') 
        	{
			System.out.println("bottom left");
        	}

		if (ch == '.') mayDescend = true;

   	 }



	public void keyPressed(KeyEvent e) { 
		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = true;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = true;
		if (e.isShiftDown()) run = true;
		if (e.getKeyCode() == KeyEvent.VK_SPACE) jump = true; 
	
		// Combat
		if ((e.getKeyCode() == KeyEvent.VK_H) && (!attackl) && (!attackr)) attackl = true;
		if ((e.getKeyCode() == KeyEvent.VK_L) && (!attackl) && (!attackr)) attackr = true;
	
		// Debug
		if (e.getKeyCode() == KeyEvent.VK_R) {
			init_game(level);
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (level == 0) {
				System.out.println("Thanks for playing!");
				stop();
			} else {
				init_game(0);
			}
		}
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



	public void takeHit() {
		hp--;

		Sound shot = new Sound("sound/shot.wav");
		shot.start();

		if (hp == 3) torso_spr.setAnimation(hitonce_anim);
		if (hp == 2) torso_spr.setAnimation(hittwice_anim);
		if (hp == 1) torso_spr.setAnimation(hitthrice_anim);

		if (hp < 1) {
			lose();
		}
	}


	public void win() {
		win = true;
		mayGoLeft = false;
		mayGoRight = false;
		mayAscend = true;
		rifleman_spr.kill();
		rifleman2_spr.kill();
	}

	public void lose() {
		lose = true;

		mayGoRight = false;
		mayGoLeft = false;
		mayAscend = false;
		mayDescend = true;

		torso_spr.hide();
		larm_spr.hide();
		rarm_spr.hide();
	}

	public boolean isTheGameOver() {
		if (win || lose) {
			return true;
		} else {
			return false;
		}
	}
}
