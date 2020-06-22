package objects.movable.weapon;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import main.Main;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import timer.MyAnimationTimer;

public class CannonBall extends Weapon {
	public static class CannonBallDestination implements Destination {
		private double radius;
		private double landingPointHeight;
		private Group root;
		private Camera cannonBallCamera;
		private Camera cannonCamera;
		private Scene scene;

		public CannonBallDestination(double radius, double landingPointHeight, Group root, Camera cannonBallCamera, Camera cannonCamera, Scene scene) {
			this.radius = radius;
			this.landingPointHeight = landingPointHeight;
			this.root = root;
			this.cannonBallCamera = cannonBallCamera;
			this.cannonCamera = cannonCamera;
			this.scene = scene;
		}

		@Override public boolean reached (double x, double y, double z ) {
			boolean reached =  ( y - this.radius ) >= 0;

			if(reached){
				Cylinder landingPoint = new Cylinder(this.radius, this.landingPointHeight);
				landingPoint.setMaterial(new PhongMaterial(Color.BLUE));
				landingPoint.getTransforms().add( //Set the splash position to cannonBall position
						new Translate(x,y + landingPointHeight / 2 - Main.Constants.OCEAN_HEIGHT,z)
				);
				this.root.getChildren().add(landingPoint); //Add splash to root

				//If cannonBall camera is active we need to switch to normal camera
				if(this.cannonBallCamera == this.scene.getCamera()){
					scene.setCamera(cannonCamera);
				}

				//Add animation for fading
				Timeline animationStart = new Timeline();
				KeyFrame frameEnd = new KeyFrame(Duration.seconds(2),
						new KeyValue(landingPoint.translateYProperty(),y-this.landingPointHeight/2 - Main.Constants.OCEAN_HEIGHT),
						new KeyValue(landingPoint.materialProperty(), new PhongMaterial(Color.WHITE), Interpolator.LINEAR));
				animationStart.getKeyFrames().add(frameEnd);
				animationStart.play();

				Timeline animationEnd = new Timeline();
				KeyFrame frameStart = new KeyFrame(Duration.seconds(2),
						new KeyValue(landingPoint.translateYProperty(),y + landingPointHeight / 2 - Main.Constants.OCEAN_HEIGHT),
						new KeyValue(landingPoint.materialProperty(), new PhongMaterial(Color.BLUE), Interpolator.LINEAR));
				animationEnd.getKeyFrames().add(frameStart);

				animationStart.setOnFinished(e -> animationEnd.play());
				animationEnd.setOnFinished(e -> this.root.getChildren().remove(landingPoint));
			}

			return  reached;
		}
	}

	private Scene scene;
	private Camera camera;
	
	private static Point3D getSpeed ( double ySpeed, double xAngle, double yAngle ) {
		Point3D speedVector = new Point3D ( 0, ySpeed*2, 0 );
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

	public CannonBall (Scene scene, Group root, double radius, Color color, double cannonHeight, double ventHeight, double xAngle, double yAngle, double ySpeed, double gravity, MyAnimationTimer timer, Camera cannonCamera, Camera cannonBallCamera) {
		super (
				root,
				CannonBall.getPosition ( cannonHeight, ventHeight, xAngle, yAngle ),
				CannonBall.getSpeed ( ySpeed, xAngle, yAngle ),
				new Point3D ( 0, gravity, 0 ),
				new CannonBallDestination ( radius, 7 * radius, root, cannonBallCamera, cannonCamera, scene ),
				timer
		);
		this.scene = scene;
		this.camera = cannonBallCamera;

		Sphere ball = new Sphere (radius );
		ball.setMaterial ( new PhongMaterial ( color ) );
		super.getChildren ( ).addAll ( ball);

		super.getChildren().add(camera);
	}
}
