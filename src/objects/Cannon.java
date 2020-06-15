package objects;

import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import objects.movable.weapon.CannonBall;
import timer.MyAnimationTimer;

public class Cannon extends Group implements EventHandler<MouseEvent> {
	private double           sceneWidth;
	private double           sceneHeight;
	private Rotate           rotateX;
	private Rotate           rotateY;
	private MyAnimationTimer timer;
	private double           width;
	private Color            cannonColor;
	private double           height;
	private double           ySpeed;
	private double           gravity;
	private Group            root;
	private double           ventHeight;

	private Group makeBody(double width, double height, double depth, Color cannonColor){
		Group body = new Group();

		//Defining unit size - depends on actual cannon size
		float xUnit = (float) (width/3);
		float yUnit = (float) (height / 8);
		float zUnit = (float) (depth / 6);

		//Box part
		Box box = new Box(xUnit * 3, 8 * yUnit, 2 * zUnit);
		box.getTransforms().add(
				new Translate(0,0,-1 * zUnit)
		);
		box.setMaterial(new PhongMaterial(cannonColor));

		body.getChildren().add(box);

		//Mesh part
		float points[] = {
				1.5f * xUnit, 4 * yUnit, 0 * zUnit,
				1.5f * xUnit, 4 * yUnit, 2 * zUnit,
				1.5f * xUnit, 2 * yUnit, 4 * zUnit,
				1.5f * xUnit, -4 * yUnit, 0 * zUnit,

				-1.5f * xUnit, 4 * yUnit, 0 * zUnit,
				-1.5f * xUnit, 4 * yUnit, 2 * zUnit,
				-1.5f * xUnit, 2 * yUnit, 4 * zUnit,
				-1.5f * xUnit, -4 * yUnit, 0 * zUnit
		};
		 float texels[] = {
		 		0.5f, 0.5f
		 };
		 int faces[] = {
		 		0, 0, 1, 0, 3, 0,
				 1, 0, 2, 0, 3, 0,
				 4, 0, 7, 0, 5, 0,
				 5, 0, 7, 0, 6, 0,
				 0, 0, 4, 0, 5, 0,
				 0, 0, 5, 0, 1, 0,
				 1, 0, 5, 0, 6, 0,
				 1, 0, 6, 0, 2, 0,
				 2, 0, 6, 0, 7, 0,
				 2, 0, 7, 0, 3, 0,
		 };

		TriangleMesh triangleMesh = new TriangleMesh();
		triangleMesh.getPoints().addAll(points);
		triangleMesh.getTexCoords().addAll(texels);
		triangleMesh.getFaces().addAll(faces);

		MeshView meshView = new MeshView(triangleMesh);
		meshView.setDrawMode(DrawMode.FILL);
		meshView.setMaterial(new PhongMaterial(cannonColor));

		body.getChildren().add(meshView);

		return body;
	}

	private Group makeVent(double radius, double ventHeight, Color cannonColor){
		Group vent = new Group();

		Cylinder neck = new Cylinder(0.5 * radius, 0.9 * ventHeight);
		neck.setMaterial(new PhongMaterial(cannonColor));
		neck.getTransforms().add(
				new Translate(0, 0.05 * ventHeight, 0)
		);

		vent.getChildren().add(neck);

		Cylinder top = new Cylinder(radius, 0.1 * ventHeight);
		top.setMaterial(new PhongMaterial(cannonColor));
		top.getTransforms().addAll(
				new Translate(0, -0.45 * ventHeight, 0)
		);

		vent.getChildren().add(top);

		return vent;
	}

	public Cannon ( Group root, double width, double height, double depth, double islandHeight, double ventHeight, Color cannonColor, double sceneWidth, double sceneHeight, double ySpeed, double gravity, MyAnimationTimer timer, Camera camera ) {
		this.root = root;
		
		Group cannon = new Group ( );
		super.getChildren ( ).addAll ( cannon );
		
		Group podium = makeBody(width, height, depth, cannonColor);
		podium.getTransforms().add(
				new Translate ( 0, -( height + islandHeight ) / 2, 0 )
		);
		cannon.getChildren ( ).addAll ( podium );
		
		Group vent = makeVent( width / 2, ventHeight, cannonColor );
		
		this.rotateX = new Rotate ( );
		this.rotateY = new Rotate ( );
		this.rotateX.setAxis ( Rotate.X_AXIS );
		this.rotateY.setAxis ( Rotate.Y_AXIS );
		
		cannon.getTransforms ( ).addAll (
				this.rotateY
		);
		vent.getTransforms ( ).addAll (
				new Translate ( 0, -height / 2, 0 ),
				this.rotateX,
				new Translate ( 0, -ventHeight / 2, 0 )
		);
		cannon.getChildren ( ).addAll ( vent );

		//Setting up the camera
		camera.getTransforms().addAll(
				new Translate(0 , -3*height, -8*depth),
				new Rotate(-10,Rotate.X_AXIS)
		);
		//Adding camera to cannon so it rotates with it!
		cannon.getChildren().add(camera);

		this.sceneHeight = sceneHeight;
		this.sceneWidth = sceneWidth;
		this.timer = timer;
		this.width = width;
		this.cannonColor = cannonColor;
		this.height = height;
		this.ySpeed = ySpeed;
		this.gravity = gravity;
		this.ventHeight = ventHeight;
	}
	
	@Override public void handle ( MouseEvent event ) {
		if ( MouseEvent.MOUSE_MOVED.equals ( event.getEventType ( ) ) ) {
			double xRatio = event.getSceneX ( ) * 2 / this.sceneWidth;
			double yRatio = event.getSceneY ( ) / this.sceneHeight;

			this.rotateX.setAngle ( -120 * yRatio );
			this.rotateY.setAngle ( 360 * xRatio );
		} else if ( MouseEvent.MOUSE_PRESSED.equals ( event.getEventType ( ) ) && this.timer.canAddWeapon ( ) ) {
			CannonBall cannonBall = new CannonBall (
					root,
					this.width / 2,
					this.cannonColor,
					this.height / 2,
					this.ventHeight,
					this.rotateX.getAngle ( ),
					this.rotateY.getAngle ( ),
					this.ySpeed,
					this.gravity,
					timer
			);
			root.getChildren ( ).addAll ( cannonBall );
		}
	}
}
