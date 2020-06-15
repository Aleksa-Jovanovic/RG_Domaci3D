package objects.movable.weapon;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import objects.Cannon;
import objects.movable.MovableObject;
import timer.MyAnimationTimer;

public class CannonBall extends Weapon {
	public static class CannonBallDestination implements Destination {
		private double radius;
		private double landingPointHeight;
		private Group root;

		public CannonBallDestination(double radius, double landingPointHeight, Group root) {
			this.radius = radius;
			this.landingPointHeight = landingPointHeight;
			this.root = root;
		}

		@Override public boolean reached (double x, double y, double z ) {
			boolean reached =  ( y - this.radius ) >= 0;

			if(reached){
				Cylinder landingPoint = new Cylinder(this.radius, this.landingPointHeight);
				landingPoint.setMaterial(new PhongMaterial(Color.BLUE));
				landingPoint.getTransforms().add( //Set the splash position to cannonBall position
						new Translate(x,y,z)
				);
				this.root.getChildren().add(landingPoint); //Add splash to root

				//Add animation for fading
				TranslateTransition animation = new TranslateTransition(Duration.seconds(3),landingPoint);
				animation.setByY(this.landingPointHeight/2);
				animation.setOnFinished(event -> this.root.getChildren().remove(landingPoint));
				animation.play();
			}

			return  reached;
		}
	}
	
	private static Point3D getSpeed ( double ySpeed, double xAngle, double yAngle ) {
		Point3D speedVector = new Point3D ( 0, ySpeed, 0 );
		Rotate rotateX = new Rotate ( xAngle, Rotate.X_AXIS );
		Rotate rotateY = new Rotate ( yAngle, Rotate.Y_AXIS );
		speedVector = rotateX.transform ( speedVector );
		speedVector = rotateY.transform ( speedVector );
		return speedVector;
	}
	
	private static Affine getPosition ( double cannonHeight, double ventHeight, double xAngle, double yAngle ) {
		Affine identity = new Affine ( );

		identity.appendRotation ( yAngle, Point3D.ZERO, Rotate.Y_AXIS );
		identity.appendTranslation ( 0, -cannonHeight, 0 );
		identity.appendRotation ( xAngle, Point3D.ZERO, Rotate.X_AXIS );
		identity.appendTranslation ( 0, -ventHeight, 0 );
		
		return identity;
	}
	
	public CannonBall ( Group root, double radius, Color color, double cannonHeight, double ventHeight, double xAngle, double yAngle, double ySpeed, double gravity, MyAnimationTimer timer ) {
		super (
				root,
				CannonBall.getPosition ( cannonHeight, ventHeight, xAngle, yAngle ),
				CannonBall.getSpeed ( ySpeed, xAngle, yAngle ),
				new Point3D ( 0, gravity, 0 ),
				new CannonBallDestination ( radius, 7 * radius, root ),
				timer
		);
		Sphere ball = new Sphere (radius );
		ball.setMaterial ( new PhongMaterial ( color ) );
		super.getChildren ( ).addAll ( ball );
	}
}
