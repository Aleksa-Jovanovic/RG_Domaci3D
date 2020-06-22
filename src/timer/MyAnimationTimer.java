package timer;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import main.CameraController;
import main.Main;
import objects.Cannon;
import objects.movable.MovableObject;
import java.util.List;

public class MyAnimationTimer extends AnimationTimer {
	private static MyAnimationTimer myAnimationTimerRef;
	private List<MovableObject> movableObjects;
	private MovableObject       weapon;
	private GameEventListener listener;
	private boolean gameOver;
	private  Group root;
	private int currentWave = 1;
	private CameraController cameraController;
	private Cannon cannon;
	
	public MyAnimationTimer (List<MovableObject> movableObjects, GameEventListener listener, Group root, CameraController cameraController) {
		myAnimationTimerRef = this;

		this.movableObjects = movableObjects;
		this.listener = listener;
		this.root = root;
		this.cameraController = cameraController;
		this.cannon = null;
	}

	public static MyAnimationTimer getMyAnimationTimerRef(){
		return myAnimationTimerRef;
	}

	public int getCurrentWave(){
		return currentWave;
	}

	public void setCannon(Cannon cannon){
		this.cannon = cannon;
	}


	@Override public synchronized void handle ( long now ) {
		boolean islandHit = this.movableObjects.removeIf ( object -> object.update ( now  ) );
		
		if ( this.gameOver == false ) {
			if ( islandHit ) {
				this.listener.onGameLost ( );
				this.gameOver = true;
			} else if ( this.weapon != null ) {
				boolean boatHit = this.movableObjects.removeIf ( object -> object.handleCollision ( this.weapon ) ); //Na samom brodu treba da se sredi handleCollision!
				if ( boatHit ) {
					this.weapon.onCollision ( );
					this.weapon = null;
					cannon.incDefetedShipsNumber();
					if ( this.movableObjects.size ( ) == 0 ) {
						//Creating new boats!
						if(currentWave < Main.Constants.MAX_WAVE_NUMBER){
							currentWave++;
							double startAngle = Math.random()*360;
							Main.Constants.BOAT_SPEED += Main.Constants.BOAT_SPEED_INCREASE;
							Main.createBoats(startAngle, movableObjects, root, cameraController);
							cannon.addBullets(10);
						}else{
							this.listener.onGameWon ( );
							this.gameOver = true;
						}

					}
				}
			}
		}
		
		if ( this.weapon != null ) {
			this.weapon.update ( now );
		}
	}
	
	public synchronized boolean canAddWeapon ( ) {
		return this.gameOver == false && this.weapon == null;
	}
	
	public synchronized void setWeapon ( MovableObject weapon ) {
		this.weapon = weapon;
	}

	public synchronized  MovableObject getWeapon(){
		return this.weapon;
	}
}
