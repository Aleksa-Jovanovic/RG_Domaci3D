package timer;

import javafx.animation.AnimationTimer;
import objects.movable.MovableObject;
import java.util.List;

public class MyAnimationTimer extends AnimationTimer {
	private List<MovableObject> movableObjects;
	private MovableObject       weapon;
	private GameEventListener listener;
	private boolean gameOver;
	
	public MyAnimationTimer ( List<MovableObject> movableObjects, GameEventListener listener ) {
		this.movableObjects = movableObjects;
		this.listener = listener;
	}
	
	@Override public synchronized void handle ( long now ) {
		boolean islandHit = this.movableObjects.removeIf ( object -> object.update ( now  ) );
		
		if ( this.gameOver == false ) {
			if ( islandHit ) {
				this.listener.onGameLost ( );
				this.gameOver = true;
			} else if ( this.weapon != null ) {
				boolean boatHit = this.movableObjects.removeIf ( object -> object.handleCollision ( this.weapon ) );
				if ( boatHit ) {
					this.weapon.onCollision ( );
					this.weapon = null;
					if ( this.movableObjects.size ( ) == 0 ) {
						this.listener.onGameWon ( );
						this.gameOver = true;
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
