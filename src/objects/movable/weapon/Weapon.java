package objects.movable.weapon;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.transform.Affine;
import objects.movable.MovableObject;
import timer.MyAnimationTimer;

public abstract class Weapon extends MovableObject {
	private MyAnimationTimer timer;
	
	public Weapon ( Group parent, Affine position, Point3D speed, Point3D damp, Destination destination, MyAnimationTimer timer ) {
		super ( parent, position, speed, damp, destination );
		
		this.timer = timer;
		
		this.timer.setWeapon ( this );
	}
	
	@Override public boolean update ( long now ) {
		boolean returnValue = super.update ( now );
		
		if ( returnValue ) {
			this.timer.setWeapon ( null );
		}
		
		return returnValue;
	}
}
