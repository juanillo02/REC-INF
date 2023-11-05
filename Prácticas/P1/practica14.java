import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class practica14
{
    public static void main(String [] arg)
    {
        try
        {
            File archivo =  new File ("portal.html");
            FileReader fr = new FileReader (archivo);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            int cont=0;
            Pattern pat = Pattern.compile("^.*<img.*");
            Matcher mat;
            while((linea=br.readLine())!=null)
            {
                mat = pat.matcher(linea);
                if(mat.matches())
                {
                    cont++;
                }
            }
            System.out.println("Hay " + cont + " imágenes.");
            fr.close();
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
}