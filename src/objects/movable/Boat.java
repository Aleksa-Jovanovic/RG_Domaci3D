package objects.movable;

import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Boat extends MovableObject {
	public static class BoatDestination implements Destination {
		private Point3D destination;
		private double delta;
		
		public BoatDestination ( Point3D destination, double delta ) {
			this.destination = destination;
			this.delta = delta;
		}
		
		@Override public boolean reached ( double x, double y, double z ) {
			double dx = Math.abs ( this.destination.getX ( ) - x );
			double dy = Math.abs ( this.destination.getY ( ) - y );
			double dz = Math.abs ( this.destination.getZ ( ) - z );
			
			if ( dx <= this.delta && dy <= this.delta && dz <= this.delta ) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private static Affine getPosition ( double angle, double distance ) {
		Affine identity = new Affine (  );
		
		identity.appendRotation ( angle, new Point3D ( 0, 0, 0 ), new Point3D ( 0, 1, 0 ) ); //Angle, Pivot, Axis!
		identity.appendTranslation ( 0, 0, distance );
		
		return identity;
	}
	
	private static Point3D getSpeed ( double angle, double distance, double speed ) {
		Affine position = Boat.getPosition ( angle, distance );
		
		return new Point3D (
				-position.getTx ( ),
				-position.getTy ( ),
				-position.getTz ( )
		).normalize ( ).multiply ( speed );
	}
	
	private static BoatDestination getBoatDestination ( double angle, double zDestination, double delta ) {
		Affine position = Boat.getPosition ( angle, zDestination );
		Point3D destination = new Point3D (
				position.getTx ( ),
				position.getTy ( ),
				position.getTz ( )
		);
		
		return new BoatDestination ( destination, delta );
	}
	
	@Override public void onDestinationReached ( ) { }

	public Boat ( Group parent, double width, double height, double depth, Color color, double distance, double angle, double speed, Camera camera, double destination, double delta ) {
		super (
				parent,
				Boat.getPosition ( angle, distance ),
				Boat.getSpeed ( angle, distance, speed ),
				new Point3D ( 0, 0, 0 ),
				Boat.getBoatDestination ( angle, destination + depth / 2, delta )
		);
		
		Box box = new Box ( width, height, depth );
		box.setMaterial (
				new PhongMaterial ( color )
		);
		super.getChildren ( ).addAll ( box );

		//Setting up the camera
		camera.getTransforms().addAll(
				new Translate(0 , -3*height, 6*depth),
				new Rotate(-180, Rotate.Y_AXIS)
		);
		super.getChildren().add(camera);
	}
}
