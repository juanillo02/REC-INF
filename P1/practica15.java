import java.util.regex.Pattern;
public class practica15
{
    private static final String REGEX = "<.*?>(.*?)<\\/.*?>";
    private static final String INPUT =
    "<a>uno</a><b>dos</b><c>tres</c><d>cuatro</d><e>cinco</e>";
    public static void main(String[] args) 
    {
        Pattern p = Pattern.compile(REGEX);
        String[] items = p.split(INPUT);
        for(String s : items)
        {
            System.out.println(s);
        }
    }
}