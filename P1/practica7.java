import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica7
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("([a-z]|[A-Z]){5,10}");
        Matcher mat = pat.matcher("reetRaactil");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}