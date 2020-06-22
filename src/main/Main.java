package main;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import objects.Cannon;
import objects.movable.Boat;
import objects.movable.MovableObject;
import timer.GameEventListener;
import timer.MyAnimationTimer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application implements GameEventListener {
    public static class Constants {
        public static final String TITLE = "Island defense";
        
        public static final double CAMERA_NEAR_CLIP = 0.1;
	    public static final double CAMERA_FAR_CLIP  = 100000;
	    public static final double CAMERA_X_ANGLE   = -45;
	    public static final double CAMERA_Z         = -2000;
        
        public static final double SCENE_WIDTH  = 800;
        public static final double SCENE_HEIGHT = 800;
        
        public static final double OCEAN_WIDTH  = 10000;
        public static final double OCEAN_DEPTH  = 10000;
        public static final double OCEAN_HEIGHT = 2;
        
        public static final double ISLAND_RADIUS  = 100;
        public static final double ISLAND_HEIGHT = 6;
        
        public static final double DELTA = 0.1;
        
        public static final int    NUMBER_OF_BOATS = 4;
        public static final double BOAT_WIDTH      = 10;
        public static final double BOAT_HEIGHT     = 10;
        public static final double BOAT_DEPTH      = 20;
        public static final double BOAT_DISTANCE   = 500;
        public static  double BOAT_SPEED      = 0.1;
        public static final double BOAT_SPEED_INCREASE = 0.2;
        
        public static final double CANNON_WIDTH       = 10;
        public static final double CANNON_HEIGHT      = 50;
        public static final double CANNON_DEPTH       = 40;
        public static final double CANNON_VENT_LENGTH = 50;
        public static final double Y_SPEED            = -2;
        public static final int MAX_BULLETS = 20;
        
        public static final double GRAVITY = 0.04;

        public static final int MAX_WAVE_NUMBER = 2;

        public static final double MAP_SCALING = 0.2;
    }
    
	private MyAnimationTimer timer;
	
	public static void main ( String[] args ) {
		launch ( args );
	}
	
	@Override public void onGameWon ( ) { }
	
	@Override public void onGameLost ( ) { }
	
	@Override
	public void start ( Stage primaryStage ) throws Exception {
		List<MovableObject> movableObjects = new ArrayList<> ( );
		Group               root           = new Group ( );
		Scene               scene          = new Scene ( root, Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT, true, SceneAntialiasing.BALANCED );

		//Ocean
		Box ocean = new Box ( Constants.OCEAN_WIDTH, Constants.OCEAN_HEIGHT, Constants.OCEAN_DEPTH );
		ocean.setMaterial ( new PhongMaterial ( Color.BLUE ) );
		ocean.setDrawMode(DrawMode.LINE);
		root.getChildren ( ).addAll ( ocean );

		//1st
		Circle circle1 = new Circle(0, 0, 50 + Constants.ISLAND_RADIUS, new Color(1,0,0,0.6));
		circle1.setTranslateZ(-3);
		//2nd
		Circle circle2 = new Circle(0, 0, 150 + Constants.ISLAND_RADIUS, new Color(1,1,0,0.6));
		Shape shape2 = Shape.subtract(circle2,circle1);
		shape2.setFill(new Color(1,1,0,0.6));
		shape2.setTranslateZ(-3);
		//3rd
		Circle circle3 = new Circle(0, 0, 300 + Constants.ISLAND_RADIUS,new Color(0,1,0,0.6));
		Shape shape3 = Shape.subtract(circle3,circle2);
		shape3.setFill(new Color(0,1,0,0.6));
		shape3.setTranslateZ(-3);
		//4th
		Circle circle4 = new Circle(0, 0, 500 + Constants.ISLAND_RADIUS, new Color(0.5,0.87,1,0.7));
		Shape shape4 = Shape.subtract(circle4,circle3);
		shape4.setFill(new Color(0.5,0.87,1,0.6));
		shape4.setTranslateZ(-3);
		//5th
		Circle circle5 = new Circle(0, 0, 1000 + Constants.ISLAND_RADIUS, new Color(0,0,1,0.5));
		Shape shape5 = Shape.subtract(circle5,circle4);
		shape5.setFill(new Color(0,0,1,0.6));
		shape5.setTranslateZ(-3);

		Group range = new Group();
		range.getChildren ( ).addAll (circle5,shape4,shape3,shape2,circle1 );
		range.getTransforms().addAll(
				new Translate(0, (Constants.OCEAN_HEIGHT ), 0),
				new Rotate(-90, Rotate.X_AXIS)
		);

		root.getChildren().add(range);
		
		Cylinder island = new Cylinder ( Constants.ISLAND_RADIUS, Constants.ISLAND_HEIGHT );
		island.setMaterial ( new PhongMaterial ( Color.BROWN ) );
		root.getChildren ( ).addAll ( island );

		//Camera--------------------------------------------------------------------------------------------------------
		CameraController cameraController = new CameraController(Constants.CAMERA_NEAR_CLIP, Constants.CAMERA_FAR_CLIP, scene, this.timer);
		scene.addEventHandler(KeyEvent.ANY, cameraController);
		Camera camera = cameraController.getFixedCamera();
		camera.getTransforms ( ).addAll (
				new Rotate ( Constants.CAMERA_X_ANGLE, Rotate.X_AXIS ),
				new Translate ( 0, 0, Constants.CAMERA_Z )
		);

		//Boats---------------------------------------------------------------------------------------------------------
		createBoats(0,movableObjects,root,cameraController);
		
		this.timer = new MyAnimationTimer ( movableObjects, this, root, cameraController );

		cameraController.setAnimationTimer(this.timer);
		Cannon cannon = new Cannon (
				scene,
				root,
				Constants.CANNON_WIDTH,
				Constants.CANNON_HEIGHT,
				Constants.CANNON_DEPTH,
				Constants.ISLAND_HEIGHT,
				Constants.CANNON_VENT_LENGTH,
				Color.GREEN,
				Constants.SCENE_WIDTH,
				Constants.SCENE_HEIGHT,
				Constants.Y_SPEED,
				Constants.GRAVITY,
				this.timer,
				cameraController.getCannonCamera(),
				cameraController.getCannonBallCamera()
		);

		Map map = new Map(scene, root, cameraController);
		map.getTransforms().addAll(
				new Translate ( 0, 0, -180),
				new Rotate ( Main.Constants.CAMERA_X_ANGLE, Rotate.X_AXIS ),
				new Translate ( -(Constants.SCENE_WIDTH) / 2.65, 0, -800)
		);
		root.getChildren().add(map);

		this.timer.setCannon(cannon);
		root.getChildren ( ).addAll ( cannon );
		scene.addEventHandler ( MouseEvent.ANY, cannon );
		
		scene.setFill ( Color.LIGHTBLUE );
		scene.setCursor ( Cursor.NONE );
		primaryStage.setScene ( scene );
		primaryStage.setTitle ( Constants.TITLE );
		primaryStage.show ( );
		timer.start ( );
	}

	public static void createBoats(double startAngle, List<MovableObject> movableObjects, Group root, CameraController cameraController){

			int additionalAngle[] = {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
			HashSet<Integer> set = new HashSet<Integer>();

			for ( int i = 0; i < Constants.NUMBER_OF_BOATS; ++i ) {
				//Angle calculation
				int angleNumber = (int)(Math.random()*(12)) ;
				while(set.contains(new Integer(angleNumber))){
					angleNumber = (int)(Math.random()*(11)) ;
				}
				set.add(new Integer(angleNumber));

				double angle = startAngle + additionalAngle[angleNumber];
				Boat boat = new Boat (
						root,
						Constants.BOAT_WIDTH,
						Constants.BOAT_HEIGHT,
						Constants.BOAT_DEPTH,
						Color.RED,
						Constants.BOAT_DISTANCE,
						angle,
						Constants.BOAT_SPEED,
						cameraController.getBoatCamera(i),
						Constants.ISLAND_RADIUS,
						Constants.DELTA
				);

				root.getChildren ( ).addAll ( boat );
				movableObjects.add ( boat );
			}
		}

}
