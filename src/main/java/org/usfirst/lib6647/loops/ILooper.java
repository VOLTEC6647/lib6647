package org.usfirst.lib6647.loops;

/**
 * Interface for {@link Looper}. Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/ILooper.java.
 */
public interface ILooper {
	/**
	 * Used to add {@link Loop Loops} to a List, in order to be later run depending
	 * on its {@link LoopType}.
	 * 
	 * @param loops
	 */
	void register(Loop... loops);
}