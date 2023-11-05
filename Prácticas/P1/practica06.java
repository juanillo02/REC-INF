import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica6
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile(".*2(?!6).*");
        Matcher mat = pat.matcher("26 es un numero ");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}
