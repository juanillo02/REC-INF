import java.io.*;
public class practica19
{
    public static void main(String [] arg)
    {
        try
        {
            File archivo =  new File ("EjercicioExpresiones.txt");
            FileReader fr = new FileReader (archivo);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            String nuevalinea = "";
            while((linea=br.readLine())!=null)
            {
                nuevalinea += linea + "\n";
            }
            fr.close();

            FileWriter wr = new FileWriter (archivo);
            nuevalinea = nuevalinea.toUpperCase();
            wr.write(nuevalinea);
            wr.close();

        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
}