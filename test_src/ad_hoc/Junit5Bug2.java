package ad_hoc;

/**
 * A bug discovered while parsing JUnit5 project
 */
public class Junit5Bug1 {

	public void bug2() {
		new LinkedHashMap<>() {{
			/* blah */
		}};
	}
}