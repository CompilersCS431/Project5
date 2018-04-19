package ProjFive;

import ProjFive.lexer.*;
import ProjFive.node.*;
import ProjFive.parser.*;
import java.io.*;

public class Main{

   public static void main(String[] arguments){
      for(int i= 0; i < arguments.length; i+=2) {
      
      try{
          //  BufferedWriter write = new BufferedWriter(new FileWriter(arguments[i+1]));
            Lexer lexer = new Lexer(new PushbackReader
                  (new InputStreamReader(new FileInputStream(arguments[i])), 1024));

			
            Parser parser = new Parser(lexer);	
            Start ast = parser.parse();
			System.out.println("***PARSING***") ;
			PrintTree t = new PrintTree();
			System.out.println("***APPLYING T***") ;
			ast.apply(t);
			//ast.apply(t);
            //write.write("It's valid!");
            //write.close();
			System.out.println("I am not error") ;
      }
      catch(Exception e){ 
			System.out.println("I am error") ;
			System.out.println("NOT VALID: " + e.getMessage());
			/*try {
				BufferedWriter write = new BufferedWriter(new FileWriter(arguments[i+1]));
				write.write("NOT VALID: " + e.getMessage());
				write.close();
			}
			catch(Exception q) {
				System.out.println("Can't find file.");
			}
		*/	
	   }
   }
}
			
}
