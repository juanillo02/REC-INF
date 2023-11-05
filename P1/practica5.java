import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica5
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("^(a|l)+$");
        Matcher mat = pat.matcher("la maria se está comiendo un pan");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}
