import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica8
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^www\\..*\\.es$");
        Matcher mat = pat.matcher("wwwhola.es");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}
