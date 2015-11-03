package test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ru.cardamo.apt.ferrante.api.GuavaFunction;

public class Test {

    Function<ImmutableSet<Integer>, ImmutableList<String>> list = Test_f.getList;

    @GuavaFunction
    String getOne() {
        return "one";
    }

    @GuavaFunction
    static ImmutableList<String> getList(ImmutableSet<Integer> set) {
        return ImmutableList.of();
    }

    @GuavaFunction
    void getVoid() {
    }

    @GuavaFunction
    static void staticVoidSingleArg(String arg) {
    }

    @GuavaFunction
    static Integer staticSingleArg(String arg) {
        return 0;
    }

    // these are invalid

    @GuavaFunction
    static void staticVoid() {
    }

    @GuavaFunction
    String getByArg(Integer arg) {
        return String.valueOf(arg);
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
