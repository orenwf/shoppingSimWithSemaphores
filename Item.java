import java.util.Random;

public class Item {
    final String weight;

    Item() {
	Random l = new Random();
	int l_num = l.nextInt(100) + 1;
	if(l_num < 31) weight = "light";
	else weight = "very heavy";
    }

    String weight() { return weight; };
}

