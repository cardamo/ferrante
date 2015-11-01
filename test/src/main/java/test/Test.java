package test;

import ru.cardamo.apt.ferrante.api.GuavaFunction;

public class Test {
    @GuavaFunction
    String getOne() {
        return "one";
    }

    @GuavaFunction
    void getVoid() {
    }

    @GuavaFunction
    String getByArg(Integer arg) {
        return String.valueOf(arg);
    }

    @GuavaFunction
    static void staticVoid() {
    }

    @GuavaFunction
    static void staticVoidSingleArg(String arg) {
    }

    @GuavaFunction
    static Integer staticSingleArg(String arg) {
        return 0;
    }

    @GuavaFunction
    static Integer staticBiArg(String a1, String a2) {
        return 0;
    }

//    static class StaticInner {
//        @GuavaFunction
//        String getByArgStaticInner(Integer arg) {
//            return String.valueOf(arg);
//        }
//    }
//
//    class Inner {
//        @GuavaFunction
//        String getByArgInner(Integer arg) {
//            return String.valueOf(arg);
//        }
//    }
}
