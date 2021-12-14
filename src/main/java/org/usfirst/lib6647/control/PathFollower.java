package org.usfirst.lib6647.control;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.lib6647.util.ControlUtil;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import net.jafama.FastMath;

public class PathFollower {
	// TODO: Comment this.

	public int lastPointUsed = 0;
	public double lastLookAheadUsed = -1.0;
	public Translation2d lastLookAheadPoint = null;

	public int GetClosestPoint(List<Translation2d> path, Pose2d currentPose) {
		var nearestPoint = lastPointUsed;
		var distanceMin = Double.MAX_VALUE;

		for (int i = lastPointUsed; i < path.size(); i++) {
			var dist = path.get(i).getDistance(currentPose.getTranslation());

			if (dist < distanceMin) {
				nearestPoint = i;
				distanceMin = dist;
			}
		}

		if (lastPointUsed != nearestPoint) {
			lastPointUsed = nearestPoint;
			lastLookAheadUsed = -1.0; // restea el porcentaje lookahead point
		}

		return lastPointUsed;
	}

	public Translation2d getLookAheadPoint(List<Translation2d> path, double lookAheadDistance, Pose2d currentPose) {
		for (int i = lastPointUsed; i < path.size() - 1; i++) {
			var E = path.get(i); // the starting point of the line segment
			var L = path.get(i + 1); // the end point of the line segment
			var C = currentPose.getTranslation(); // (robot location)
			var r = lookAheadDistance; // (lookahead distance)

			var d = L.minus(E);
			var f = E.minus(C);

			var a = ControlUtil.dotProduct(d, d);
			var b = 2 * ControlUtil.dotProduct(f, d);
			var c = ControlUtil.dotProduct(f, f) - (r * r);

			var discriminant = (b * b) - (4.0 * a * c);
			var intersection = -1.0;

			if (discriminant < 0.0) {
				// no intersection
			} else {
				// ray didn't totally miss sphere,
				// so there is a solution to
				// the equation.
				discriminant = FastMath.sqrt(discriminant);
				// either solution may be on or off the ray so need to test both
				// t1 is always the smaller value, because BOTH discriminant and
				// a are nonnegative.
				var t1 = (-b - discriminant) / (2.0 * a);
				var t2 = (-b + discriminant) / (2.0 * a);
				// 3x HIT cases:
				// -o-> --|--> | | --|->
				// Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

				// 3x MISS cases:
				// -> o o -> | -> |
				// FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)
				if (t1 >= 0.0 && t1 <= 1.0) { // intersection
					// t1 is the intersection, and it's closer than t2
					// (since t1 uses -b - discriminant)
					// Impale, Poke
					intersection = t1;

					// here t1 didn't intersect so we are either started
					// inside the sphere or completely past it
				} else if (t2 >= 0.0 && t2 <= 1.0) { // intersection
					// ExitWound
					intersection = t2;
				}
				// no intersection
			}

			if (intersection >= 0.0 && intersection <= 1.0 && intersection > lastLookAheadUsed) {
				lastLookAheadUsed = intersection;
				d.times(intersection);
				lastLookAheadPoint = E.plus(d);

				return (lastLookAheadPoint); // intersection point
			}
		}
		return lastLookAheadPoint;
	}

	public double curvatureArc(Translation2d lookAheadPoint, Pose2d currentPose) {
		var L = currentPose.getTranslation().getDistance(lookAheadPoint);
		var a = FastMath.tan(currentPose.getRotation().getDegrees() * (Math.PI / 180)) * -1.0;

		var b = 1.0;
		var c = (FastMath.tan(currentPose.getRotation().getDegrees() * (Math.PI / 180))
				* currentPose.getTranslation().getX()) - currentPose.getTranslation().getY();

		var x = Math.abs((a * lookAheadPoint.getX()) + (b * lookAheadPoint.getY()) + c)
				/ (FastMath.sqrt((a * a) + (b * b)));

		var sign = 1.0;
		var signednum = (FastMath.sin(currentPose.getRotation().getDegrees() * (Math.PI / 180))
				* (lookAheadPoint.getX() - currentPose.getTranslation().getX()))
				- (FastMath.cos(currentPose.getRotation().getDegrees() * (Math.PI / 180))
						* (lookAheadPoint.getY() - currentPose.getTranslation().getY()));
		if (signednum < 0.0)
			sign = -1.0;

		var curvature = ((2.0 * x) / (L * L)) * sign;
		return (curvature);
	}

	public List<Double> calculateVelocity(double targetVelocity, double arcCurvatureLookAhead, double trackWidth,
			double maxVelocity) {
		List<Double> values = new ArrayList<>();
		values.add((targetVelocity * (2.0 + (arcCurvatureLookAhead * trackWidth))) / 2.0); // left
		values.add((targetVelocity * (2.0 - (arcCurvatureLookAhead * trackWidth))) / 2.0); // right

		if (Math.abs(values.get(0)) > Math.abs(maxVelocity) || Math.abs(values.get(1)) > Math.abs(maxVelocity)) {
			if (Math.abs(values.get(0)) > Math.abs(values.get(1))) {
				double ratio = Math.abs(maxVelocity / values.get(0));
				values.set(0, maxVelocity);
				if (values.get(0) < 0.0) {
					values.set(0, values.get(0) * -1.0);
				}
				values.set(1, values.get(1) * ratio);
			} else {
				double ratio = Math.abs(maxVelocity / values.get(1));
				values.set(1, maxVelocity);
				if (values.get(1) < 0.0) {
					values.set(1, values.get(1) * -1.0);
				}
				values.set(0, values.get(0) * ratio);
			}
		}

		return values;
	}
}