package lambdas;

public class WithLambda {

    private Set<String> segments = new TreeSet<>((String seg1, String seg2) -> {
        if (seg1.equals(seg2)) {
            return 0;
        } else {
            return 1;
        }
    });

    public WithLambda() {
        Collection<Object> col;
        boolean found = col.stream().anyMatch(t -> System.out.print("lambda!"));
    }
}