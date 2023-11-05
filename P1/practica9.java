import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica9
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^(([1-9]|(1|2)[0-9]|3[0-1])\\/([1-9]|1[0-2])\\/([1-9][0-9]|[1-9]))$");
        Matcher mat = pat.matcher("25-10/83");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}