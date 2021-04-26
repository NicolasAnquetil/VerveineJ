package lambdas;

/**
 * Some bugs discovered while parsing JUnit5 project
 */
public class Junit5Bugs {

	public void bug2() {
		new LinkedHashMap<>() {{
			/* blah */
		}};
	}
}