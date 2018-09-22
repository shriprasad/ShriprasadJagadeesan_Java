package model;

/**
 * Created by Prasad on 19-09-2018.
 */
public enum AccountType {

    I,E;

   private AccountType() {
    }

    static AccountType  accountType(String accountType)
    {
         if(!( "I".equals(accountType) || "E".equals(accountType) ) )
             throw new  IllegalArgumentException("incorrect accounttype");

         return "I".equals(accountType)?I:E;


    }
}
