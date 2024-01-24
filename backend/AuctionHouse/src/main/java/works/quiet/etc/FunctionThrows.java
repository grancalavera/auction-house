package works.quiet.etc;

@FunctionalInterface
public interface FunctionThrows<T, R, E extends Exception> {
    R apply(T t) throws E;
}
