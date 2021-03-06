package main;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import objects.Cannon;
import timer.MyAnimationTimer;

public class CameraController implements EventHandler<KeyEvent> {

    private Scene scene;
    private MyAnimationTimer timer;

    private Camera fixedCamera; //main.Main camera
    private Camera cannonCamera; //Cannon camera
    private Camera[] boatCamera; //Each boat has one camera
    private Camera activeBoatCamera; //Currently active boat camera
    private Camera cannonBallCamera; //Cannon ball camera
    private Camera upCamera; //Sky View


    public CameraController(double nearClip, double farClip, Scene scene, MyAnimationTimer timer){
        this.scene = scene;
        this.timer = timer;

        //main.Main camera
        this.fixedCamera = new PerspectiveCamera(true);
        this.fixedCamera.setNearClip(nearClip);
        this.fixedCamera.setFarClip(farClip);
        scene.setCamera(this.fixedCamera);

        //Cannon camera
        this.cannonCamera = new PerspectiveCamera(true);
        this.cannonCamera.setNearClip(nearClip);
        this.cannonCamera.setFarClip(farClip);

        //Boat cameras
        this.boatCamera = new Camera[4];
        for(int i = 0; i < boatCamera.length; i++){
            boatCamera[i] = new PerspectiveCamera(true);
            boatCamera[i].setNearClip(nearClip);
            boatCamera[i].setFarClip(farClip);
            boatCamera[i].getTransforms().addAll(
                    new Translate(0 , -3 * Main.Constants.BOAT_HEIGHT, 6 * Main.Constants.BOAT_DEPTH),
                    new Rotate(-180, Rotate.Y_AXIS)
            );
        }

        activeBoatCamera =null;

        //CannonBall camera
        this.cannonBallCamera = new PerspectiveCamera(true);
        this.cannonBallCamera.setNearClip(nearClip);
        this.cannonBallCamera.setFarClip(farClip);

        //Setting up the camera-----------------------------------------------------------------------------------------
        /*this.cannonBallCamera.getTransforms().addAll(
                new Rotate(30, Rotate.X_AXIS),
                new Translate(0 , 10, -300)
        );*/

        this.upCamera = new PerspectiveCamera(true);
        this.upCamera.setNearClip(nearClip);
        this.upCamera.setFarClip(farClip);
        this.upCamera.getTransforms().addAll(
                new Rotate ( -90, Rotate.X_AXIS ),
                new Translate ( 0, 0, Main.Constants.CAMERA_Z )
        );

    }

    public Camera getFixedCamera(){
        return fixedCamera;
    }

    public Camera getCannonCamera(){
        return cannonCamera;
    }

    public Camera getBoatCamera(int boatID){
        return boatCamera[boatID];
    }

    public Camera getCannonBallCamera(){
        return cannonBallCamera;
    }

    public Camera getUpCamera(){
        return upCamera;
    }

    public boolean cannonBallCameraOn(){
        return this.scene.getCamera() == this.cannonBallCamera;
    }

    public void setAnimationTimer(MyAnimationTimer timer){
        this.timer = timer;
    }

    @Override
    public void handle(KeyEvent event) {
        if(event.getEventType().equals(KeyEvent.KEY_RELEASED))
            return;

        //Works only on key press and on key hold
        switch (event.getCode()){
            //Selection of cameras
            case DIGIT0:
            case NUMPAD0: {
                this.scene.setCamera(this.fixedCamera);
                Cannon.getCannonRefernce().switchAmmoDisplay(true);
                activeBoatCamera = null;
                break;
            }
            case DIGIT5:
            case NUMPAD5:{
                this.scene.setCamera(this.cannonCamera);
                Cannon.getCannonRefernce().switchAmmoDisplay(false);
                activeBoatCamera = null;
                break;
            }
            case SPACE:{
                if(timer.getWeapon() != null){
                    this.scene.setCamera(this.cannonBallCamera);
                }else {
                    System.out.println("There is no bullet!");
                }
                break;
            }
            case DIGIT1:
            case NUMPAD1:{
                this.scene.setCamera(this.boatCamera[0]);
                activeBoatCamera = this.boatCamera[0];
                Cannon.getCannonRefernce().hideAmmoCount();
                break;
            }
            case DIGIT2:
            case NUMPAD2:{
                this.scene.setCamera(this.boatCamera[1]);
                activeBoatCamera = this.boatCamera[1];
                Cannon.getCannonRefernce().hideAmmoCount();
                break;
            }
            case DIGIT3:
            case NUMPAD3:{
                this.scene.setCamera(this.boatCamera[2]);
                activeBoatCamera = this.boatCamera[2];
                Cannon.getCannonRefernce().hideAmmoCount();
                break;
            }
            case DIGIT4:
            case NUMPAD4:{
                this.scene.setCamera(this.boatCamera[3]);
                activeBoatCamera = this.boatCamera[3];
                Cannon.getCannonRefernce().hideAmmoCount();
                break;
            }
            //Sky camera
            case DIGIT9:{
                this.scene.setCamera(this.upCamera);
                activeBoatCamera = null;
                Cannon.getCannonRefernce().hideAmmoCount();
                break;
            }
            //Movement of boat camera
            case LEFT:{
                if(activeBoatCamera != null){
                    double newX = activeBoatCamera.getTranslateX() + 5;
                    activeBoatCamera.setTranslateX(newX);
                    break;
                }
            }
            case RIGHT:{
                if(activeBoatCamera != null){
                    double newX = activeBoatCamera.getTranslateX() - 5;
                    activeBoatCamera.setTranslateX(newX);
                    break;
                }
            }
            case UP:{
                if(activeBoatCamera != null){
                    double newY = activeBoatCamera.getTranslateY() - 5;
                    activeBoatCamera.setTranslateY(newY);
                    break;
                }
            }
            case DOWN:{
                if(activeBoatCamera != null){
                    double newY = activeBoatCamera.getTranslateY() + 5;
                    activeBoatCamera.setTranslateY(newY);
                    break;
                }
            }
            case PAGE_UP:{
                if(activeBoatCamera != null){
                    double newZ = activeBoatCamera.getTranslateZ() - 5;
                    activeBoatCamera.setTranslateZ(newZ);
                    break;
                }
            }
            case PAGE_DOWN:{
                if(activeBoatCamera != null){
                    double newZ = activeBoatCamera.getTranslateZ() + 5;
                    activeBoatCamera.setTranslateZ(newZ);
                    break;
                }
            }
            case T:{
                Map.getMapRef().toggleShow();
                break;
            }
            default:{
                System.out.println(event.getCode());
                break;
            }
        }
    }
}
