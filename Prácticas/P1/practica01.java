import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica1
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^abc.*");
        Matcher mat = pat.matcher("Abcdefg");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}
