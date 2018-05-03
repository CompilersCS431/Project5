package ProjFive;

import ProjFive.lexer.*;
import ProjFive.node.*;
import ProjFive.parser.*;
import java.io.*;

public class Main{

   public static void main(String[] arguments){
  
    Lexer lexer = new Lexer(new PushbackReader(new InputStreamReader(System.in)));
      try{
          //  BufferedWriter write = new BufferedWriter(new FileWriter(arguments[i+1]));
            lexer = new Lexer(new PushbackReader
                  (new InputStreamReader(new FileInputStream("C:\\Users\\yangm89\\Desktop\\Project5-master\\Originaltestone.txt")), 1024));

      }
      catch(Exception e){ }
            Parser parser = new Parser(lexer);
            Start ast = new Start(); 
            try {
            ast = parser.parse();
             }
            catch(Exception e) { }
			//System.out.println("***PARSING***") ;
			PrintTree t = new PrintTree();
			//System.out.println("***APPLYING T***") ;
			ast.apply(t);
           
            //SecondPass s = new SecondPass(t.symbolTable);
                //    ast.apply(s);
            
                //    System.out.println(s.dataPart + s.codePart);
            
			//ast.apply(t);
            //write.write("It's valid!");
            //write.close();
			System.out.println("I am not error") ;
      
  /*    }
      catch(Exception e){ 
			System.out.println("I am error") ;
			System.out.println("NOT VALID: " + e.getMessage());
			try {
				BufferedWriter write = new BufferedWriter(new FileWriter(arguments[i+1]));
				write.write("NOT VALID: " + e.getMessage());
				write.close();
			}
			catch(Exception q) {
				System.out.println("Can't find file.");
			}
			
	   }  */
   } 
}
			
