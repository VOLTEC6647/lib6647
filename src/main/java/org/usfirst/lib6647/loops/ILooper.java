package org.usfirst.lib6647.loops;

/**
 * Interface for {@link Looper}. Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/ILooper.java
 */
public interface ILooper {
	/**
	 * Used to add {@link Loop Loops} to a List, depending on its {@link LoopType
	 * type}, to be later run.
	 * 
	 * @param loop
	 */
	void register(Loop loop);
}