package org.usfirst.lib6647.control;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.lib6647.util.ControlUtil;

import edu.wpi.first.wpilibj.geometry.Translation2d;

public class PathSegment {
	// TODO: Comment this.

	public List<Markers> markers;
	public List<Translation2d> points;
	public List<Double> curvatures;
	public List<Double> velocities;

	public double maxVelAccelLimiter = 1;

	// TODO: Need to adjust these.
	public double pointsSpacing = 0.15; // en metros
	public double weightRealPoints = 0.983; // peso de los puntos reales contra los smooth
	public double smoothTolerance = 0.01; // smooth tolerance para el smoother
	public double curvatureVelLimiter = 2; // que tan rapido queremos que
											// vaya en las curvas(1-5)
	public double lookAheadDistance = 0.5; // en metros

	public double driveMaxVelocity = 0.0;
	public double driveMaxAccel = 0.0;
	// ...

	public double slowingDownRampDistance = 1;
	public double slowingDownRate = 0.85;

	public PathSegment() {
		// TODO: Modify fields; make them private.
	}

	public void generate(final List<Translation2d> path, final List<Markers> markers) {
		points = calculatePoints(path, markers);
		points = smooth(points, weightRealPoints, (1 - weightRealPoints), smoothTolerance);
		curvatures = getCurvatures(points);
		velocities = getVelocities(points, curvatures, driveMaxVelocity * maxVelAccelLimiter,
				driveMaxAccel * maxVelAccelLimiter);
	}

	private List<Translation2d> calculatePoints(final List<Translation2d> path, final List<Markers> passedMarkers) {
		List<Translation2d> newPath = new ArrayList<>();
		markers = new ArrayList<>();

		for (int i = 0; i < path.size() - 1; i++) {
			markers.add(new Markers(passedMarkers.get(i).marker, newPath.size()));

			var vector = new Translation2d(path.get(i + 1).getX() - path.get(i).getX(),
					path.get(i + 1).getY() - path.get(i).getY());
			final var numExtraPoints = (int) Math.ceil(ControlUtil.getMagnitude(vector) / pointsSpacing);

			vector = ControlUtil.normalize(vector);
			vector.times(pointsSpacing);

			for (int j = 0; j < numExtraPoints; j++)
				newPath.add(new Translation2d((path.get(i).getX() + (vector.getX() * j)),
						(path.get(i).getY() + (vector.getY() * j))));
		}

		newPath.add(path.get(path.size() - 1));
		return newPath;
	}

	private List<Translation2d> smooth(final List<Translation2d> path, final double weightData,
			final double weightSmooth, final double tolerance) {
		List<Translation2d> newPath = new ArrayList<>(path);
		var change = tolerance;

		while (change >= tolerance) {
			change = 0.0;
			for (int i = 1; i < newPath.size() - 1; i++) {
				var auxX = newPath.get(i).getX();
				var auxY = newPath.get(i).getY();

				newPath.set(i,
						new Translation2d(auxX + (weightData * (path.get(i).getX() - auxX)
								+ weightSmooth * (newPath.get(i - 1).getX() + newPath.get(i + 1).getX() - 2.0 * auxX)),
								auxY));
				change += Math.abs(auxX - newPath.get(i).getX());

				auxX = newPath.get(i).getX(); // Update auxX variable.

				newPath.set(i, new Translation2d(auxY, auxY + (weightData * (path.get(i).getY() - auxY)
						+ weightSmooth * (newPath.get(i - 1).getY() + newPath.get(i + 1).getY() - 2.0 * auxY))));
				change += Math.abs(auxY - newPath.get(i).getY());
			}
		}

		return newPath;
	}

	private List<Double> getCurvatures(List<Translation2d> path) {
		List<Double> curvatures = new ArrayList<Double>();
		curvatures.add(0.0);

		for (int i = 1; i < path.size() - 1; i++) {
			var auxX = path.get(i).getX();
			var auxY = path.get(i).getY();

			if (path.get(i).getX() == path.get(i - 1).getX()) {
				path.set(i, new Translation2d(auxX + 0.001, auxY));

				auxX = path.get(i).getX();
				auxY = path.get(i).getY();
			}

			var k1 = (0.5 * ((auxX * auxX) + (auxY * auxY) - (path.get(i - 1).getX() * path.get(i - 1).getX())
					- (path.get(i - 1).getY() * path.get(i - 1).getY()))) / (auxX - path.get(i - 1).getX());
			var k2 = (auxY - path.get(i - 1).getY()) / (auxX - path.get(i - 1).getX());
			var b = (0.5 * ((path.get(i - 1).getX() * path.get(i - 1).getX()) - (2 * path.get(i - 1).getY() * k1)
					+ (path.get(i - 1).getY() * path.get(i - 1).getY())
					- (path.get(i + 1).getX() * path.get(i + 1).getX()) + (2.0 * path.get(i + 1).getX() * k1)
					- (path.get(i + 1).getY() * path.get(i + 1).getY())))
					/ ((path.get(i + 1).getX() * k2) - path.get(i + 1).getY() + path.get(i - 1).getY()
							- (path.get(i - 1).getX() * k2));
			var a = k1 - (k2 * b);
			var r = Math.sqrt(((path.get(i).getX() - a) * (path.get(i).getX() - a))
					+ ((path.get(i).getY() - b) * (path.get(i).getY() - b)));

			curvatures.add(Double.isNaN(1 / r) ? 0.0 : 1 / r);
		}

		curvatures.add(0.0);
		return curvatures;
	}

	private ArrayList<Double> getVelocities(List<Translation2d> path, List<Double> curvatures, double maxVel,
			double maxAccel) {
		var k = curvatureVelLimiter;
		List<Double> velocities = new ArrayList<>();

		for (int i = 0; i < path.size(); i++)
			velocities.add(0.0);

		velocities.set(path.size() - 1, 0.0);

		for (int i = path.size() - 2; i >= 0; i--) {
			var distance = path.get(i).getDistance(path.get(i + 1));
			var tempVel = maxVel;

			if (curvatures.get(i) != 0)
				tempVel = Math.min(maxVel, (k / curvatures.get(i)));

			velocities.set(i, Math.min(tempVel,
					Math.sqrt((velocities.get(i + 1) * velocities.get(i + 1)) + (2.0 * distance * maxAccel))));
		}

		int idealnum = (int) (slowingDownRampDistance / pointsSpacing);

		if (velocities.size() > idealnum) {
			for (int i = 0; i < idealnum; i++) {
				var index = (velocities.size() - 2 - i);
				velocities.set(index, (velocities.get(index) * ((slowingDownRate / idealnum) * (i + 1))));
			}
		} else {
			for (int i = 0; i < (velocities.size() - 1); i++) {
				var index = (velocities.size() - 2 - i);
				velocities.set(index,
						(velocities.get(index) * ((slowingDownRate / (velocities.size() - 1)) * (i + 1))));
			}
		}

		return new ArrayList<>(velocities);
	}
}