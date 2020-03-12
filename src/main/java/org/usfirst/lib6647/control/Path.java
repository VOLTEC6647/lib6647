package org.usfirst.lib6647.control;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.geometry.Translation2d;

public abstract class Path {
	// TODO: Comment this.

	protected PathSegment pathSegment = null;

	protected class DataHolder {
		private Translation2d position;
		private String marker;

		public DataHolder(Translation2d position, String marker) {
			this.position = position;
			this.marker = marker;
		}
	}

	public void startPath() {
		pathSegment = new PathSegment();
	}

	public void buildPath(List<DataHolder> allData) {
		List<Translation2d> path = new ArrayList<>();
		List<Markers> markers = new ArrayList<>();

		for (DataHolder data : allData) {
			path.add(data.position);
			markers.add(new Markers(data.marker, 0));
		}

		if (pathSegment == null)
			pathSegment = new PathSegment();

		pathSegment.generate(path, markers);
	}
}