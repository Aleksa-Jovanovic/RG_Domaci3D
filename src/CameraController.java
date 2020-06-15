import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class CameraController implements EventHandler<KeyEvent> {

    private Scene scene;
    private Camera fixedCamera; //Main camera
    private Camera cannonCamera; //Cannon camera

    public CameraController(double nearClip, double farClip, Scene scene){
        this.scene = scene;

        this.fixedCamera = new PerspectiveCamera(true);
        this.fixedCamera.setNearClip(nearClip);
        this.fixedCamera.setFarClip(farClip);
        scene.setCamera(this.fixedCamera);

        this.cannonCamera = new PerspectiveCamera(true);
        this.cannonCamera.setNearClip(nearClip);
        this.cannonCamera.setFarClip(farClip);
    }

    public Camera getFixedCamera(){
        return fixedCamera;
    }

    public Camera getCannonCamera(){
        return cannonCamera;
    }

    @Override
    public void handle(KeyEvent event) {
        switch (event.getCode()){
            case DIGIT0:
            case NUMPAD0: {
                this.scene.setCamera(this.fixedCamera);
                break;
            }
            case DIGIT5:
            case NUMPAD5:{
                this.scene.setCamera(this.cannonCamera);
                break;
            }
        }
    }
}
