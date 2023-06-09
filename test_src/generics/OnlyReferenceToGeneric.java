package generics;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;


public class OnlyReferenceToGeneric {

    private class InnerClass {
        

        private class InnerInnerClass {
            private final HashMap<String, String> mappings;

            private void solve(final String st) {
                while (this.mappings.keySet().size() > 0) {
                    final String cle = this.mappings.keySet().iterator().next();
                    solve(cle, st);
                }
            }
        }

    } 

}
