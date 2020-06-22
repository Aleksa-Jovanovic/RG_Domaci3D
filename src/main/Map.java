package main;

import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import objects.movable.Boat;
import objects.movable.MovableObject;
import timer.MyAnimationTimer;

import java.util.List;


public class Map extends Group {
    private Rectangle ocean;
    private Group turret;
    private Circle turretBody;
    private Rectangle turretVent;
    private Rectangle boats[];
    private Translate boatsTranslate[];

    private static Map mapRef;
    private boolean mapShowing;

    SubScene mapScene;

    private Group root;
    private CameraController cameraController;
    private Scene scene;
    private double sceneWidth;
    private double sceneHeight;
    private Camera camera;


    public static Map getMapRef(){
        return mapRef;
    }
    public boolean toggleShow(){
        boolean lastState = mapShowing;
        mapShowing = !mapShowing;
        if(lastState)
            root.getChildren().remove(this);
        else
            root.getChildren().add(this);

        return lastState;
    }

    public Map(Scene scene, Group root, CameraController cameraController){
        mapRef = this;
        mapShowing = true;

        this.scene = scene;
        this.sceneWidth = Main.Constants.SCENE_WIDTH;
        this.sceneHeight = Main.Constants.SCENE_HEIGHT;
        this.root = root;
        this.cameraController = cameraController;
        this.camera = this.cameraController.getUpCamera();



        this.mapScene = new SubScene(this,(int)(this.sceneWidth * Main.Constants.MAP_SCALING),(int)(this.sceneHeight * Main.Constants.MAP_SCALING),true, SceneAntialiasing.BALANCED);
        //this.mapScene.setCamera(this.camera);

        //super.getChildren().add(this.mapScene);


        this.ocean = new Rectangle((int)(this.sceneWidth * Main.Constants.MAP_SCALING),(int)(this.sceneHeight * Main.Constants.MAP_SCALING));
        this.ocean.setFill(Color.LIGHTBLUE);
        this.ocean.setStroke(Color.BLACK);
        this.ocean.setStrokeWidth(4);

        super.getChildren().add(this.ocean);

        turretBody = new Circle(Main.Constants.CANNON_WIDTH * Main.Constants.MAP_SCALING * 2);
        turretBody.setFill(Color.GREEN);
        turretBody.getTransforms().add(
                new Translate(ocean.getWidth()/2 - turretBody.getRadius(),ocean.getHeight()/2 - turretBody.getRadius())
        );

        super.getChildren().add(turretBody);

        boats = new Rectangle[4];
        boatsTranslate = new Translate[4];
        List<MovableObject> movableObjects = MyAnimationTimer.getMyAnimationTimerRef().getMovableObjects();
        int index = 0;
        for(int i = 0; i < movableObjects.size(); i++){
            if(movableObjects.get(i) instanceof Boat){
                boats[index] = new Rectangle(Main.Constants.BOAT_DEPTH * Main.Constants.MAP_SCALING * 1, Main.Constants.BOAT_HEIGHT * Main.Constants.MAP_SCALING * 1, Color.GREY);

                Double distance = ((Boat) movableObjects.get(i)).getDistance() * Main.Constants.MAP_SCALING * 2;
                Double angle = ((Boat) movableObjects.get(i)).getAngle();
                boats[index].getTransforms().addAll(
                        new Rotate(angle,ocean.getWidth()/2 - boats[index].getWidth()/2, ocean.getHeight()/2 - boats[index].getHeight()/2),
                    new Translate(distance*Main.Constants.MAP_SCALING,0),

                    new Translate(ocean.getWidth()/2 - boats[index].getWidth()/2 - turretBody.getRadius(), ocean.getHeight()/2 - boats[index].getHeight()/2 - turretBody.getRadius())
                );
                super.getChildren().add(boats[index]);
                index++;
            }
        }

    }

}
