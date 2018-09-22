package parser;

import java.io.IOException;

/**
 * Created by Prasad on 21-09-2018.
 */
@FunctionalInterface
public interface InputParser<T, X extends IOException>  {

    T parse(String path) throws X;


}

