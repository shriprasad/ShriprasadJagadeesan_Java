package parser;

import java.io.IOException;

/**
 * Created by Prasad on 21-09-2018.
 */
@FunctionalInterface
public interface TransactionParser<T,X extends IOException> {

    T parse(T input,String path) throws X;



}
