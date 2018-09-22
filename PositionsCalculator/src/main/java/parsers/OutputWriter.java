package parsers;

/**
 * Created by Prasad on 21-09-2018.
 */
@FunctionalInterface
public interface OutputWriter<T,X extends Exception> {

    boolean write(T t,String outputPath) throws X;

}
