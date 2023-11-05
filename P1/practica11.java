import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica11
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^\\+34\\s[8-9][1-8]\\s[0-9]{7}$");
        Matcher mat = pat.matcher("+44 95 6030466");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}