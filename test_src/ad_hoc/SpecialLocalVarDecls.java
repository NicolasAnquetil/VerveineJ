package ad_hoc;

public class SpecialLocalVarDecls {
    Object aField;
    public void aMethod() {
        boolean firstVar = aField.invocation(
                new List() {{int secondVar = 0; }
                    public void anotherMethod() {
                        while (true) {
                            int thirdVar = 0;
                            if (thirdVar > 0) {
                                System.out.println(thirdVar-1);
                            }
                            else {
                                System.out.println(thirdVar + 1);
                            }
                        }
                    }
                }
        );
        if (firstVar) {
            System.out.println("true");
        }
        else {
            System.out.println("false");
        }
    }
}