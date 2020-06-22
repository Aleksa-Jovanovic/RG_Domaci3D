package objects.movable;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import main.Main;

public class Boat extends MovableObject {

    private double width;
    private double height;
    private double depth;

    private  double angle;
    private double distance;
    private  double speed;

    private Group parent;
    private Group boat;


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

	private Group makeBoat(double width, double height, double depth){
		float xUnit = (float) (width/ 15);
		float yUnit = (float) (height / 15);
		float zUnit = (float) (depth / 47);

		Group boat = new Group();

		Box bottomBox = new Box ( width, yUnit * 8, zUnit * 35 );
		bottomBox.setMaterial (
				new PhongMaterial ( Color.LIGHTGRAY )
		);
		boat.getChildren().add(bottomBox);

		Box topBox = new Box( xUnit * 10, yUnit * 7, zUnit * 14);
		topBox.setMaterial(new PhongMaterial(Color.DARKGRAY));
		topBox.getTransforms().add(
				new Translate(0, -(height) / 2, (zUnit * 35) / 2 - zUnit * 7 - 4 * zUnit)
		);
		boat.getChildren().add(topBox);

		//Mesh part
		float points[] = {
				0.0f, -yUnit * 4.0f, -(12.0f + 35.0f / 2) * zUnit, //Vrh nosa ----- 0
				7.5f * xUnit, -yUnit * 4.0f, -(35.0f / 2) * zUnit, //Gore levo ---- 1
				7.5f * xUnit, yUnit * 4.0f, -(35.0f / 2) * zUnit, //Dole levo ------------- 2
				-7.5f * xUnit, -yUnit * 4.0f, -(35.0f / 2) * zUnit, //Gore desno -- 3
				-7.5f * xUnit, yUnit * 4.0f, -(35.0f / 2) * zUnit //Dole desno ------------ 4
		};
		float texels[] = {
				0.5f, 0.5f
		};
		int faces[] = {
			3, 0, 0, 0, 1, 0,
				2, 0, 0, 0, 4, 0,
				3, 0, 4, 0, 0, 0,
				2, 0, 1, 0, 0, 0
		};

		TriangleMesh triangleMesh = new TriangleMesh();
		triangleMesh.getPoints().addAll(points);
		triangleMesh.getTexCoords().addAll(texels);
		triangleMesh.getFaces().addAll(faces);

		MeshView meshView = new MeshView(triangleMesh);
		meshView.setDrawMode(DrawMode.FILL);
		meshView.setMaterial(new PhongMaterial(Color.LIGHTGRAY));

		boat.getChildren().add(meshView);

		return boat;
	}

	public Boat ( Group parent, double width, double height, double depth, Color color, double distance, double angle, double speed, Camera camera, double destination, double delta ) {
		super (
				parent,
				Boat.getPosition ( angle, distance ),
				Boat.getSpeed ( angle, distance, speed ),
				new Point3D ( 0, 0, 0 ),
				Boat.getBoatDestination ( angle, destination + depth / 2, delta )
		);

        this.width = width;
        this.depth = depth;
        this.height = height;

        this.speed = speed;
        this.angle = angle;
        this.distance = distance;

        this.parent = parent;
		
		this.boat = this.makeBoat(width, height, depth);
		boat.getTransforms().add(
				new Translate(0, - height / 4, 0)
		);

		super.getChildren ( ).addAll ( boat );

		//Setting up the camera
		super.getChildren().add(camera);
	}

	public void increaseSpeed(){
		this.speed += Main.Constants.BOAT_SPEED_INCREASE;
		Point3D newSpeed = this.getSpeed(this.angle, this.distance, this.speed);
		super.setSpeed(newSpeed);
	}

	public double getAngle(){
		return angle;
	}
	public double getDistance(){
		return distance;
	}

	@Override
    public void onCollision ( ) {
        TranslateTransition sink = new TranslateTransition(Duration.seconds(5),this);
        sink.setByY(this.height / 2);
        sink.play();
        sink.setOnFinished(e ->this.parent.getChildren ( ).remove ( this ) );
    }

    @Override
	public boolean handleCollision ( MovableObject other ) {
		if ( this.getTransformedBounds (boat).intersects ( other.getTransformedBounds ( ) ) ) {
			this.onCollision ( );
			return true;
		} else {
			return false;
		}
	}

}
