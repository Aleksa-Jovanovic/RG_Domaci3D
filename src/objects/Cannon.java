package objects;

import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import main.Main;
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
	private Scene 			 scene;
	private Camera cannonBallCamera;
	private Camera camera;

	private int ammoNumber;
	private Group ammoIndicator;
	private Group ammoIndicatorCannonView;

	private Rectangle healthBar;
	private double health;

	private int numberOfDefetedShips;
	private Text defetedShipsTextCannonView;
	private Text defetedShipsText;

	private boolean fixedCameraView = true;
	private Group cannonGroup;

	private static Cannon cannonRefernce = null;
	public static Cannon getCannonRefernce(){
		return cannonRefernce;
	}

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

	public Cannon (Scene scene, Group root, double width, double height, double depth, double islandHeight, double ventHeight, Color cannonColor, double sceneWidth, double sceneHeight, double ySpeed, double gravity, MyAnimationTimer timer, Camera camera, Camera cannonBallCamera ) {
		cannonRefernce = this;

		this.scene = scene;
		this.root = root;
		this.cannonBallCamera = cannonBallCamera;
		this.camera = camera;

		this.ammoNumber = Main.Constants.MAX_BULLETS;
		this.ammoIndicator = new Group();
		this.ammoIndicatorCannonView = new Group();

		this.healthBar = new Rectangle(width * 8, 8);
		this.health = width * 8;
		this.healthBar.setFill(Color.RED);

		this.numberOfDefetedShips = 0;
		this.defetedShipsTextCannonView = new Text("Kills : 0");
		this.defetedShipsTextCannonView.setFont(new Font(15));
		this.defetedShipsText = new Text("Kills : 0");
		this.defetedShipsText.setFont(new Font(30));

		Group cannon = new Group ( );
		this.cannonGroup = cannon;
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

		//Adding health indicator
		this.healthBar.getTransforms().addAll(
				new Translate(-(width * 4) , -0.5*height, -1*depth)
		);
		//this.cannonGroup.getChildren().add(healthBar);

		//Adding kill count
		this.defetedShipsTextCannonView.getTransforms().addAll(
				new Translate(-(width * 16), -3.6*height, 8*depth)
		);
		//this.cannonGroup.getChildren().add(defetedShipsTextCannonView);
		this.defetedShipsText.getTransforms().addAll(
				new Translate ( 0, 0, -100),
				new Rotate ( Main.Constants.CAMERA_X_ANGLE, Rotate.X_AXIS ),
				new Translate ( -(sceneWidth) / 3, -340, -800)
		);
		this.root.getChildren().add(this.defetedShipsText);

		this.sceneHeight = sceneHeight;
		this.sceneWidth = sceneWidth;
		this.timer = timer;
		this.width = width;
		this.cannonColor = cannonColor;
		this.height = height;
		this.ySpeed = ySpeed;
		this.gravity = gravity;
		this.ventHeight = ventHeight;

		//AmmoIndicator
		for(int i = 0; i < ammoNumber ; i ++){
			Rectangle ammo = new Rectangle(i*4,  0, 2, 10);
			ammo.setFill(Color.YELLOW);
			ammo.setDepthTest(DepthTest.DISABLE);
			ammoIndicator.getChildren().add(ammo);
		}

		ammoIndicator.getTransforms().addAll(
				new Translate ( 0, 0, -100),
				new Rotate ( Main.Constants.CAMERA_X_ANGLE, Rotate.X_AXIS ),
				new Translate ( -(ammoNumber * 4) / 2, 0, -1600)
		);

		for(int i = 0; i < ammoNumber ; i ++){
			Rectangle ammo = new Rectangle(i*4,  0, 2, 10);
			ammo.setFill(Color.YELLOW);
			ammo.setDepthTest(DepthTest.DISABLE);
			ammoIndicatorCannonView.getChildren().add(ammo);
		}

		ammoIndicatorCannonView.getTransforms().addAll(
				new Translate(-(ammoNumber * 4) / 2 , -0.7*height, -1*depth)
		);


		this.root.getChildren().addAll(ammoIndicator);
		//this.cannonGroup.getChildren().add(ammoIndicatorCannonView);
	}

	//Ammo methods
	public void addBullets(int ammoCount){
		for (int i = 0; i < ammoCount; i++){
			if(ammoNumber == Main.Constants.MAX_BULLETS )
				break;
			this.addBullet();
		}
	}


	public void addBullet(){
		ammoNumber++;
		Rectangle ammo = new Rectangle((ammoNumber- 1 ) * 4,  0, 2, 10);
		ammo.setFill(Color.YELLOW);
		ammo.setDepthTest(DepthTest.DISABLE);
		ammoIndicator.getChildren().add(ammo);
		ammo = new Rectangle((ammoNumber- 1 ) * 4,  0, 2, 10);
		ammo.setFill(Color.YELLOW);
		ammo.setDepthTest(DepthTest.DISABLE);
		ammoIndicatorCannonView.getChildren().add(ammo);
		System.out.println(ammoNumber);
	}

	public void removeBullet(){
		ammoNumber--;
		ammoIndicator.getChildren().remove(ammoNumber);
		ammoIndicatorCannonView.getChildren().remove(ammoNumber);
	}

	public void switchAmmoDisplay(boolean toFixedCamera){
		this.hideAmmoCount();
		if(toFixedCamera){ // FixedCamera view
			this.root.getChildren().add(ammoIndicator);
			this.root.getChildren().add(defetedShipsText);
		}else{ //CannonCamera view
			this.cannonGroup.getChildren().add(ammoIndicatorCannonView);
			this.cannonGroup.getChildren().add(healthBar);
			this.cannonGroup.getChildren().add(defetedShipsTextCannonView);
		}
	}
	public void hideAmmoCount(){
		this.cannonGroup.getChildren().remove(defetedShipsTextCannonView);
		this.root.getChildren().remove(defetedShipsText);
		this.cannonGroup.getChildren().remove(ammoIndicatorCannonView);
		this.cannonGroup.getChildren().remove(healthBar);
		this.root.getChildren().remove(ammoIndicator);
	}

	public void lowerHealth(){
		double lowerBy = 6 + Math.random()*4;
		double health = this.healthBar.getWidth();
		health -= lowerBy;
		if(health < 0)
			health = 0;
		this.healthBar.setWidth(health);
	}

	public double getHealth(){
		return this.health;
	}

	public int getNumberOfDefetedShips(){
		return numberOfDefetedShips;
	}

	public void incDefetedShipsNumber(){
		numberOfDefetedShips++;
		defetedShipsText.setText("Kills : " + String.valueOf(numberOfDefetedShips));
		defetedShipsTextCannonView.setText("Kills : " + String.valueOf(numberOfDefetedShips));
	}

	@Override public void handle ( MouseEvent event ) {
		if ( MouseEvent.MOUSE_MOVED.equals ( event.getEventType ( ) ) ) {
			double xRatio = event.getSceneX ( ) * 2 / this.sceneWidth;
			double yRatio = event.getSceneY ( ) / this.sceneHeight;

			this.rotateX.setAngle ( -120 * yRatio );
			this.rotateY.setAngle ( 360 * xRatio );
		} else if ( MouseEvent.MOUSE_PRESSED.equals ( event.getEventType ( ) ) && this.timer.canAddWeapon ( ) ) {

			if(ammoNumber == 0)
				return;

			CannonBall cannonBall = new CannonBall (
					scene,
					root,
					this.width / 2,
					this.cannonColor,
					this.height / 2,
					this.ventHeight,
					this.rotateX.getAngle ( ),
					this.rotateY.getAngle ( ),
					this.ySpeed,
					this.gravity,
					timer,
					camera,
					cannonBallCamera
			);
			//lowerHealth();
			this.removeBullet();
			root.getChildren ( ).addAll ( cannonBall );
		}
	}
}
