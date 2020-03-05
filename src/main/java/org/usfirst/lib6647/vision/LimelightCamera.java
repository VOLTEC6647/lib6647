package org.usfirst.lib6647.vision;

import org.usfirst.lib6647.vision.LimelightControlModes.CamMode;
import org.usfirst.lib6647.vision.LimelightControlModes.LEDMode;
import org.usfirst.lib6647.vision.LimelightControlModes.SnapshotMode;
import org.usfirst.lib6647.vision.LimelightControlModes.StreamMode;
import org.usfirst.lib6647.vision.LimelightData.Data;
import org.usfirst.lib6647.vision.LimelightData.RawCrosshair;
import org.usfirst.lib6647.vision.LimelightData.RawData;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Wrapper for simple usage of a Limelight camera.
 * <p>
 * Originally copied over from:
 * https://github.com/Spectrum3847/Ultraviolet-2020/blob/master/src/main/java/frc/lib/drivers/LimeLight.java
 */
public class LimelightCamera {

	// TODO: Comment this properly.

	private String name;
	private NetworkTable table;
	private boolean isConnected = false;
	private double heartBeatPeriod = 0.1;

	Notifier heartBeat = new Notifier(new Runnable() {
		@Override
		public void run() {
			resetPipelineLatency();

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			isConnected = getData(Data.PIPELINE_LATENCY) != 0.0;
		}
	});

	public LimelightCamera(String name) {
		this.name = name;
		table = NetworkTableInstance.getDefault().getTable(name);
		heartBeat.startPeriodic(heartBeatPeriod);
	}

	public String getName() {
		return name;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public NetworkTableEntry getDataEntry(Data type) {
		return table.getEntry(type.getName());
	}

	public NetworkTableEntry getRawDataEntry(RawData type, int contour) {
		return table.getEntry(type.getName() + MathUtil.clamp(contour, 0, 2));
	}

	public NetworkTableEntry getRawCrosshairEntry(RawCrosshair type, int calibratedCrosshair) {
		return table.getEntry(type.getName() + MathUtil.clamp(calibratedCrosshair, 0, 1));
	}

	public double getData(Data type) {
		return getDataEntry(type).getDouble(0.0);
	}

	public double getRawData(RawData type, int contour) {
		return table.getEntry(type.getName() + MathUtil.clamp(contour, 0, 2)).getDouble(0.0);
	}

	public double getRawCrosshair(RawCrosshair type, int calibratedCrosshair) {
		return table.getEntry(type.getName() + MathUtil.clamp(calibratedCrosshair, 0, 1)).getDouble(0.0);
	}

	public boolean isTargetFound() {
		return getData(Data.TARGET_FOUND) != 0.0f;
	}

	private void resetPipelineLatency() {
		getDataEntry(Data.PIPELINE_LATENCY).setValue(0.0);
	}

	public void setLEDMode(LEDMode mode) {
		table.getEntry("ledMode").setValue(mode.getValue());
	}

	public double getLEDMode() {
		return table.getEntry("ledMode").getDouble(0.0);
	}

	public void setCamMode(CamMode mode) {
		table.getEntry("camMode").setValue(mode.getValue());
	}

	public double getCamMode() {
		return table.getEntry("camMode").getDouble(0.0);
	}

	public void setStream(StreamMode mode) {
		table.getEntry("stream").setValue(mode.getValue());
	}

	public double getStream() {
		return table.getEntry("stream").getDouble(0.0);
	}

	public void setSnapshot(SnapshotMode mode) {
		table.getEntry("snapshot").setValue(mode.getValue());
	}

	public double getSnapshot() {
		return table.getEntry("snapshot").getDouble(0.0);
	}

	public void setPipeline(int pipeline) {
		table.getEntry("pipeline").setValue(MathUtil.clamp(pipeline, 0, 9));
	}

	public double getPipeline() {
		return table.getEntry("pipeline").getDouble(0.0);
	}
}