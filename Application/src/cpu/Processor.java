package cpu;

public class Processor {
	private static int numCores;
	static
	{
		numCores = Runtime.getRuntime().availableProcessors();
	}
	public static int getNumCores()
	{
		return numCores;
	}
}