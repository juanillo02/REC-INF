import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica12
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^[A-Z]((#|-)\\d{1,2}\\-\\d{4}|\\s\\d{2}(-)\\d{5}|\\s\\d{6}|#\\s\\d{2}\\s\\d{4})$");
        Matcher mat = pat.matcher("P 76-96785");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}