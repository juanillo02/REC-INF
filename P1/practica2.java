import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica2
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^(a|A)bc.*");
        Matcher mat = pat.matcher("zabcdefg");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}
